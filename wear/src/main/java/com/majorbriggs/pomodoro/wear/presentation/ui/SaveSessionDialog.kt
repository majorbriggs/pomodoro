package com.majorbriggs.pomodoro.wear.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert

@Composable
fun SaveSessionDialog(
  onPositiveClick: () -> Unit,
  onNegativeClick: () -> Unit,
) {
  Alert(
    icon = {},
    title = {
      Text(text = "Session stopped")
    },
    negativeButton = {
      Button(
        onClick = onNegativeClick,
        colors = ButtonDefaults.secondaryButtonColors()
      ) {
        Icon(
          imageVector = Icons.Filled.Clear,
          contentDescription = "No"
        )
      }
    },
    positiveButton = {
      Button(
        onClick = onPositiveClick,
        colors = ButtonDefaults.primaryButtonColors()
      ) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = "Yes"
        )
      }
    }

  ) {
    Text(
      text = "Save it as done?",
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.body2,
      color = MaterialTheme.colors.onBackground
    )
  }
}
