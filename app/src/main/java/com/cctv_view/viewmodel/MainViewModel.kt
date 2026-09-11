package com.cctv_view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cctv_view.data.Channel
import com.cctv_view.data.ChannelCategory
import com.cctv_view.data.ChannelRepository
import com.cctv_view.data.PlayerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentChannel: Channel? = null,
    val isChangingChannel: Boolean = false,
    val isPlaying: Boolean = false,
    val showChannelList: Boolean = false,
    val showMenu: Boolean = false,
    val showNumberInput: Boolean = false,
    val numberInputBuffer: String = "",
    val showOverlay: Boolean = false,
    val overlayMessage: String = "",
    val channelCategory: ChannelCategory = ChannelCategory.CCTV,
    val programInfo: String = "",
    val webViewKey: Int = 0,
    val overlayDuration: Int = 5,
    val playerType: PlayerType = PlayerType.EXOPLAYER,
    val errorMessage: String = "",
    val showError: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChannelRepository(application)
    private val prefs = application.getSharedPreferences("cctv_view", android.content.Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var numberInputJob: kotlinx.coroutines.Job? = null

    init {
        loadSettings()
        loadLastChannel()
    }

    private fun loadSettings() {
        val overlayDuration = prefs.getInt("overlay_duration", 5)
        val playerTypeIndex = prefs.getInt("player_type", PlayerType.EXOPLAYER.ordinal)
        val playerType = try {
            PlayerType.entries[playerTypeIndex]
        } catch (e: Exception) {
            PlayerType.EXOPLAYER
        }
        _uiState.update {
            it.copy(
                overlayDuration = overlayDuration,
                playerType = playerType
            )
        }
    }

    fun savePlayerType(type: PlayerType) {
        prefs.edit().putInt("player_type", type.ordinal).apply()
        _uiState.update { it.copy(playerType = type) }
    }

    private fun loadLastChannel() {
        val lastChannelId = prefs.getInt("last_channel_id", 0)
        val channel = repository.getChannelById(lastChannelId) ?: repository.getAllChannels().firstOrNull()
        _uiState.update { it.copy(currentChannel = channel) }
    }

    fun saveLastChannel() {
        _uiState.value.currentChannel?.let { channel ->
            prefs.edit().putInt("last_channel_id", channel.id).apply()
        }
    }

    fun changeChannel(channel: Channel) {
        _uiState.update {
            it.copy(
                currentChannel = channel,
                isChangingChannel = true,
                webViewKey = it.webViewKey + 1,
                programInfo = "",
                overlayMessage = "${channel.name}\n加载中...",
                showOverlay = true,
                showError = false,
                errorMessage = "",
                isPlaying = false
            )
        }
        saveLastChannel()

        // 原生播放器会自动回调状态，这里设置超时保护
        viewModelScope.launch {
            delay(8000)
            if (_uiState.value.isChangingChannel && !_uiState.value.isPlaying) {
                _uiState.update { state ->
                    state.copy(
                        isChangingChannel = false,
                        showError = true,
                        errorMessage = "加载超时，请尝试切换播放源或检查网络",
                        showOverlay = false
                    )
                }
            }
        }

        // 定时隐藏浮层
        val duration = (_uiState.value.overlayDuration * 1000L)
        viewModelScope.launch {
            delay(duration)
            if (_uiState.value.showOverlay && _uiState.value.isPlaying) {
                _uiState.update { it.copy(showOverlay = false) }
            }
        }
    }

    fun onPlaybackStateChanged(isPlaying: Boolean, message: String) {
        if (isPlaying) {
            val channelName = _uiState.value.currentChannel?.name ?: ""
            val showProgramInfo = prefs.getBoolean("show_program_info", true)
            _uiState.update {
                it.copy(
                    isChangingChannel = false,
                    isPlaying = true,
                    showError = false,
                    errorMessage = "",
                    overlayMessage = if (showProgramInfo) channelName else "",
                    showOverlay = showProgramInfo
                )
            }
            // 定时隐藏浮层
            if (showProgramInfo) {
                val duration = (_uiState.value.overlayDuration * 1000L)
                viewModelScope.launch {
                    delay(duration)
                    _uiState.update { state -> state.copy(showOverlay = false) }
                }
            }
        } else if (message.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    overlayMessage = message,
                    showOverlay = true
                )
            }
        }
    }

    fun onPlaybackError(errorMsg: String) {
        _uiState.update {
            it.copy(
                isChangingChannel = false,
                isPlaying = false,
                showError = true,
                errorMessage = errorMsg,
                showOverlay = false
            )
        }
    }

    fun retryPlayback() {
        _uiState.value.currentChannel?.let { channel ->
            changeChannel(channel)
        }
    }

    fun nextChannel() {
        val current = _uiState.value.currentChannel ?: return
        val directChange = prefs.getBoolean("direct_channel_change", false)
        if (directChange) {
            val next = repository.getNextChannel(current.id)
            next?.let { changeChannel(it) }
        } else {
            val next = repository.getNextChannel(current.id)
            next?.let {
                showChannelListWithSelection(it.id)
            }
        }
    }

    fun previousChannel() {
        val current = _uiState.value.currentChannel ?: return
        val directChange = prefs.getBoolean("direct_channel_change", false)
        if (directChange) {
            val prev = repository.getPreviousChannel(current.id)
            prev?.let { changeChannel(it) }
        } else {
            val prev = repository.getPreviousChannel(current.id)
            prev?.let {
                showChannelListWithSelection(it.id)
            }
        }
    }

    private fun showChannelListWithSelection(channelId: Int) {
        val channel = repository.getChannelById(channelId)
        val category = if (channel?.category == ChannelCategory.CCTV) ChannelCategory.CCTV else ChannelCategory.LOCAL
        _uiState.update {
            it.copy(
                showChannelList = true,
                channelCategory = category
            )
        }
    }

    fun onPageFinished(programInfo: String) {
        val showProgramInfo = prefs.getBoolean("show_program_info", true)
        val message = if (showProgramInfo && programInfo.isNotEmpty()) {
            "${_uiState.value.currentChannel?.name ?: ""}\n$programInfo"
        } else {
            _uiState.value.currentChannel?.name ?: ""
        }
        _uiState.update {
            it.copy(
                isChangingChannel = false,
                isPlaying = true,
                programInfo = programInfo,
                overlayMessage = message,
                showOverlay = showProgramInfo
            )
        }
        if (showProgramInfo) {
            val duration = (_uiState.value.overlayDuration * 1000L)
            viewModelScope.launch {
                delay(duration)
                _uiState.update { state -> state.copy(showOverlay = false) }
            }
        }
    }

    fun toggleChannelList() {
        _uiState.update {
            it.copy(
                showChannelList = !it.showChannelList,
                showMenu = false,
                showNumberInput = false
            )
        }
    }

    fun toggleMenu() {
        _uiState.update {
            it.copy(
                showMenu = !it.showMenu,
                showChannelList = false,
                showNumberInput = false
            )
        }
    }

    fun hideAllOverlays() {
        _uiState.update {
            it.copy(
                showChannelList = false,
                showMenu = false,
                showNumberInput = false,
                numberInputBuffer = ""
            )
        }
    }

    fun appendNumber(number: Int) {
        val newBuffer = _uiState.value.numberInputBuffer + number.toString()
        _uiState.update { it.copy(numberInputBuffer = newBuffer, showNumberInput = true) }
        numberInputJob?.cancel()
        numberInputJob = viewModelScope.launch {
            delay(2000) // 缩短到2秒，响应更快
            processNumberInput()
        }
    }

    private fun processNumberInput() {
        val buffer = _uiState.value.numberInputBuffer
        if (buffer.isNotEmpty()) {
            val channelNumber = buffer.toIntOrNull()
            channelNumber?.let { num ->
                repository.getChannelByNumber(num)?.let { channel ->
                    changeChannel(channel)
                }
            }
        }
        _uiState.update { it.copy(showNumberInput = false, numberInputBuffer = "") }
    }

    fun clearNumberInput() {
        numberInputJob?.cancel()
        _uiState.update { it.copy(showNumberInput = false, numberInputBuffer = "") }
    }

    fun setChannelCategory(category: ChannelCategory) {
        _uiState.update { it.copy(channelCategory = category) }
    }

    fun refreshPage() {
        _uiState.update {
            it.copy(
                webViewKey = it.webViewKey + 1,
                showOverlay = true,
                overlayMessage = "刷新中...",
                showError = false
            )
        }
        // 原生播放器：重新加载
        _uiState.value.currentChannel?.let { channel ->
            changeChannel(channel)
        }
    }

    fun getCCTVChannels(): List<Channel> = repository.getCCTVChannels()
    fun getLocalChannels(): List<Channel> = repository.getLocalChannels()
    fun getAllChannels(): List<Channel> = repository.getAllChannels()
}
