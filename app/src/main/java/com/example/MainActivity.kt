package com.example

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.model.ParentTab
import com.example.ui.components.ParentBottomBar
import com.example.ui.components.ParentTopAppBar
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.ConnectScreen
import com.example.ui.screens.SchedulesScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavySurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ParentControlViewModel
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity(), Shizuku.OnRequestPermissionResultListener {

    private val viewModel: ParentControlViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addRequestPermissionResultListener(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ParentControlApp(
                    viewModel = viewModel,
                    onRequestShizukuPermission = { requestShizukuPermission() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(this)
    }

    fun requestShizukuPermission() {
        try {
            Shizuku.requestPermission(0)
        } catch (e: Throwable) {
            Toast.makeText(this, "Shizuku service error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Shizuku granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Shizuku denied", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            val isGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (isGranted) {
                Toast.makeText(this, "Shizuku granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Shizuku denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun ParentControlApp(
    viewModel: ParentControlViewModel,
    onRequestShizukuPermission: () -> Unit = {},
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkNavyBackground)
        ) {
            // Shizuku permission request action banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("shizuku_banner_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Shizuku Service",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Shizuku Service",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "System permission bridge",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary
                                )
                            )
                        }
                    }

                    Button(
                        onClick = onRequestShizukuPermission,
                        modifier = Modifier.testTag("request_shizuku_permission_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = DarkNavyBackground
                        )
                    ) {
                        Text(
                            text = "Request Shizuku Permission",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
