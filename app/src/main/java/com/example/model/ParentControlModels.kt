package com.example.model

enum class ParentTab(val label: String) {
    CONNECT("Connect"),
    APPS("Apps"),
    SCHEDULES("Schedules")
}

data class ChildDevice(
    val name: String = "Pixel 8a (Emma's Phone)",
    val deviceId: String = "SHIZUKU-P2P-8849-AF",
    val isConnected: Boolean = true,
    val batteryPercent: Int = 82,
    val isCharging: Boolean = false,
    val lastSyncAgo: String = "Just now",
    val protocol: String = "Hypercore / WebRTC P2P"
)

data class InstalledApp(
    val id: String,
    val name: String,
    val packageName: String,
    val category: String,
    val isBlocked: Boolean,
    val dailyUsageMinutes: Int,
    val usageFormatted: String
)

data class SchedulePolicy(
    val isBedtimeEnabled: Boolean = true,
    val bedtimeStartHour: Int = 21,
    val bedtimeStartMinute: Int = 30,
    val bedtimeEndHour: Int = 7,
    val bedtimeEndMinute: Int = 0,
    val isScreenTimeLimitEnabled: Boolean = true,
    val screenTimeLimitHours: Float = 3.5f,
    val isInstantPauseActive: Boolean = false,
    val activeDays: Set<Int> = setOf(1, 2, 3, 4, 5) // Mon-Fri
)
