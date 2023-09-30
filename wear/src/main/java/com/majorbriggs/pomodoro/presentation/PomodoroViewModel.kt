package com.majorbriggs.pomodoro.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.majorbriggs.pomodoro.presentation.model.PomodoroState
import com.majorbriggs.pomodoro.presentation.model.PomodoroViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroViewModel : ViewModel() {

    private val seconds: Int = 1

    private val pomodoroSize = 25 * seconds
    private val breakSize = 5 * seconds

    private val _elapsedTime = MutableStateFlow(pomodoroSize)
    private val _pomodoroState = MutableStateFlow(PomodoroState.RESET)
    private val _pomodorosDone = MutableStateFlow(0)

    private val pomodorosDone = _pomodorosDone.asStateFlow()

    val pomodoroViewState: StateFlow<PomodoroViewState> =
        _pomodoroState.combine(pomodorosDone) { state, pomodorosDone ->
            when (state) {
                PomodoroState.WORK -> PomodoroViewState(
                    stateText = "Working...",
                    toggleButtonImageVector = Icons.Default.Pause,
                    pomodorosDone = pomodorosDone,
                    resetButtonEnabled = false,
                )
                PomodoroState.BREAK -> PomodoroViewState(
                    stateText = "Break time!",
                    toggleButtonImageVector = Icons.Default.PlayArrow,
                    pomodorosDone = pomodorosDone,
                    resetButtonEnabled = false,
                )
                PomodoroState.PAUSED -> PomodoroViewState(
                    stateText = "Paused",
                    toggleButtonImageVector = Icons.Default.PlayArrow,
                    pomodorosDone = pomodorosDone,
                    resetButtonEnabled = true,
                )
                PomodoroState.RESET -> PomodoroViewState(
                    stateText = "Ready to start?",
                    toggleButtonImageVector = Icons.Default.PlayArrow,
                    pomodorosDone = pomodorosDone,
                    resetButtonEnabled = false,
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PomodoroViewState(
                stateText = "Ready to start?",
                toggleButtonImageVector = Icons.Default.PlayArrow,
                pomodorosDone = 0,
                resetButtonEnabled = false,
            )
        )

    val timeText = _elapsedTime.map {
        secondsToFormattedTime(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        secondsToFormattedTime(pomodoroSize)
    )

    init {
        _pomodoroState.flatMapLatest { getTime(it) }.launchIn(viewModelScope)
    }

    private fun getTime(pomodoroState: PomodoroState): Flow<Int> {
        return flow {
            while (pomodoroState == PomodoroState.WORK || pomodoroState == PomodoroState.BREAK) {
                delay(1000)
                if (_elapsedTime.value == 0) {
                    if (pomodoroState == PomodoroState.WORK) {
                        _pomodorosDone.update { it + 1 }
                        _pomodoroState.update { PomodoroState.BREAK }
                        _elapsedTime.update { breakSize }
                    } else {
                        _pomodoroState.update { PomodoroState.RESET }
                        _elapsedTime.update { pomodoroSize }
                    }
                }
                _elapsedTime.update { it - 1 }
            }
        }
    }

    fun toggleTimer() {
        when (_pomodoroState.value) {
            PomodoroState.WORK -> _pomodoroState.update { PomodoroState.PAUSED }
            PomodoroState.BREAK -> _pomodoroState.update { PomodoroState.WORK }
            PomodoroState.PAUSED -> _pomodoroState.update { PomodoroState.WORK }
            PomodoroState.RESET -> _pomodoroState.update { PomodoroState.WORK }
        }
    }

    fun reset() {
        _pomodoroState.value = PomodoroState.RESET
        _elapsedTime.value = pomodoroSize
    }

    fun finishSession() {
        _pomodorosDone.update { it + 1 }
        reset()
    }

    fun secondsToFormattedTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}
