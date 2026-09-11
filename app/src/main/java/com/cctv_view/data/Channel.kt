package com.cctv_view.data

data class Channel(
    val id: Int,
    val number: Int,
    val name: String,
    val url: String,           // 网页播放地址（WebView备用）
    val streamUrl: String,     // 直播流地址（m3u8，原生播放器用）
    val category: ChannelCategory,
    val logo: String? = null
)

enum class ChannelCategory {
    CCTV,
    LOCAL
}

enum class PlayerType {
    EXOPLAYER,  // 原生播放器（默认，不依赖WebView）
    WEBVIEW     // WebView 网页播放（备用）
}
