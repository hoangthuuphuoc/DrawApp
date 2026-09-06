package com.example.draw.camera.ui

import com.example.draw.camera.model.CameraColorFilter
import com.example.draw.camera.model.CameraFacing
import com.example.draw.camera.model.CameraSize
import com.example.draw.camera.model.FilterMode
import com.example.draw.camera.model.FlashState
import com.example.draw.camera.model.TimerState

data class CameraUiState(
    val flashState: FlashState = FlashState.OFF,

    val timerState: TimerState = TimerState.OFF,

    val cameraSize: CameraSize = CameraSize.S4_3,

    val gridEnabled: Boolean = true,

    val cameraFacing: CameraFacing = CameraFacing.BACK,

    val filterMode: FilterMode = FilterMode.HEAD,

    val selectedFilterRes: Int? = null,

    val countdownValue: Int? = null,

    val exposureIndex: Int = 0,

    val exposureMin: Int = 0,

    val exposureMax: Int = 0,

    val hasFlash: Boolean = false,

    val isCameraReady: Boolean = false,

    val isTakingPicture: Boolean = false,

    val showToolsPanel: Boolean = false,
    val showExposurePanel: Boolean = false,
    val showFilterPanel: Boolean = false,
    val zoomRatio: Float = 1f,
    val zoomMinRatio: Float = 1f,
    val zoomMaxRatio: Float = 1f,
    val colorFilter: CameraColorFilter =
        CameraColorFilter.ORIGINAL,

    val showColorFilterPanel: Boolean =
        false,
)