package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SchedulePolicy
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentContainer
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavyOutline
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DarkNavySurfaceVariant
import com.example.ui.theme.EmeraldAllowed
import com.example.ui.theme.EmeraldAllowedContainer
import com.example.ui.theme.RoseBlocked
import com.example.ui.theme.RoseBlockedContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TimePickerTarget
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Screen displaying scheduling policies (Bedtime Mode, Screen Time Limit, Instant Pause).
 *
 * // TODO: Implement sending schedule policies to child device.
 */
@Composable
fun SchedulesScreen(
    policy: SchedulePolicy,
    activeTimePicker: TimePickerTarget?,
    onToggleBedtime: (Boolean) -> Unit,
    onOpenTimePicker: (TimePickerTarget) -> Unit,
    onDismissTimePicker: () -> Unit,
    onSaveTimePicker: (target: TimePickerTarget, hour: Int, minute: Int) -> Unit,
    onToggleScreenTimeLimit: (Boolean) -> Unit,
    onUpdateScreenTimeLimit: (Float) -> Unit,
    onToggleInstantPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Implement sending schedule policies to child device.

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Bedtime Mode Card with Time Picker
        BedtimeModeCard(
            policy = policy,
            onToggle = onToggleBedtime,
            onEditStart = { onOpenTimePicker(TimePickerTarget.BEDTIME_START) },
            onEditEnd = { onOpenTimePicker(TimePickerTarget.BEDTIME_END) }
        )

        // Screen Time Limit Card with Slider (1-12 hours)
        ScreenTimeLimitCard(
            policy = policy,
            onToggle = onToggleScreenTimeLimit,
            onUpdateLimit = onUpdateScreenTimeLimit
        )

        // Instant Lock / Pause Device Card
        InstantPauseCard(
            isPaused = policy.isInstantPauseActive,
            onToggle = onToggleInstantPause
        )

        // Policy Sync Banner
        PolicySyncBanner()

        Spacer(modifier = Modifier.height(12.dp))
    }

    // Material 3 TimePicker Dialog
    if (activeTimePicker != null) {
        val initialHour = if (activeTimePicker == TimePickerTarget.BEDTIME_START) {
            policy.bedtimeStartHour
        } else {
            policy.bedtimeEndHour
        }
        val initialMinute = if (activeTimePicker == TimePickerTarget.BEDTIME_START) {
            policy.bedtimeStartMinute
        } else {
            policy.bedtimeEndMinute
        }

        ScheduleTimePickerDialog(
            title = if (activeTimePicker == TimePickerTarget.BEDTIME_START) "Bedtime Starts At" else "Bedtime Ends At",
            initialHour = initialHour,
            initialMinute = initialMinute,
            onDismiss = onDismissTimePicker,
            onConfirm = { hour, minute ->
                onSaveTimePicker(activeTimePicker, hour, minute)
            }
        )
    }
}

@Composable
private fun BedtimeModeCard(
    policy: SchedulePolicy,
    onToggle: (Boolean) -> Unit,
    onEditStart: () -> Unit,
    onEditEnd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bedtime_mode_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with Bedtime Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (policy.isBedtimeEnabled) CyanAccentContainer else DarkNavySurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bedtime,
                            contentDescription = "Bedtime",
                            tint = if (policy.isBedtimeEnabled) CyanAccent else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Bedtime Mode",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = if (policy.isBedtimeEnabled) "Active Schedule" else "Disabled",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (policy.isBedtimeEnabled) EmeraldAllowed else TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Switch(
                    checked = policy.isBedtimeEnabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("bedtime_toggle"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanAccent,
                        checkedTrackColor = CyanAccentContainer
                    )
                )
            }

            AnimatedVisibility(visible = policy.isBedtimeEnabled) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkNavySurfaceVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bedtime Hours (Tap to Change Time)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Time Chip / Card
                        TimeSelectionBox(
                            label = "Bedtime Begins",
                            timeFormatted = formatTime(policy.bedtimeStartHour, policy.bedtimeStartMinute),
                            onClick = onEditStart,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bedtime_start_picker_button")
                        )

                        // End Time Chip / Card
                        TimeSelectionBox(
                            label = "Wakes Up At",
                            timeFormatted = formatTime(policy.bedtimeEndHour, policy.bedtimeEndMinute),
                            onClick = onEditEnd,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bedtime_end_picker_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Child device locks automatically during these hours. Emergency calling remains enabled.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSelectionBox(
    label: String,
    timeFormatted: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavySurfaceVariant)
            .border(1.dp, DarkNavyOutline, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun ScreenTimeLimitCard(
    policy: SchedulePolicy,
    onToggle: (Boolean) -> Unit,
    onUpdateLimit: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("screen_time_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with Screen Time Limit Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (policy.isScreenTimeLimitEnabled) CyanAccentContainer else DarkNavySurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timelapse,
                            contentDescription = "Screen Time",
                            tint = if (policy.isScreenTimeLimitEnabled) CyanAccent else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Screen Time Limit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = if (policy.isScreenTimeLimitEnabled) {
                                "${formatHours(policy.screenTimeLimitHours)} Daily Allowance"
                            } else {
                                "Unrestricted"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (policy.isScreenTimeLimitEnabled) CyanAccent else TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Switch(
                    checked = policy.isScreenTimeLimitEnabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("screen_time_toggle"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanAccent,
                        checkedTrackColor = CyanAccentContainer
                    )
                )
            }

            AnimatedVisibility(visible = policy.isScreenTimeLimitEnabled) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkNavySurfaceVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Limit",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = formatHours(policy.screenTimeLimitHours),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            ),
                            modifier = Modifier.testTag("screen_time_limit_value")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider from 1 to 12 hours
                    Slider(
                        value = policy.screenTimeLimitHours,
                        onValueChange = { newValue ->
                            // Round to nearest 0.5 hours
                            val rounded = (newValue * 2).roundToInt() / 2f
                            onUpdateLimit(rounded)
                        },
                        valueRange = 1f..12f,
                        steps = 21, // 0.5 hr intervals between 1 and 12
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("screen_time_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = DarkNavySurfaceVariant
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "1 hour",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                        Text(
                            text = "6 hours",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                        Text(
                            text = "12 hours",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset chips: 2h, 3.5h, 5h, 8h
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2f, 3.5f, 5f, 8f).forEach { hours ->
                            FilterChip(
                                selected = (policy.screenTimeLimitHours == hours),
                                onClick = { onUpdateLimit(hours) },
                                label = { Text(formatHours(hours)) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanAccentContainer,
                                    selectedLabelColor = CyanAccent,
                                    containerColor = DarkNavySurfaceVariant,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstantPauseCard(
    isPaused: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("instant_pause_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPaused) RoseBlockedContainer else DarkNavySurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isPaused) RoseBlocked else DarkNavySurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Filled.LockClock else Icons.Filled.PauseCircle,
                            contentDescription = null,
                            tint = if (isPaused) Color.White else AmberWarning,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (isPaused) "Device Currently Paused" else "Instant Device Pause",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = if (isPaused) "All apps locked on child device" else "Family dinner or focus time",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isPaused) RoseBlocked else TextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("instant_pause_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPaused) EmeraldAllowed else RoseBlocked,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Filled.PlayCircle else Icons.Filled.PauseCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPaused) "Resume Child Device" else "Pause Device Immediately",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun PolicySyncBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Policy Sync Active",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Any schedule adjustments are dispatched automatically to the child device via Shizuku RPC and direct P2P link.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF002838))
            ) {
                Text("Set Time")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = DarkNavySurfaceVariant,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = TextPrimary,
                        selectorColor = CyanAccent,
                        periodSelectorBorderColor = CyanAccent,
                        periodSelectorSelectedContainerColor = CyanAccentContainer,
                        periodSelectorSelectedContentColor = CyanAccent,
                        timeSelectorSelectedContainerColor = CyanAccentContainer,
                        timeSelectorSelectedContentColor = CyanAccent
                    )
                )
            }
        },
        containerColor = DarkNavySurface
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val amPm = if (isPm) "PM" else "AM"
    return String.format(Locale.US, "%d:%02d %s", displayHour, minute, amPm)
}

private fun formatHours(hours: Float): String {
    val whole = hours.toInt()
    val fraction = hours - whole
    val minutes = (fraction * 60).roundToInt()

    return if (minutes > 0) {
        "${whole}h ${minutes}m"
    } else {
        "${whole} hrs"
    }
}
