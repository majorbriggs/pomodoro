package com.majorbriggs.pomodoro.wear.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector

data class PomodoroViewState(
    val stateText: String,
    val toggleButtonImageVector: ImageVector,
    val pomodorosDone: Int = 0,
    val resetButtonEnabled: Boolean = false,
)
