package com.example.draw.camera.data


import androidx.annotation.DrawableRes
import com.example.draw.camera.model.FilterMode

data class FaceFilterItem(
    @DrawableRes
    val previewRes: Int,

    @DrawableRes
    val filterRes: Int?,

    val mode: FilterMode
)