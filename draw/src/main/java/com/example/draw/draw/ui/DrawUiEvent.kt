package com.example.draw.draw.ui

sealed interface DrawUiEvent {

    data object OpenDraw : DrawUiEvent

    data object CloseDraw : DrawUiEvent

    data class ToolSelected(
        val tool: Int
    ) : DrawUiEvent

    data object ColorClicked : DrawUiEvent

    data class ColorSelected(
        val color: Int
    ) : DrawUiEvent

    data object UndoClicked : DrawUiEvent

    data object RedoClicked : DrawUiEvent
}