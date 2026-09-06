package com.example.draw.camera.ui

import android.net.Uri
import com.example.draw.camera.model.CameraColorFilter
import com.example.draw.camera.model.FilterMode

sealed interface CameraUiEvent {

    data object ScreenStarted : CameraUiEvent

    data object BackClicked : CameraUiEvent

    data object CaptureClicked : CameraUiEvent

    data object SwitchCameraClicked : CameraUiEvent

    data object FlashClicked : CameraUiEvent

    data object TimerClicked : CameraUiEvent

    data object GridClicked : CameraUiEvent

    data object CameraSizeClicked : CameraUiEvent

    data class ExposureChanged(
        val value: Int
    ) : CameraUiEvent

    data class FilterModeChanged(
        val mode: FilterMode
    ) : CameraUiEvent

    data class FilterSelected(
        val filterRes: Int?
    ) : CameraUiEvent

    data object ToolsClicked : CameraUiEvent

    data object ExposureClicked : CameraUiEvent

    data object FilterClicked : CameraUiEvent

    data object ClosePanelClicked : CameraUiEvent

    data class ZoomChanged(
        val ratio: Float,
    ) : CameraUiEvent

    data class ZoomStateChanged(
        val ratio: Float, val minRatio: Float, val maxRatio: Float
    ) : CameraUiEvent

    data class PermissionResult(
        val granted: Boolean
    ) : CameraUiEvent

    data class CameraReady(
        val hasFlash: Boolean, val exposureMin: Int, val exposureMax: Int
    ) : CameraUiEvent

    data class PhotoSaved(
        val uri: Uri
    ) : CameraUiEvent

    data class PhotoProcessed(
        val uri: Uri
    ) : CameraUiEvent

    data class CameraError(
        val message: String
    ) : CameraUiEvent

    data object GalleryClicked : CameraUiEvent

    data class GalleryImageSelected(
        val uri: Uri
    ) : CameraUiEvent

    data object ColorFilterClicked : CameraUiEvent

    data class ColorFilterSelected(
        val filter: CameraColorFilter
    ) : CameraUiEvent
}