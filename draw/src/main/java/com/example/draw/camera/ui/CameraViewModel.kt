package com.example.draw.camera.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.draw.camera.model.CameraFacing
import com.example.draw.camera.model.CameraSize
import com.example.draw.camera.model.FlashState
import com.example.draw.camera.model.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CameraUiState()
    )

    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<CameraUiEffect>(
        Channel.BUFFERED
    )

    val effect = _effect.receiveAsFlow()

    private var countdownJob: Job? = null

    fun onEvent(
        event: CameraUiEvent
    ) {
        when (event) {

            CameraUiEvent.ScreenStarted -> {
                sendEffect(
                    CameraUiEffect.RequestCameraPermission
                )
            }

            CameraUiEvent.BackClicked -> {
                val state = _uiState.value

                if (state.showToolsPanel || state.showExposurePanel || state.showFilterPanel) {
                    _uiState.update {
                        it.copy(
                            showToolsPanel = false,
                            showExposurePanel = false,
                            showFilterPanel = false
                        )
                    }

                    return
                }

                countdownJob?.cancel()

                sendEffect(
                    CameraUiEffect.CloseScreen
                )
            }

            CameraUiEvent.CaptureClicked -> {
                capture()
            }

            CameraUiEvent.SwitchCameraClicked -> {
                switchCamera()
            }

            CameraUiEvent.FlashClicked -> {
                changeFlash()
            }

            CameraUiEvent.TimerClicked -> {
                changeTimer()
            }

            CameraUiEvent.GridClicked -> {
                _uiState.update {
                    it.copy(
                        gridEnabled = !it.gridEnabled
                    )
                }
            }

            CameraUiEvent.CameraSizeClicked -> {
                changeCameraSize()
            }

            is CameraUiEvent.ExposureChanged -> {
                changeExposure(
                    event.value
                )
            }

            is CameraUiEvent.FilterModeChanged -> {
                _uiState.update {
                    it.copy(
                        filterMode = event.mode,

                        selectedFilterRes = null
                    )
                }
            }

            is CameraUiEvent.FilterSelected -> {
                _uiState.update {
                    it.copy(
                        selectedFilterRes = event.filterRes
                    )
                }
            }

            CameraUiEvent.ToolsClicked -> {
                _uiState.update {
                    it.copy(
                        showToolsPanel = !it.showToolsPanel,
                        showExposurePanel = false,
                        showFilterPanel = false
                    )
                }
            }

            CameraUiEvent.ExposureClicked -> {
                _uiState.update {
                    it.copy(
                        showToolsPanel = false, showExposurePanel = true, showFilterPanel = false
                    )
                }
            }

            CameraUiEvent.FilterClicked -> {
                _uiState.update {
                    it.copy(
                        showToolsPanel = false, showExposurePanel = false, showFilterPanel = true
                    )
                }
            }

            CameraUiEvent.ClosePanelClicked -> {
                _uiState.update {
                    it.copy(
                        showToolsPanel = false, showExposurePanel = false, showFilterPanel = false, showColorFilterPanel = false
                    )
                }
            }

            is CameraUiEvent.ZoomChanged -> {
                val state =
                    _uiState.value

                if (
                    !state.isCameraReady ||
                    state.isTakingPicture
                ) {
                    return
                }

                val ratio =
                    event.ratio.coerceIn(
                        state.zoomMinRatio,
                        state.zoomMaxRatio
                    )

                sendEffect(
                    CameraUiEffect.SetZoom(
                        ratio
                    )
                )
            }
            is CameraUiEvent.ZoomStateChanged -> {
                _uiState.update {
                    it.copy(
                        zoomRatio = event.ratio,
                        zoomMinRatio = event.minRatio,
                        zoomMaxRatio = event.maxRatio
                    )
                }
            }

            is CameraUiEvent.PermissionResult -> {
                if (event.granted) {
                    configureCamera()
                } else {
                    sendEffect(
                        CameraUiEffect.ShowMessage(
                            "Cần quyền Camera"
                        )
                    )

                    sendEffect(
                        CameraUiEffect.CloseScreen
                    )
                }
            }

            is CameraUiEvent.CameraReady -> {
                cameraReady(
                    event
                )
            }

            is CameraUiEvent.PhotoSaved -> {
                val state = _uiState.value

                sendEffect(
                    CameraUiEffect.ProcessCapturedPhoto(
                        uri = event.uri,

                        cameraSize = state.cameraSize,

                        filterMode = state.filterMode,

                        filterRes = state.selectedFilterRes
                    )
                )
            }

            is CameraUiEvent.PhotoProcessed -> {
                _uiState.update {
                    it.copy(
                        isTakingPicture = false,

                        countdownValue = null
                    )
                }

                sendEffect(
                    CameraUiEffect.OpenEditor(
                        event.uri
                    )
                )
            }

            is CameraUiEvent.CameraError -> {
                _uiState.update {
                    it.copy(
                        isTakingPicture = false,

                        isCameraReady = false,

                        countdownValue = null
                    )
                }

                sendEffect(
                    CameraUiEffect.ShowMessage(
                        event.message
                    )
                )
            }
            CameraUiEvent.GalleryClicked -> {
                val state = _uiState.value

                if (state.isTakingPicture) {
                    return
                }

                _uiState.update {
                    it.copy(
                        showToolsPanel = false,
                        showExposurePanel = false,
                        showFilterPanel = false
                    )
                }

                sendEffect(
                    CameraUiEffect.OpenGallery
                )
            }
            is CameraUiEvent.GalleryImageSelected -> {
                sendEffect(
                    CameraUiEffect.OpenEditor(
                        event.uri
                    )
                )
            }
            CameraUiEvent.ColorFilterClicked -> {
                _uiState.update {
                    it.copy(
                        showToolsPanel = false,
                        showExposurePanel = false,
                        showFilterPanel = false,
                        showColorFilterPanel = true
                    )
                }
            }
            is CameraUiEvent.ColorFilterSelected -> {
                _uiState.update {
                    it.copy(
                        colorFilter = event.filter
                    )
                }
            }
        }
    }

    private fun configureCamera() {
        val state = _uiState.value

        _uiState.update {
            it.copy(
                isCameraReady = false
            )
        }

        sendEffect(
            CameraUiEffect.ConfigureCamera(
                cameraFacing = state.cameraFacing,

                cameraSize = state.cameraSize
            )
        )
    }

    private fun cameraReady(
        event: CameraUiEvent.CameraReady
    ) {
        val current = _uiState.value

        val exposure = current.exposureIndex.coerceIn(
            event.exposureMin, event.exposureMax
        )

        _uiState.update {
            it.copy(
                hasFlash = event.hasFlash,

                isCameraReady = true,

                flashState = if (event.hasFlash) {
                    it.flashState
                } else {
                    FlashState.OFF
                },

                exposureMin = event.exposureMin,

                exposureMax = event.exposureMax,

                exposureIndex = exposure
            )
        }

        sendEffect(
            CameraUiEffect.SetFlash(
                _uiState.value.flashState
            )
        )

        sendEffect(
            CameraUiEffect.SetExposure(
                exposure
            )
        )
    }

    private fun switchCamera() {
        val state = _uiState.value

        if (state.isTakingPicture) {
            return
        }

        val facing = when (state.cameraFacing) {
            CameraFacing.BACK -> CameraFacing.FRONT

            CameraFacing.FRONT -> CameraFacing.BACK
        }

        _uiState.update {
            it.copy(
                cameraFacing = facing,

                flashState = FlashState.OFF,

                hasFlash = false,

                isCameraReady = false
            )
        }

        configureCamera()
    }

    private fun changeFlash() {
        val state = _uiState.value

        if (
            !state.isCameraReady ||
            state.isTakingPicture
        ) {
            return
        }

        if (!state.hasFlash) {
            sendEffect(
                CameraUiEffect.ShowMessage(
                    "Camera này không hỗ trợ Flash"
                )
            )

            return
        }

        val flash = when (state.flashState) {
            FlashState.OFF -> FlashState.ON

            FlashState.ON -> FlashState.AUTO

            FlashState.AUTO -> FlashState.OFF
        }

        _uiState.update {
            it.copy(
                flashState = flash
            )
        }

        sendEffect(
            CameraUiEffect.SetFlash(
                flash
            )
        )
    }

    private fun changeTimer() {
        val timer = when (_uiState.value.timerState) {
            TimerState.OFF -> TimerState.S3

            TimerState.S3 -> TimerState.S5

            TimerState.S5 -> TimerState.S10

            TimerState.S10 -> TimerState.OFF
        }

        _uiState.update {
            it.copy(
                timerState = timer
            )
        }
    }

    private fun changeCameraSize() {
        if (_uiState.value.isTakingPicture) {
            return
        }

        val size = when (_uiState.value.cameraSize) {
            CameraSize.S1_1 -> CameraSize.S4_3

            CameraSize.S4_3 -> CameraSize.S16_9

            CameraSize.S16_9 -> CameraSize.FULL

            CameraSize.FULL -> CameraSize.S1_1
        }

        _uiState.update {
            it.copy(
                cameraSize = size
            )
        }
    }

    private fun changeExposure(
        value: Int
    ) {
        val state = _uiState.value

        val exposure = value.coerceIn(
            state.exposureMin, state.exposureMax
        )

        _uiState.update {
            it.copy(
                exposureIndex = exposure
            )
        }

        sendEffect(
            CameraUiEffect.SetExposure(
                exposure
            )
        )
    }

    private fun capture() {
        val state = _uiState.value

        if (!state.isCameraReady || state.isTakingPicture) {
            return
        }

        _uiState.update {
            it.copy(
                showToolsPanel = false,
                showExposurePanel = false,
                showFilterPanel = false
            )
        }

        if (state.timerState == TimerState.OFF) {
            captureNow()
            return
        }

        startCountdown(
            state.timerState.seconds
        )
    }

    private fun captureNow() {
        _uiState.update {
            it.copy(
                isTakingPicture = true
            )
        }

        sendEffect(
            CameraUiEffect.CapturePhoto
        )
    }

    private fun startCountdown(
        seconds: Int
    ) {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isTakingPicture = true
                )
            }

            for (value in seconds downTo 1) {
                _uiState.update {
                    it.copy(
                        countdownValue = value
                    )
                }

                delay(
                    1000L
                )
            }

            _uiState.update {
                it.copy(
                    countdownValue = null
                )
            }

            sendEffect(
                CameraUiEffect.CapturePhoto
            )
        }
    }

    private fun sendEffect(
        effect: CameraUiEffect
    ) {
        _effect.trySend(
            effect
        )
    }
}
