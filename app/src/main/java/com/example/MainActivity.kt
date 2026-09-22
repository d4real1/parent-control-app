package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.model.ParentTab
import com.example.ui.components.ParentBottomBar
import com.example.ui.components.ParentTopAppBar
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.ConnectScreen
import com.example.ui.screens.SchedulesScreen
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ParentControlViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ParentControlViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ParentControlApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ParentControlApp(
    viewModel: ParentControlViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNotificationMessage) {
        uiState.userNotificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotificationMessage()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        topBar = {
            ParentTopAppBar(
                isConnected = uiState.childDevice.isConnected,
                onShieldClick = {
                    viewModel.selectTab(ParentTab.CONNECT)
                }
            )
        },
        bottomBar = {
            ParentBottomBar(
                currentTab = uiState.selectedTab,
                onTabSelected = { tab ->
                    viewModel.selectTab(tab)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkNavyBackground)
        ) {
            Crossfade(
                targetState = uiState.selectedTab,
                animationSpec = tween(durationMillis = 200),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    ParentTab.CONNECT -> {
                        ConnectScreen(
                            childDevice = uiState.childDevice,
                            showQrDialog = uiState.showQrScannerDialog,
                            isConnectingP2P = uiState.isConnectingP2P,
                            onOpenQrScanner = { viewModel.openQrScannerDialog() },
                            onCloseQrScanner = { viewModel.closeQrScannerDialog() },
                            onConfirmPair = { viewModel.pairWithChildDevice() },
                            onToggleConnection = { viewModel.toggleDeviceConnection() }
                        )
                    }

                    ParentTab.APPS -> {
                        AppsScreen(
                            apps = uiState.apps,
                            isRefreshing = uiState.isRefreshingApps,
                            onRefresh = { viewModel.refreshAppList() },
                            onToggleAppBlock = { appId -> viewModel.toggleAppBlock(appId) }
                        )
                    }

                    ParentTab.SCHEDULES -> {
                        SchedulesScreen(
                            policy = uiState.schedulePolicy,
                            activeTimePicker = uiState.showTimePickerFor,
                            onToggleBedtime = { enabled -> viewModel.toggleBedtimeMode(enabled) },
                            onOpenTimePicker = { target -> viewModel.openTimePicker(target) },
                            onDismissTimePicker = { viewModel.dismissTimePicker() },
                            onSaveTimePicker = { target, hour, minute ->
                                if (target == com.example.viewmodel.TimePickerTarget.BEDTIME_START) {
                                    viewModel.setBedtimeStart(hour, minute)
                                } else {
                                    viewModel.setBedtimeEnd(hour, minute)
                                }
                            },
                            onToggleScreenTimeLimit = { enabled -> viewModel.toggleScreenTimeLimit(enabled) },
                            onUpdateScreenTimeLimit = { hours -> viewModel.updateScreenTimeLimit(hours) },
                            onToggleInstantPause = { viewModel.toggleInstantPause() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Parent Control for $name", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun ParentControlPreview() {
    MyApplicationTheme {
        ParentControlApp(viewModel = ParentControlViewModel())
    }
}
