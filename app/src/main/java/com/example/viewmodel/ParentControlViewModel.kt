package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ChildDevice
import com.example.model.InstalledApp
import com.example.model.ParentTab
import com.example.model.SchedulePolicy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParentControlUiState(
    val selectedTab: ParentTab = ParentTab.CONNECT,
    val childDevice: ChildDevice = ChildDevice(),
    val apps: List<InstalledApp> = defaultAppsList(),
    val schedulePolicy: SchedulePolicy = SchedulePolicy(),
    val isRefreshingApps: Boolean = false,
    val showQrScannerDialog: Boolean = false,
    val isConnectingP2P: Boolean = false,
    val showTimePickerFor: TimePickerTarget? = null,
    val userNotificationMessage: String? = null
)

enum class TimePickerTarget {
    BEDTIME_START,
    BEDTIME_END
}

class ParentControlViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ParentControlUiState())
    val uiState: StateFlow<ParentControlUiState> = _uiState.asStateFlow()

    fun selectTab(tab: ParentTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // ==========================================
    // TAB 1: CONNECT & PAIRING
    // ==========================================

    fun openQrScannerDialog() {
        _uiState.update { it.copy(showQrScannerDialog = true) }
    }

    fun closeQrScannerDialog() {
        _uiState.update { it.copy(showQrScannerDialog = false, isConnectingP2P = false) }
    }

    fun pairWithChildDevice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnectingP2P = true) }

            // TODO: Implement QR code scanning and P2P connection using Hypercore Protocol or WebRTC.
            delay(1500) // Simulated connection handshake

            _uiState.update {
                it.copy(
                    isConnectingP2P = false,
                    showQrScannerDialog = false,
                    childDevice = it.childDevice.copy(
                        isConnected = true,
                        lastSyncAgo = "Just now",
                        name = "Pixel 8a (Emma's Phone)"
                    ),
                    userNotificationMessage = "Successfully paired with child device!"
                )
            }
        }
    }

    fun toggleDeviceConnection() {
        val currentlyConnected = _uiState.value.childDevice.isConnected
        if (currentlyConnected) {
            _uiState.update {
                it.copy(
                    childDevice = it.childDevice.copy(isConnected = false, lastSyncAgo = "Disconnected"),
                    userNotificationMessage = "Child device disconnected"
                )
            }
        } else {
            openQrScannerDialog()
        }
    }

    // ==========================================
    // TAB 2: APPS MANAGEMENT
    // ==========================================

    fun toggleAppBlock(appId: String) {
        _uiState.update { state ->
            val updatedApps = state.apps.map { app ->
                if (app.id == appId) {
                    app.copy(isBlocked = !app.isBlocked)
                } else {
                    app
                }
            }
            state.copy(apps = updatedApps)
        }
    }

    fun refreshAppList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingApps = true) }

            // TODO: Implement fetching app list from child via Shizuku.
            delay(1200) // Simulated daemon query via Shizuku RPC

            _uiState.update {
                it.copy(
                    isRefreshingApps = false,
                    userNotificationMessage = "App inventory refreshed from child device via Shizuku"
                )
            }
        }
    }

    // ==========================================
    // TAB 3: SCHEDULES & POLICIES
    // ==========================================

    fun toggleBedtimeMode(enabled: Boolean) {
        _uiState.update {
            it.copy(schedulePolicy = it.schedulePolicy.copy(isBedtimeEnabled = enabled))
        }
        sendSchedulePoliciesToChild()
    }

    fun setBedtimeStart(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(
                schedulePolicy = it.schedulePolicy.copy(
                    bedtimeStartHour = hour,
                    bedtimeStartMinute = minute
                ),
                showTimePickerFor = null
            )
        }
        sendSchedulePoliciesToChild()
    }

    fun setBedtimeEnd(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(
                schedulePolicy = it.schedulePolicy.copy(
                    bedtimeEndHour = hour,
                    bedtimeEndMinute = minute
                ),
                showTimePickerFor = null
            )
        }
        sendSchedulePoliciesToChild()
    }

    fun openTimePicker(target: TimePickerTarget) {
        _uiState.update { it.copy(showTimePickerFor = target) }
    }

    fun dismissTimePicker() {
        _uiState.update { it.copy(showTimePickerFor = null) }
    }

    fun toggleScreenTimeLimit(enabled: Boolean) {
        _uiState.update {
            it.copy(
                schedulePolicy = it.schedulePolicy.copy(isScreenTimeLimitEnabled = enabled)
            )
        }
        sendSchedulePoliciesToChild()
    }

    fun updateScreenTimeLimit(hours: Float) {
        _uiState.update {
            it.copy(
                schedulePolicy = it.schedulePolicy.copy(screenTimeLimitHours = hours)
            )
        }
        sendSchedulePoliciesToChild()
    }

    fun toggleInstantPause() {
        _uiState.update {
            val nextState = !it.schedulePolicy.isInstantPauseActive
            it.copy(
                schedulePolicy = it.schedulePolicy.copy(isInstantPauseActive = nextState),
                userNotificationMessage = if (nextState) "Child device temporarily locked" else "Device unlocked"
            )
        }
        sendSchedulePoliciesToChild()
    }

    private fun sendSchedulePoliciesToChild() {
        // TODO: Implement sending schedule policies to child device.
    }

    fun clearNotificationMessage() {
        _uiState.update { it.copy(userNotificationMessage = null) }
    }
}

private fun defaultAppsList(): List<InstalledApp> = listOf(
    InstalledApp(
        id = "1",
        name = "YouTube",
        packageName = "com.google.android.youtube",
        category = "Entertainment",
        isBlocked = false,
        dailyUsageMinutes = 105,
        usageFormatted = "1h 45m"
    ),
    InstalledApp(
        id = "2",
        name = "TikTok",
        packageName = "com.zhiliaoapp.musically",
        category = "Social Media",
        isBlocked = true,
        dailyUsageMinutes = 80,
        usageFormatted = "1h 20m"
    ),
    InstalledApp(
        id = "3",
        name = "Roblox",
        packageName = "com.roblox.client",
        category = "Games",
        isBlocked = false,
        dailyUsageMinutes = 55,
        usageFormatted = "55m"
    ),
    InstalledApp(
        id = "4",
        name = "Instagram",
        packageName = "com.instagram.android",
        category = "Social Media",
        isBlocked = true,
        dailyUsageMinutes = 42,
        usageFormatted = "42m"
    ),
    InstalledApp(
        id = "5",
        name = "Minecraft",
        packageName = "com.mojang.minecraftpe",
        category = "Games",
        isBlocked = false,
        dailyUsageMinutes = 35,
        usageFormatted = "35m"
    ),
    InstalledApp(
        id = "6",
        name = "WhatsApp",
        packageName = "com.whatsapp",
        category = "Communication",
        isBlocked = false,
        dailyUsageMinutes = 28,
        usageFormatted = "28m"
    ),
    InstalledApp(
        id = "7",
        name = "Duolingo",
        packageName = "com.duolingo",
        category = "Education",
        isBlocked = false,
        dailyUsageMinutes = 20,
        usageFormatted = "20m"
    ),
    InstalledApp(
        id = "8",
        name = "Spotify",
        packageName = "com.spotify.music",
        category = "Audio",
        isBlocked = false,
        dailyUsageMinutes = 45,
        usageFormatted = "45m"
    ),
    InstalledApp(
        id = "9",
        name = "Chrome",
        packageName = "com.android.chrome",
        category = "Web Browser",
        isBlocked = false,
        dailyUsageMinutes = 18,
        usageFormatted = "18m"
    ),
    InstalledApp(
        id = "10",
        name = "Subway Surfers",
        packageName = "com.kiloo.subwaysurf",
        category = "Games",
        isBlocked = false,
        dailyUsageMinutes = 15,
        usageFormatted = "15m"
    )
)
