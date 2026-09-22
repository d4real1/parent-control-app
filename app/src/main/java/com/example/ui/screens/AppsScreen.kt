package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InstalledApp
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

private enum class AppFilter {
    ALL,
    ALLOWED,
    BLOCKED
}

/**
 * Screen displaying installed applications on the child's device
 * with Allow/Block toggles and a refresh mechanism.
 *
 * // TODO: Implement fetching app list from child via Shizuku.
 */
@Composable
fun AppsScreen(
    apps: List<InstalledApp>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onToggleAppBlock: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Implement fetching app list from child via Shizuku.

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(AppFilter.ALL) }

    val filteredApps = remember(apps, searchQuery, selectedFilter) {
        apps.filter { app ->
            val matchesSearch = app.name.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true) ||
                    app.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                AppFilter.ALL -> true
                AppFilter.ALLOWED -> !app.isBlocked
                AppFilter.BLOCKED -> app.isBlocked
            }

            matchesSearch && matchesFilter
        }
    }

    val blockedCount = apps.count { it.isBlocked }
    val allowedCount = apps.count { !it.isBlocked }

    val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Summary & Refresh Action Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("apps_header_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavySurface)
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
                        text = "Installed Apps (${apps.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$allowedCount Allowed",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmeraldAllowed,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                        Text(
                            text = "$blockedCount Blocked",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = RoseBlocked,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Refresh Button
                Button(
                    onClick = onRefresh,
                    enabled = !isRefreshing,
                    modifier = Modifier.testTag("refresh_apps_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccentContainer,
                        contentColor = CyanAccent
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh App List",
                        modifier = Modifier
                            .size(18.dp)
                            .then(if (isRefreshing) Modifier.rotate(spinAngle) else Modifier)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRefreshing) "Syncing..." else "Refresh",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_search_field"),
            placeholder = {
                Text(
                    text = "Search apps or categories...",
                    color = TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = TextSecondary
                )
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkNavySurface,
                unfocusedContainerColor = DarkNavySurface,
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = DarkNavyOutline,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips: All / Allowed / Blocked
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == AppFilter.ALL,
                onClick = { selectedFilter = AppFilter.ALL },
                label = { Text("All (${apps.size})") },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanAccentContainer,
                    selectedLabelColor = CyanAccent,
                    containerColor = DarkNavySurface,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedFilter == AppFilter.ALLOWED,
                onClick = { selectedFilter = AppFilter.ALLOWED },
                label = { Text("Allowed ($allowedCount)") },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldAllowedContainer,
                    selectedLabelColor = EmeraldAllowed,
                    containerColor = DarkNavySurface,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedFilter == AppFilter.BLOCKED,
                onClick = { selectedFilter = AppFilter.BLOCKED },
                label = { Text("Blocked ($blockedCount)") },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoseBlockedContainer,
                    selectedLabelColor = RoseBlocked,
                    containerColor = DarkNavySurface,
                    labelColor = TextSecondary
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of Apps
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("installed_apps_list"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = filteredApps,
                key = { it.id }
            ) { app ->
                AppListItem(
                    app = app,
                    onToggleBlock = { onToggleAppBlock(app.id) }
                )
            }
        }
    }
}

@Composable
private fun AppListItem(
    app: InstalledApp,
    onToggleBlock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_item_${app.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavySurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App category icon badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (app.isBlocked) RoseBlockedContainer else DarkNavySurfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(app.category),
                    contentDescription = app.category,
                    tint = if (app.isBlocked) RoseBlocked else CyanAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // App name, package & usage details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (app.isBlocked) RoseBlockedContainer else EmeraldAllowedContainer
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (app.isBlocked) "BLOCKED" else "ALLOWED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (app.isBlocked) RoseBlocked else EmeraldAllowed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${app.category} • ${app.usageFormatted} today",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Allow / Block Switch
            // Checked means Blocked, Unchecked means Allowed (or vice versa - let's make Blocked explicit)
            Switch(
                checked = !app.isBlocked, // Checked = Allowed
                onCheckedChange = { onToggleBlock() },
                modifier = Modifier.testTag("app_toggle_${app.id}"),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EmeraldAllowed,
                    checkedTrackColor = EmeraldAllowedContainer,
                    uncheckedThumbColor = RoseBlocked,
                    uncheckedTrackColor = RoseBlockedContainer
                )
            )
        }
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "games" -> Icons.Filled.Games
        "entertainment" -> Icons.Filled.Movie
        "social media" -> Icons.Filled.Share
        "education" -> Icons.Filled.School
        else -> Icons.Filled.Widgets
    }
}
