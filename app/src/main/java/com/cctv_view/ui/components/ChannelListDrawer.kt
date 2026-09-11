package com.cctv_view.ui.components

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.cctv_view.data.Channel
import com.cctv_view.data.ChannelCategory

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChannelListDrawer(
    cctvChannels: List<Channel>,
    localChannels: List<Channel>,
    currentChannelId: Int,
    selectedCategory: ChannelCategory,
    onCategorySelected: (ChannelCategory) -> Unit,
    onChannelSelected: (Channel) -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    var tabIndex by remember { mutableIntStateOf(if (selectedCategory == ChannelCategory.CCTV) 0 else 1) }
    val channels = if (tabIndex == 0) cctvChannels else localChannels
    val listState = rememberLazyListState()

    // 找到当前频道的初始索引
    val initialIndex = remember(currentChannelId, tabIndex) {
        channels.indexOfFirst { it.id == currentChannelId }.coerceAtLeast(0)
    }

    var selectedChannelIndex by remember { mutableIntStateOf(initialIndex) }
    var lastKeyTime by remember { mutableLongStateOf(0L) }

    // 显示后自动滚动到当前频道并请求焦点
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        focusRequester.requestFocus()
        coroutineScope.launch {
            listState.scrollToItem(initialIndex.coerceAtLeast(0))
        }
    }

    // 当分类改变时更新选中索引
    LaunchedEffect(tabIndex) {
        val newList = if (tabIndex == 0) cctvChannels else localChannels
        val newIndex = newList.indexOfFirst { it.id == currentChannelId }.coerceAtLeast(0)
        selectedChannelIndex = newIndex
        coroutineScope.launch {
            listState.animateScrollToItem(newIndex)
        }
    }

    // 当当前频道变化时更新选中索引
    LaunchedEffect(currentChannelId) {
        val newIndex = channels.indexOfFirst { it.id == currentChannelId }.coerceAtLeast(0)
        selectedChannelIndex = newIndex
        coroutineScope.launch {
            listState.animateScrollToItem(newIndex)
        }
    }

    fun navigateChannel(direction: Int) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastKeyTime < 150) return
        lastKeyTime = currentTime

        val newIndex = (selectedChannelIndex + direction).coerceIn(0, channels.size - 1)
        if (newIndex != selectedChannelIndex) {
            selectedChannelIndex = newIndex
            coroutineScope.launch {
                listState.animateScrollToItem(newIndex)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(400.dp)
                .shadow(8.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                    if (event.nativeKeyEvent.action != KeyEvent.ACTION_DOWN) return@onKeyEvent false

                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            navigateChannel(-1)
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            navigateChannel(1)
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            // 左键切换分类
                            tabIndex = if (tabIndex == 0) 1 else 0
                            onCategorySelected(if (tabIndex == 0) ChannelCategory.CCTV else ChannelCategory.LOCAL)
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            // 右键切换分类
                            tabIndex = if (tabIndex == 0) 1 else 0
                            onCategorySelected(if (tabIndex == 0) ChannelCategory.CCTV else ChannelCategory.LOCAL)
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER -> {
                            if (selectedChannelIndex in channels.indices) {
                                onChannelSelected(channels[selectedChannelIndex])
                            }
                            true
                        }
                        KeyEvent.KEYCODE_BACK,
                        KeyEvent.KEYCODE_MENU -> {
                            onDismiss()
                            true
                        }
                        else -> false
                    }
                },
            shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标题
                Text(
                    text = "频道列表",
                    modifier = Modifier.padding(20.dp),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 分类 Tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("央视频道", "地方频道").forEachIndexed { index, title ->
                        val isSelected = tabIndex == index
                        Button(
                            onClick = {
                                tabIndex = index
                                onCategorySelected(if (index == 0) ChannelCategory.CCTV else ChannelCategory.LOCAL)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White
                                else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                title,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // 频道列表
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = channels,
                        key = { channel -> channel.id }
                    ) { channel ->
                        val index = channels.indexOf(channel)
                        ChannelListItem(
                            channel = channel,
                            isSelected = channel.id == currentChannelId,
                            isFocused = selectedChannelIndex == index,
                            onClick = {
                                onChannelSelected(channel)
                            },
                            onFocus = {
                                if (selectedChannelIndex != index) {
                                    selectedChannelIndex = index
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelListItem(
    channel: Channel,
    isSelected: Boolean,
    isFocused: Boolean,
    onClick: () -> Unit,
    onFocus: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocus()
                }
            },
        shape = RoundedCornerShape(8.dp),
        color = when {
            isFocused -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${channel.number}  ${channel.name}",
                fontSize = 18.sp,
                color = when {
                    isFocused -> Color.White
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Text("▶", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            }
        }
    }
}
