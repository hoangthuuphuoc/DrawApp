package com.example.draw.camera.controller

import android.content.Context
import android.net.Uri
import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.draw.camera.model.CameraFacing
import com.example.draw.camera.model.CameraSize
import com.example.draw.camera.model.FlashState
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraXController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val analyzer: ImageAnalysis.Analyzer
) {

    private val mainExecutor = ContextCompat.getMainExecutor(
        context
    )

    private val analysisExecutor = Executors.newSingleThreadExecutor()

    private val controller = LifecycleCameraController(
        context
    )

    init {
        controller.setEnabledUseCases(
            CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
        )

        controller.setImageCaptureMode(
            ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
        )

        controller.setImageAnalysisBackpressureStrategy(
            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
        )

        controller.setImageAnalysisResolutionSelector(
            analysisResolutionSelector()
        )

        controller.setImageAnalysisAnalyzer(
            analysisExecutor, analyzer
        )

        controller.setTapToFocusEnabled(
            false
        )

        controller.setPinchToZoomEnabled(
            true
        )

        previewView.controller = controller
    }

    fun focusAt(
        x: Float, y: Float, onResult: (Boolean) -> Unit
    ) {
        val control = controller.cameraControl ?: run {
            onResult(false)
            return
        }

        val point = previewView.meteringPointFactory.createPoint(
                x, y
            )

        val action = FocusMeteringAction.Builder(
            point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
        ).setAutoCancelDuration(
                3, TimeUnit.SECONDS
            ).build()

        val future = control.startFocusAndMetering(
            action
        )

        future.addListener(
            {
                val successful = runCatching {
                    future.get().isFocusSuccessful
                }.getOrDefault(
                    false
                )

                onResult(
                    successful
                )
            }, mainExecutor
        )
    }

    fun bind(
        cameraFacing: CameraFacing, cameraSize: CameraSize, onReady: (
            Boolean, Int, Int
        ) -> Unit, onError: (Throwable) -> Unit
    ) {
        runCatching {
            controller.unbind()

            controller.setCameraSelector(
                when (cameraFacing) {
                    CameraFacing.BACK -> CameraSelector.DEFAULT_BACK_CAMERA

                    CameraFacing.FRONT -> CameraSelector.DEFAULT_FRONT_CAMERA
                }
            )

            val selector = resolutionSelector(
                cameraSize
            )

            controller.setPreviewResolutionSelector(
                selector
            )

            controller.setImageCaptureResolutionSelector(
                selector
            )

            controller.setImageAnalysisResolutionSelector(
                analysisResolutionSelector()
            )

            previewView.controller = controller

            controller.bindToLifecycle(
                lifecycleOwner
            )

            previewView.post {
                val info = controller.cameraInfo

                if (info == null) {
                    onError(
                        IllegalStateException(
                            "Camera chưa sẵn sàng"
                        )
                    )

                    return@post
                }

                val range = info.exposureState.exposureCompensationRange

                onReady(
                    info.hasFlashUnit(), range.lower, range.upper
                )
            }
        }.onFailure(
            onError
        )
    }

    fun observeZoom(
        onChanged: (
            ratio: Float, minRatio: Float, maxRatio: Float
        ) -> Unit
    ) {
        controller.zoomState.observe(
            lifecycleOwner
        ) { state ->

            state ?: return@observe

            onChanged(
                state.zoomRatio, state.minZoomRatio, state.maxZoomRatio
            )
        }
    }

    fun setZoom(
        ratio: Float
    ) {
        val state = controller.zoomState.value ?: return

        val safeRatio = ratio.coerceIn(
            state.minZoomRatio, state.maxZoomRatio
        )

        controller.setZoomRatio(
            safeRatio
        )
    }

    fun setFlash(
        flashState: FlashState
    ) {
        controller.setImageCaptureFlashMode(
            when (flashState) {
                FlashState.OFF -> ImageCapture.FLASH_MODE_OFF

                FlashState.ON -> ImageCapture.FLASH_MODE_ON

                FlashState.AUTO -> ImageCapture.FLASH_MODE_AUTO
            }
        )
    }

    fun setExposure(
        value: Int
    ) {
        val info = controller.cameraInfo ?: return

        val control = controller.cameraControl ?: return

        val range = info.exposureState.exposureCompensationRange

        val safeValue = value.coerceIn(
            range.lower, range.upper
        )

        control.setExposureCompensationIndex(
            safeValue
        )
    }

    fun takePhoto(
        onSuccess: (Uri) -> Unit, onError: (Throwable) -> Unit
    ) {
        val file = File(
            context.cacheDir, "camera_${System.currentTimeMillis()}.jpg"
        )

        val options = ImageCapture.OutputFileOptions.Builder(
                file
            ).build()

        controller.takePicture(
            options, mainExecutor, object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults: ImageCapture.OutputFileResults
                ) {
                    onSuccess(
                        Uri.fromFile(
                            file
                        )
                    )
                }

                override fun onError(
                    exception: ImageCaptureException
                ) {
                    onError(
                        exception
                    )
                }
            })
    }

    private fun analysisResolutionSelector(): ResolutionSelector {

        return ResolutionSelector.Builder().setResolutionStrategy(
                ResolutionStrategy(
                    Size(
                        640, 480
                    ), ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER
                )
            ).build()
    }

    private fun resolutionSelector(
        cameraSize: CameraSize
    ): ResolutionSelector {

        val ratio = when (cameraSize) {
            CameraSize.S16_9 -> AspectRatio.RATIO_16_9

            CameraSize.S1_1, CameraSize.S4_3, CameraSize.FULL -> AspectRatio.RATIO_4_3
        }

        return ResolutionSelector.Builder().setAspectRatioStrategy(
                AspectRatioStrategy(
                    ratio, AspectRatioStrategy.FALLBACK_RULE_AUTO
                )
            ).build()
    }

    fun close() {
        controller.clearImageAnalysisAnalyzer()

        controller.unbind()

        previewView.controller = null

        analysisExecutor.shutdown()
    }
}