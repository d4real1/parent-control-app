package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ParentTab
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentContainer
import com.example.ui.theme.DarkNavySurface

@Composable
fun ParentBottomBar(
    currentTab: ParentTab,
    onTabSelected: (ParentTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("parent_bottom_nav_bar"),
        containerColor = DarkNavySurface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        // Tab 1: Connect
        NavigationBarItem(
            selected = currentTab == ParentTab.CONNECT,
            onClick = { onTabSelected(ParentTab.CONNECT) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ParentTab.CONNECT) {
                        Icons.Filled.QrCodeScanner
                    } else {
                        Icons.Outlined.QrCodeScanner
                    },
                    contentDescription = "Connect Tab"
                )
            },
            label = { Text("Connect") },
            modifier = Modifier.testTag("tab_connect"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanAccent,
                selectedTextColor = CyanAccent,
                indicatorColor = CyanAccentContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Tab 2: Apps
        NavigationBarItem(
            selected = currentTab == ParentTab.APPS,
            onClick = { onTabSelected(ParentTab.APPS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ParentTab.APPS) {
                        Icons.Filled.Apps
                    } else {
                        Icons.Outlined.Apps
                    },
                    contentDescription = "Apps Tab"
                )
            },
            label = { Text("Apps") },
            modifier = Modifier.testTag("tab_apps"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanAccent,
                selectedTextColor = CyanAccent,
                indicatorColor = CyanAccentContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Tab 3: Schedules
        NavigationBarItem(
            selected = currentTab == ParentTab.SCHEDULES,
            onClick = { onTabSelected(ParentTab.SCHEDULES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ParentTab.SCHEDULES) {
                        Icons.Filled.Schedule
                    } else {
                        Icons.Outlined.Schedule
                    },
                    contentDescription = "Schedules Tab"
                )
            },
            label = { Text("Schedules") },
            modifier = Modifier.testTag("tab_schedules"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanAccent,
                selectedTextColor = CyanAccent,
                indicatorColor = CyanAccentContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
