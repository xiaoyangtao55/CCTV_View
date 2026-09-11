package com.cctv_view.ui.screens

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cctv_view.MainActivity
import com.cctv_view.data.PlayerType
import com.cctv_view.ui.components.*
import com.cctv_view.viewmodel.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlayerScreen(
    onOpenSettings: () -> Unit,
    viewModel: MainViewModel = viewModel(
        factory = MainActivity.ViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val cctvChannels = remember { viewModel.getCCTVChannels() }
    val localChannels = remember { viewModel.getLocalChannels() }

    // 防抖时间戳
    var lastKeyTime by remember { mutableLongStateOf(0L) }
    val keyDebounceMs = 150L // 按键防抖间隔

    // 菜单项
    val menuItems = remember {
        listOf(
            MenuItem(0, "刷新", { Icon(Icons.Default.Refresh, contentDescription = null) }) {
                viewModel.refreshPage()
                viewModel.hideAllOverlays()
            },
            MenuItem(1, "频道列表", { Icon(Icons.Default.List, contentDescription = null) }) {
                viewModel.toggleChannelList()
            },
            MenuItem(2, "播放/暂停", { Icon(Icons.Default.PlayArrow, contentDescription = null) }) {
                viewModel.hideAllOverlays()
            },
            MenuItem(3, "切换播放器", { Icon(Icons.Default.SwapHoriz, contentDescription = null) }) {
                // 切换播放器类型
                val newType = if (uiState.playerType == PlayerType.EXOPLAYER) {
                    PlayerType.WEBVIEW
                } else {
                    PlayerType.EXOPLAYER
                }
                viewModel.savePlayerType(newType)
                viewModel.hideAllOverlays()
                // 重新加载当前频道
                uiState.currentChannel?.let { viewModel.changeChannel(it) }
            },
            MenuItem(4, "设置", { Icon(Icons.Default.Settings, contentDescription = null) }) {
                viewModel.hideAllOverlays()
                onOpenSettings()
            }
        )
    }

    // 按键处理函数
    fun handleKeyDown(keyCode: Int): Boolean {
        val currentTime = System.currentTimeMillis()

        // 防抖检查（某些按键不需要防抖）
        val needsDebounce = when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> true
            else -> false
        }

        if (needsDebounce && currentTime - lastKeyTime < keyDebounceMs) {
            return true // 防抖，忽略重复按键
        }
        lastKeyTime = currentTime

        Log.d("PlayerScreen", "按键: $keyCode")

        return when {
            // 有错误显示时，按确认键重试
            uiState.showError && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) -> {
                viewModel.retryPlayback()
                true
            }

            // 频道列表显示时，不在这里处理（频道列表自己处理）
            uiState.showChannelList -> false

            // 菜单显示时，不在这里处理（菜单自己处理）
            uiState.showMenu -> false

            // 数字输入显示时
            uiState.showNumberInput -> {
                when (keyCode) {
                    in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                        val number = keyCode - KeyEvent.KEYCODE_0
                        viewModel.appendNumber(number)
                        true
                    }
                    KeyEvent.KEYCODE_BACK -> {
                        viewModel.clearNumberInput()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        // 确认立即切换
                        viewModel.appendNumber(-1) // 触发处理
                        true
                    }
                    else -> false
                }
            }

            // 正常播放状态
            else -> {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        viewModel.previousChannel()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        viewModel.nextChannel()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_LEFT,
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        // 左右键调出频道列表
                        viewModel.toggleChannelList()
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        // 确认键调出频道列表
                        viewModel.toggleChannelList()
                        true
                    }
                    KeyEvent.KEYCODE_MENU -> {
                        // 菜单键调出菜单
                        Log.d("PlayerScreen", "菜单键按下")
                        viewModel.toggleMenu()
                        true
                    }
                    KeyEvent.KEYCODE_BACK -> {
                        // 返回键：如果有浮层就关闭，否则不处理（让系统处理）
                        if (uiState.showChannelList || uiState.showMenu || uiState.showNumberInput) {
                            viewModel.hideAllOverlays()
                            true
                        } else {
                            false
                        }
                    }
                    in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                        val number = keyCode - KeyEvent.KEYCODE_0
                        viewModel.appendNumber(number)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    // 根布局 - 使用 Box 来处理按键焦点
    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    handleKeyDown(event.nativeKeyEvent.keyCode)
                } else {
                    false
                }
            }
    ) {
        // 播放器区域
        Box(modifier = Modifier.fillMaxSize()) {
            // 根据播放器类型选择播放方式
            if (uiState.playerType == PlayerType.EXOPLAYER) {
                // 原生 ExoPlayer 播放
                VideoPlayer(
                    channel = uiState.currentChannel,
                    onPlaybackStateChanged = { isPlaying, message ->
                        viewModel.onPlaybackStateChanged(isPlaying, message)
                    },
                    onError = { errorMsg ->
                        viewModel.onPlaybackError(errorMsg)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // WebView 备用播放
                TVWebView(
                    channel = uiState.currentChannel,
                    onPageFinished = { info -> viewModel.onPageFinished(info) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 加载中指示器
            if (uiState.isChangingChannel) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 6.dp,
                        color = Color.White
                    )
                }
            }

            // 错误提示
            if (uiState.showError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .clickable { viewModel.retryPlayback() },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.layout.Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "播放失败",
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = uiState.errorMessage,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.retryPlayback() },
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            Text("重新加载")
                        }
                        Text(
                            text = "按菜单键可切换播放器",
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }

            // 频道信息浮层
            ChannelOverlay(
                message = uiState.overlayMessage,
                isVisible = uiState.showOverlay && !uiState.showError,
                modifier = Modifier
            )

            // 数字输入浮层
            NumberInputOverlay(
                input = uiState.numberInputBuffer,
                isVisible = uiState.showNumberInput,
                modifier = Modifier
            )

            // 频道列表
            ChannelListDrawer(
                cctvChannels = cctvChannels,
                localChannels = localChannels,
                currentChannelId = uiState.currentChannel?.id ?: -1,
                selectedCategory = uiState.channelCategory,
                onCategorySelected = { viewModel.setChannelCategory(it) },
                onChannelSelected = {
                    viewModel.changeChannel(it)
                    viewModel.hideAllOverlays()
                    // 关闭后把焦点还给主界面
                    focusRequester.requestFocus()
                },
                onDismiss = {
                    viewModel.hideAllOverlays()
                    focusRequester.requestFocus()
                },
                isVisible = uiState.showChannelList
            )

            // 菜单
            if (uiState.showMenu) {
                MenuOverlay(
                    items = menuItems,
                    isVisible = true,
                    onDismiss = {
                        viewModel.hideAllOverlays()
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier
                )
            }
        }
    }

    // 页面启动时请求焦点
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
