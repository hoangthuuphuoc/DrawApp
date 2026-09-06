package com.example.draw.draw.ui

import android.graphics.Color

data class DrawUiState(
    val isDrawMode: Boolean = false, val selectedTool: Int = 2, val selectedColor: Int = Color.BLACK
)