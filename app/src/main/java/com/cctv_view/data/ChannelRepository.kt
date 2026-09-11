package com.cctv_view.data

import android.content.Context

class ChannelRepository(private val context: Context) {

    private val channels: List<Channel> by lazy {
        getDefaultChannels()
    }

    private fun getDefaultChannels(): List<Channel> {
        return listOf(
            // ===== 央视频道 (id 0-19) =====
            // 直播源说明：
            // - streamUrl: 原生播放器使用的 m3u8 直播流地址
            // - url: WebView 备用播放的网页地址
            // 注意：直播源可能会变化，如果某个源失效，可以在设置中切换到 WebView 模式

            Channel(0, 1, "CCTV-1 综合",
                "https://tv.cctv.com/live/cctv1/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225840/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(1, 2, "CCTV-2 财经",
                "https://tv.cctv.com/live/cctv2/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225841/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(2, 3, "CCTV-3 综艺",
                "https://tv.cctv.com/live/cctv3/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225842/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(3, 4, "CCTV-4 中文国际",
                "https://tv.cctv.com/live/cctv4/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225843/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(4, 5, "CCTV-5 体育",
                "https://tv.cctv.com/live/cctv5/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225844/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(5, 6, "CCTV-6 电影",
                "https://tv.cctv.com/live/cctv6/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225845/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(6, 7, "CCTV-7 国防军事",
                "https://tv.cctv.com/live/cctv7/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225846/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(7, 8, "CCTV-8 电视剧",
                "https://tv.cctv.com/live/cctv8/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225847/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(8, 9, "CCTV-9 纪录",
                "https://tv.cctv.com/live/cctvjilu",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225848/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(9, 10, "CCTV-10 科教",
                "https://tv.cctv.com/live/cctv10/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225849/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(10, 11, "CCTV-11 戏曲",
                "https://tv.cctv.com/live/cctv11/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225850/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(11, 12, "CCTV-12 社会与法",
                "https://tv.cctv.com/live/cctv12/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225851/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(12, 13, "CCTV-13 新闻",
                "https://tv.cctv.com/live/cctv13/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225852/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(13, 14, "CCTV-14 少儿",
                "https://tv.cctv.com/live/cctvchild",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225853/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(14, 15, "CCTV-15 音乐",
                "https://tv.cctv.com/live/cctv15/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225854/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(15, 16, "CCTV-16 奥林匹克",
                "https://tv.cctv.com/live/cctv16/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225855/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(16, 17, "CCTV-17 农业农村",
                "https://tv.cctv.com/live/cctv17/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225856/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(17, 18, "CCTV-5+ 体育赛事",
                "https://tv.cctv.com/live/cctv5plus/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225857/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(18, 19, "CCTV-4 欧洲",
                "https://tv.cctv.com/live/cctveurope",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225858/index.m3u8",
                ChannelCategory.CCTV, null),
            Channel(19, 20, "CCTV-4 美洲",
                "https://tv.cctv.com/live/cctvamerica/",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225859/index.m3u8",
                ChannelCategory.CCTV, null),

            // ===== 地方频道 (id 20-45) =====
            // 卫视直播源（移动IPTV源，部分地区可能需要切换到 WebView 模式）

            Channel(20, 21, "北京卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002309",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225860/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(21, 22, "广东卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002485",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225861/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(22, 23, "广东珠江",
                "https://www.gdtv.cn/tvChannelDetail/44",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225862/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(23, 24, "江苏卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002521",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225863/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(24, 25, "东方卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002483",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225864/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(25, 26, "浙江卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002520",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225865/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(26, 27, "湖南卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002475",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225866/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(27, 28, "湖北卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002508",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225867/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(28, 29, "广西卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002509",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225868/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(29, 30, "黑龙江卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002498",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225869/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(30, 31, "海南卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002506",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225870/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(31, 32, "重庆卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002531",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225871/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(32, 33, "深圳卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002481",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225872/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(33, 34, "四川卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002516",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225873/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(34, 35, "河南卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002525",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225874/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(35, 36, "东南卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002484",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225875/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(36, 37, "贵州卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002490",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225876/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(37, 38, "江西卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002503",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225877/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(38, 39, "辽宁卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002505",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225878/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(39, 40, "安徽卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002532",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225879/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(40, 41, "河北卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002493",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225880/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(41, 42, "山东卫视",
                "https://www.yangshipin.cn/tv/home?pid=600002513",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225881/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(42, 43, "大湾区卫视",
                "https://www.gdtv.cn/tvChannelDetail/51",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225882/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(43, 44, "广东少儿",
                "https://www.gdtv.cn/tvChannelDetail/54",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225883/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(44, 45, "嘉佳卡通",
                "https://www.gdtv.cn/tvChannelDetail/66",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225884/index.m3u8",
                ChannelCategory.LOCAL, null),
            Channel(45, 46, "荔枝纪录片",
                "https://www.gdtv.cn/tvChannelDetail/94",
                "https://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221225885/index.m3u8",
                ChannelCategory.LOCAL, null),
        )
    }

    fun getAllChannels(): List<Channel> = channels

    fun getChannelsByCategory(category: ChannelCategory): List<Channel> =
        channels.filter { it.category == category }

    fun getCCTVChannels(): List<Channel> = getChannelsByCategory(ChannelCategory.CCTV)

    fun getLocalChannels(): List<Channel> = getChannelsByCategory(ChannelCategory.LOCAL)

    fun getChannelByNumber(number: Int): Channel? =
        channels.find { it.number == number }

    fun getChannelById(id: Int): Channel? =
        channels.find { it.id == id }

    fun getNextChannel(currentId: Int): Channel? {
        val index = channels.indexOfFirst { it.id == currentId }
        return if (index >= 0 && index < channels.size - 1) channels[index + 1] else null
    }

    fun getPreviousChannel(currentId: Int): Channel? {
        val index = channels.indexOfFirst { it.id == currentId }
        return if (index > 0) channels[index - 1] else null
    }
}
