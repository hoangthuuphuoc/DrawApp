package com.example.draw.draw.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DrawViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        DrawUiState()
    )

    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<DrawUiEffect>()

    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(
        event: DrawUiEvent
    ) {

        when (event) {

            DrawUiEvent.OpenDraw -> {
                openDraw()
            }

            DrawUiEvent.CloseDraw -> {
                closeDraw()
            }

            is DrawUiEvent.ToolSelected -> {
                selectTool(
                    event.tool
                )
            }

            DrawUiEvent.ColorClicked -> {
                showColorDialog()
            }

            is DrawUiEvent.ColorSelected -> {
                selectColor(
                    event.color
                )
            }

            DrawUiEvent.UndoClicked -> {
                undo()
            }

            DrawUiEvent.RedoClicked -> {
                redo()
            }
        }
    }

    private fun openDraw() {
        _uiState.update {
            it.copy(
                isDrawMode = true
            )
        }
    }

    private fun closeDraw() {
        _uiState.update {
            it.copy(
                isDrawMode = false
            )
        }
    }

    private fun selectTool(
        tool: Int
    ) {
        _uiState.update {
            it.copy(
                selectedTool = tool
            )
        }
    }

    private fun selectColor(
        color: Int
    ) {
        _uiState.update {
            it.copy(
                selectedColor = color
            )
        }
    }

    private fun showColorDialog() {

        viewModelScope.launch {
            _uiEffect.emit(
                DrawUiEffect.ShowColorDialog
            )
        }
    }

    private fun undo() {

        viewModelScope.launch {
            _uiEffect.emit(
                DrawUiEffect.Undo
            )
        }
    }

    private fun redo() {

        viewModelScope.launch {
            _uiEffect.emit(
                DrawUiEffect.Redo
            )
        }
    }
}