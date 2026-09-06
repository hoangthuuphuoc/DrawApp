package com.example.draw.sticker.ui

import android.graphics.Color

data class StickerUiState(
    val textStickerCount: Int = 0,
    val bitmapStickerCount: Int = 0,
    val drawUiState: DrawUiState=DrawUiState(),
    val stickerUiState: StickerToolUiState = StickerToolUiState(),
    val cropUiState: CropUiState= CropUiState()
)
data class DrawUiState(
    val isDrawMode: Boolean = false, val selectedTool: Int = 2, val selectedColor: Int = Color.BLACK
)
data class StickerToolUiState(
    val selectedCategory: String = "All"
)
data class CropUiState(
    val selectedRatio: String = "Free"
)