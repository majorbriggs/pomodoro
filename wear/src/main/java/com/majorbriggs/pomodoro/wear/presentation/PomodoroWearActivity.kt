/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.majorbriggs.pomodoro.wear.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.dialog.Dialog
import com.majorbriggs.pomodoro.wear.presentation.model.PomodoroViewState
import com.majorbriggs.pomodoro.wear.presentation.theme.PomodoroTheme
import com.majorbriggs.pomodoro.wear.presentation.ui.SaveSessionDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PomodoroWearActivity : ComponentActivity() {

    // The remaining variables are related to the binding/monitoring/interacting with the
    // service that gathers all the data to calculate walking points.
    private var pomodoroServiceBound = false

    // Gathers mock location/sensor data for walking workouts and also promotes itself to a
    // foreground service with Ongoing Notification to continue gathering data when a user is
    // engaged in an active walking workout.
    private var pomodoroForegroundService: PomodoroForegroundService? = null


    private val pomodoroServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as PomodoroForegroundService.LocalBinder
            pomodoroForegroundService = binder.pomodoroForegroundService
            pomodoroServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            pomodoroForegroundService = null
            pomodoroServiceBound = false
        }
    }

    override fun onStart() {
        super.onStart()

        val serviceIntent = Intent(this, PomodoroForegroundService::class.java)
        bindService(serviceIntent, pomodoroServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        if (pomodoroServiceBound) {
            unbindService(pomodoroServiceConnection)
            pomodoroServiceBound = false
        }
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = hiltViewModel<PomodoroViewModel>()
            val viewState by viewModel.pomodoroViewState.collectAsState()
            val timeText by viewModel.timeText.collectAsState()
            Pomodoro(
                timeText = timeText,
                viewState = viewState,
                onToggle = {
                    if (viewState.resetButtonEnabled) {
                        pomodoroForegroundService?.stopWalkingWorkout()
                    } else {
                        pomodoroForegroundService?.startWalkingWorkout()
                    }
                    viewModel.toggleTimer()
                },
                onSessionDone = viewModel::finishSession,
                onSessionStopped = viewModel::reset
            )
        }
    }
}

@Composable
fun Pomodoro(
    modifier: Modifier = Modifier,
    viewState: PomodoroViewState,
    timeText: String,
    onToggle: () -> Unit,
    onSessionDone: () -> Unit,
    onSessionStopped: () -> Unit,
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    PomodoroTheme {
        Scaffold(
            timeText = { TimeText() }
        ) {
            Dialog(
                showDialog = showSaveDialog,
                onDismissRequest = { showSaveDialog = false }
            ) {
                SaveSessionDialog(
                    onPositiveClick = {
                        showSaveDialog = false
                        onSessionDone()
                    },
                    onNegativeClick = {
                        showSaveDialog = false
                        onSessionStopped()
                    },
                )
            }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = viewState.stateText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.title3
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center,
                    text = timeText,
                )
                Spacer(Modifier.height(10.dp))
                Row {
                    Button(onClick = onToggle) {
                        Icon(imageVector = viewState.toggleButtonImageVector, contentDescription = "Start")
                    }
                    Spacer(modifier.width(10.dp))
                    Button(
                        onClick = { showSaveDialog = true },
                        enabled = viewState.resetButtonEnabled,
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "Reset")
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = viewState.pomodorosDone.toString(),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Done,
                        tint = MaterialTheme.colors.secondary,
                        contentDescription = "Pomodoros done"
                    )
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    Pomodoro(
        modifier = Modifier,
        timeText = "25:00",
        viewState = PomodoroViewState(
            stateText = "State",
            resetButtonEnabled = false,
            toggleButtonImageVector = Icons.Default.PlayArrow,
            pomodorosDone = 0,
        ),
        onToggle = {},
        onSessionDone = {},
        onSessionStopped = {})
}
