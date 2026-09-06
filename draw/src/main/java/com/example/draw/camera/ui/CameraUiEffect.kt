package com.example.draw.camera.ui

import android.net.Uri
import com.example.draw.camera.model.CameraFacing
import com.example.draw.camera.model.CameraSize
import com.example.draw.camera.model.FilterMode
import com.example.draw.camera.model.FlashState

sealed interface CameraUiEffect {

    data object RequestCameraPermission : CameraUiEffect

    data class ConfigureCamera(
        val cameraFacing: CameraFacing, val cameraSize: CameraSize
    ) : CameraUiEffect

    data class SetZoom(
        val ratio: Float
    ) : CameraUiEffect

    data class SetFlash(
        val flashState: FlashState
    ) : CameraUiEffect

    data class SetExposure(
        val value: Int
    ) : CameraUiEffect

    data object CapturePhoto : CameraUiEffect

    data class ProcessCapturedPhoto(
        val uri: Uri, val cameraSize: CameraSize, val filterMode: FilterMode, val filterRes: Int?
    ) : CameraUiEffect

    data class OpenEditor(
        val uri: Uri
    ) : CameraUiEffect

    data class ShowMessage(
        val message: String
    ) : CameraUiEffect

    data object CloseScreen : CameraUiEffect

    data object OpenGallery : CameraUiEffect

}