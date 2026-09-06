package com.example.draw.draw.ui

sealed class DrawUiEffect {

    data object ShowColorDialog : DrawUiEffect()

    data object Undo : DrawUiEffect()

    data object Redo : DrawUiEffect()
}