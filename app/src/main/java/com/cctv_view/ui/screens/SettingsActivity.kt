package com.cctv_view.ui.screens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cctv_view.data.PlayerType
import com.cctv_view.ui.theme.CCTVViewTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CCTVViewTheme {
                SettingsScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("cctv_view", Context.MODE_PRIVATE)

    var fontSize by remember {
        mutableStateOf(prefs.getString("text_size", "22") ?: "22")
    }
    var directChannelChange by remember {
        mutableStateOf(prefs.getBoolean("direct_channel_change", false))
    }
    var showProgramInfo by remember {
        mutableStateOf(prefs.getBoolean("show_program_info", true))
    }
    var overlayDuration by remember {
        mutableStateOf(prefs.getInt("overlay_duration", 5))
    }
    var playerType by remember {
        val typeIndex = prefs.getInt("player_type", PlayerType.EXOPLAYER.ordinal)
        mutableStateOf(
            try {
                PlayerType.entries[typeIndex]
            } catch (e: Exception) {
                PlayerType.EXOPLAYER
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "设置",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 播放器设置
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "播放器",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "选择播放方式，原生播放器兼容性更好",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "原生播放器" to PlayerType.EXOPLAYER,
                                "网页播放" to PlayerType.WEBVIEW
                            ).forEach { (label, type) ->
                                val isSelected = playerType == type
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        playerType = type
                                        prefs.edit().putInt("player_type", type.ordinal).apply()
                                    },
                                    label = { Text(label) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (playerType == PlayerType.EXOPLAYER)
                                "使用 ExoPlayer 原生播放 m3u8 直播流，不依赖 WebView，低版本系统也能用"
                            else
                                "使用 WebView 加载网页播放，兼容更多频道但需要较新版本 WebView",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 字体大小设置
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "字体大小",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "当前: ${fontSize}sp",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("18", "22", "25", "30").forEach { size ->
                                FilterChip(
                                    selected = fontSize == size,
                                    onClick = {
                                        fontSize = size
                                        prefs.edit().putString("text_size", size).apply()
                                    },
                                    label = { Text("${size}sp") },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 直接换台模式
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "直接换台模式",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (directChannelChange) "上下键直接换台" else "上下键显示频道列表",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = directChannelChange,
                            onCheckedChange = {
                                directChannelChange = it
                                prefs.edit().putBoolean("direct_channel_change", it).apply()
                            }
                        )
                    }
                }
            }

            // 显示节目信息
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "显示频道信息",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (showProgramInfo) "切换频道时显示频道名称" else "不显示频道信息",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = showProgramInfo,
                            onCheckedChange = {
                                showProgramInfo = it
                                prefs.edit().putBoolean("show_program_info", it).apply()
                            }
                        )
                    }
                }
            }

            // 浮层显示时长
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "浮层显示时长",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${overlayDuration} 秒",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                        Slider(
                            value = overlayDuration.toFloat(),
                            onValueChange = {
                                overlayDuration = it.toInt()
                                prefs.edit().putInt("overlay_duration", overlayDuration).apply()
                            },
                            valueRange = 2f..10f,
                            steps = 8,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 按键说明
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "按键说明",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(
                            "↑ / ↓" to "上/下一个频道（或列表移动）",
                            "← / →" to "调出频道列表（或切换分类）",
                            "确认/OK" to "打开频道列表 / 选择",
                            "菜单" to "打开功能菜单",
                            "数字键 0-9" to "输入频道号快速换台",
                            "返回" to "关闭浮层 / 返回"
                        ).forEach { (key, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    key,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(100.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    desc,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // 关于
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "关于",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "CCTV View TV 版 v1.1.0\n专为 Android TV 优化\n支持原生播放 + WebView 双模式",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // 重置按钮
            item {
                Button(
                    onClick = {
                        prefs.edit().clear().apply()
                        fontSize = "22"
                        directChannelChange = false
                        showProgramInfo = true
                        overlayDuration = 5
                        playerType = PlayerType.EXOPLAYER
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("恢复默认设置", fontSize = 16.sp)
                }
            }
        }
    }
}
