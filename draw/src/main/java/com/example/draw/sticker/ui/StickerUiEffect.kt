package com.example.draw.sticker.ui

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes

sealed class StickerUiEffect {

    data object ShowTextDialog : StickerUiEffect()

    data class AddTextSticker(
        val text: String, @ColorRes val color: Int
    ) : StickerUiEffect()

    data class AddBitmapSticker(@DrawableRes val image: Int) : StickerUiEffect()

    data object ShowColorDialog : StickerUiEffect()

    data class ChangeTextColor(
        @ColorInt val color: Int
    ) : StickerUiEffect()

    data object ShowTextSize : StickerUiEffect()

    data class ChangeTextSize(
        val size: Float
    ) : StickerUiEffect()

    data object ShowTextStroke : StickerUiEffect()

    data class ChangeTextStrokeWidth(
        val width: Float
    ) : StickerUiEffect()

    data class ChangeTextStrokeColor(
        val color: Int
    ) : StickerUiEffect()

    data object ShowTextFont : StickerUiEffect()

    data class ChangeTextFont(
        @FontRes val font: Int
    ) : StickerUiEffect()

    data object ShowCrop : StickerUiEffect()

    data class StartCrop(
        val width: Int, val height: Int
    ) : StickerUiEffect()
}