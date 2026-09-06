package com.example.draw.sticker.ui

import StickerUiEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.draw.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StickerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        StickerUiState()
    )

    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<StickerUiEffect>()

    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(
        event: StickerUiEvent
    ) {

        when (event) {

            StickerUiEvent.AddTextClicked -> {
                showTextDialog()
            }

            is StickerUiEvent.ConfirmTextClicked -> {
                addTextSticker(
                    event.text
                )
            }

            StickerUiEvent.AddBitmapClicked -> {
                showStickerTool()
            }

            StickerUiEvent.TextColorClicked -> {
                showColorDialog()
            }

            is StickerUiEvent.TextColorSelected -> {
                changeTextColor(
                    event.color
                )
            }

            StickerUiEvent.TextSizeClicked -> {
                showTextSize()
            }

            is StickerUiEvent.TextSizeSelected -> {
                changeTextSize(event.size)
            }

            StickerUiEvent.TextStrokeClicked -> {
                showTextStroke()
            }

            is StickerUiEvent.TextStrokeWidthSelected -> {
                changeTextStrokeWidth(
                    event.width
                )
            }

            is StickerUiEvent.TextStrokeColorSelected -> {
                changeTextStrokeColor(
                    event.color
                )
            }

            StickerUiEvent.TextFontClicked -> {
                showTextFont()
            }

            is StickerUiEvent.TextFontSelected -> {
                changeTextFont(
                    event.font
                )
            }

            is StickerUiEvent.StickerCategoryClicked -> {
                changeStickerCategory(event.category)
            }

            is StickerUiEvent.StickerSelected -> {
                addBitmapSticker(event.image)
            }
            StickerUiEvent.CropClicked -> {
                showCrop()
            }

            is StickerUiEvent.CropRatioSelected -> {
                selectCropRatio(
                    event.width,
                    event.height,
                    event.name
                )
            }
        }
    }
    private fun showCrop() {
        _uiState.update { state ->
            state.copy(
                cropUiState = state.cropUiState.copy(
                    selectedRatio = "Free"
                )
            )
        }

        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ShowCrop
            )
        }
    }
    private fun selectCropRatio(
        width: Int,
        height: Int,
        name: String
    ) {
        _uiState.update { state ->
            state.copy(
                cropUiState = state.cropUiState.copy(
                    selectedRatio = name
                )
            )
        }

        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.StartCrop(
                    width = width,
                    height = height
                )
            )
        }
    }

    private fun showTextFont() {
        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ShowTextFont
            )
        }
    }

    private fun changeTextFont(
        font: Int
    ) {
        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ChangeTextFont(
                    font
                )
            )
        }
    }

    private fun changeTextSize(
        size: Float
    ) {
        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ChangeTextSize(
                    size
                )
            )
        }
    }
    private fun showStickerTool() {

        _uiState.update { state ->
            state.copy(
                stickerUiState = state.stickerUiState.copy(
                    selectedCategory = "All"
                )
            )
        }
    }

    private fun changeStickerCategory(
        category: String
    ) {

        _uiState.update { state ->
            state.copy(
                stickerUiState = state.stickerUiState.copy(
                    selectedCategory = category
                )
            )
        }
    }

    private fun showTextSize() {
        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ShowTextSize
            )
        }
    }

    private fun showColorDialog() {

        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ShowColorDialog
            )
        }
    }

    private fun changeTextColor(
        color: Int
    ) {

        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ChangeTextColor(
                    color
                )
            )
        }
    }

    private fun showTextDialog() {

        viewModelScope.launch {

            _uiEffect.emit(
                StickerUiEffect.ShowTextDialog
            )
        }
    }

    private fun addTextSticker(
        text: String
    ) {

        if (text.isBlank()) {
            return
        }

        _uiState.update { state ->

            state.copy(
                textStickerCount = state.textStickerCount + 1
            )
        }

        viewModelScope.launch {

            _uiEffect.emit(
                StickerUiEffect.AddTextSticker(
                    text = text, color = R.color.white
                )
            )
        }
    }

    private fun addBitmapSticker(image: Int) {
        _uiState.update { state ->

            state.copy(
                bitmapStickerCount = state.bitmapStickerCount + 1
            )
        }

        viewModelScope.launch {

            _uiEffect.emit(
                StickerUiEffect.AddBitmapSticker(image)
            )
        }
    }

    private fun showTextStroke() {

        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ShowTextStroke
            )
        }
    }

    private fun changeTextStrokeWidth(
        width: Float
    ) {

        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ChangeTextStrokeWidth(
                    width
                )
            )
        }
    }

    private fun changeTextStrokeColor(
        color: Int
    ) {

        viewModelScope.launch {
            _uiEffect.emit(
                StickerUiEffect.ChangeTextStrokeColor(
                    color
                )
            )
        }
    }


}