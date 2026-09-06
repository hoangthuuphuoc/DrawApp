package com.example.draw.camera.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.draw.R
import com.example.draw.camera.adapter.FaceFilterAdapter
import com.example.draw.camera.controller.CameraXController
import com.example.draw.camera.detector.FaceDetectorManager
import com.example.draw.camera.filter.CameraColorFilterFactory
import com.example.draw.camera.filter.DrawableBitmapLoader
import com.example.draw.camera.filter.FaceFilterRepository
import com.example.draw.camera.model.CameraColorFilter
import com.example.draw.camera.model.CameraSize
import com.example.draw.camera.model.FilterMode
import com.example.draw.camera.model.FlashState
import com.example.draw.camera.processor.CameraCaptureProcessor
import com.example.draw.databinding.ActivityCameraBinding
import com.example.draw.sticker.ui.StickerActivity
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var lastColorFilter: CameraColorFilter? = null

    private val viewModel: CameraViewModel by viewModels()

    private lateinit var faceDetector: FaceDetectorManager

    private lateinit var cameraController: CameraXController

    private lateinit var captureProcessor: CameraCaptureProcessor

    private lateinit var filterAdapter: FaceFilterAdapter

    private var lastCameraSize: CameraSize? = null

    private var lastFilterMode: FilterMode? = null

    private var lastOverlayMode: FilterMode? = null

    private var lastFilterRes: Int? = null

    private var filterInitialized = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        viewModel.onEvent(
            CameraUiEvent.PermissionResult(
                granted
            )
        )
    }
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        uri ?: return@registerForActivityResult

        viewModel.onEvent(
            CameraUiEvent.GalleryImageSelected(
                uri
            )
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(
            window, false
        )

        binding = ActivityCameraBinding.inflate(
            layoutInflater
        )

        setContentView(
            binding.root
        )
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.flTopCamera
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.updateLayoutParams<ViewGroup.LayoutParams> {
                height = dp(84) + systemBars.top
            }

            view.updatePadding(
                top = systemBars.top
            )

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.flBottomCamera
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.updateLayoutParams<ViewGroup.LayoutParams> {
                height = dp(190) + systemBars.bottom
            }

            view.updatePadding(
                bottom = systemBars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(
            binding.root
        )
        setupTapFocus()

        setupFaceDetector()

        setupCamera()

        setupDynamicFrame()

        setupCaptureProcessor()

        setupFilters()

        setupClicks()

        setupExposure()

        setupBackPressed()

        observeState()

        observeEffect()

        viewModel.onEvent(
            CameraUiEvent.ScreenStarted
        )
    }

    private fun setupDynamicFrame() {
        binding.overlayView.setOnFrameChangedListener { frame ->
            updateDynamicFrame(
                frame
            )
        }
    }

    private fun updateDynamicFrame(
        frame: RectF
    ) {
        val screenHeight = binding.overlayView.height

        if (screenHeight <= 0) {
            return
        }

        val topHeight = frame.top.roundToInt().coerceAtLeast(0)

        val bottomHeight = (screenHeight - frame.bottom).roundToInt().coerceAtLeast(0)

        binding.viewTopShade.updateLayoutParams<ViewGroup.LayoutParams> {
            height = topHeight
        }

        binding.viewBottomShade.updateLayoutParams<ViewGroup.LayoutParams> {
            height = bottomHeight
        }

        updateZoomPosition(
            frame
        )
    }

    private fun updateZoomPosition(
        frame: RectF
    ) {
        val zoomView = binding.llZoomControls

        val applyPosition = {
            val zoomHeight = zoomView.height

            if (zoomHeight > 0) {
                val margin = dp(12)

                val maxY =
                    binding.overlayView.height - binding.flBottomCamera.height - zoomHeight - margin

                val frameY = frame.bottom - zoomHeight - margin

                zoomView.y = minOf(
                    frameY, maxY.toFloat()
                ).coerceAtLeast(
                    margin.toFloat()
                )
            }
        }

        if (zoomView.height > 0) {
            applyPosition()
        } else {
            zoomView.post {
                applyPosition()
            }
        }
    }

    private fun dp(
        value: Int
    ): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    private fun setupFaceDetector() {
        faceDetector = FaceDetectorManager(
            context = this,

            onFacesDetected = { faces ->
                binding.faceOverlayView.updateFaces(
                    faces
                )
            },

            onError = {
                viewModel.onEvent(
                    CameraUiEvent.CameraError(
                        it.message ?: "Không thể nhận diện khuôn mặt"
                    )
                )
            })
    }

    private fun setupCamera() {
        cameraController = CameraXController(
            context = this,
            lifecycleOwner = this,
            previewView = binding.previewView,
            analyzer = faceDetector.analyzer
        )
        cameraController.observeZoom { ratio, minRatio, maxRatio ->

            viewModel.onEvent(
                CameraUiEvent.ZoomChanged(
                    ratio = ratio,
                )
            )
        }
    }

    private fun setupCaptureProcessor() {
        captureProcessor = CameraCaptureProcessor(
            this
        )
    }

    private fun setupFilters() {
        filterAdapter = FaceFilterAdapter { item ->

            viewModel.onEvent(
                CameraUiEvent.FilterSelected(
                    item.filterRes
                )
            )
        }

        binding.rvFilters.apply {
            layoutManager = LinearLayoutManager(
                this@CameraActivity, LinearLayoutManager.HORIZONTAL, false
            )

            adapter = filterAdapter
        }
    }

    private fun setupClicks() {
        binding.cardBack.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.BackClicked
            )
        }

        binding.cardCapture.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.CaptureClicked
            )
        }
        binding.cardGallery.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.GalleryClicked
            )
        }

        binding.cardSwitchCamera.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.SwitchCameraClicked
            )
        }

        binding.cardFlash.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.FlashClicked
            )
        }

        binding.toolFlash.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.FlashClicked
            )
        }

        binding.cardTimer.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.TimerClicked
            )
        }

        binding.toolTimer.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.TimerClicked
            )
        }

        binding.cardTools.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ToolsClicked
            )
        }

        binding.toolExposure.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ExposureClicked
            )
        }

        binding.cardFilter.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.FilterClicked
            )
        }

        binding.cardCameraSize.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.CameraSizeClicked
            )
        }

        binding.toolFrame.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.CameraSizeClicked
            )
        }

        binding.cardGrid.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.GridClicked
            )
        }

        binding.toolGrid.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.GridClicked
            )
        }

        binding.tvFilterBack.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ClosePanelClicked
            )
        }

        binding.tvFilterHead.setOnClickListener {
            selectFilterMode(
                FilterMode.HEAD
            )
        }

        binding.tvFilterFace.setOnClickListener {
            selectFilterMode(
                FilterMode.FACE
            )
        }

        binding.tvFilterEyes.setOnClickListener {
            selectFilterMode(
                FilterMode.EYES
            )
        }

        binding.tvFilterNose.setOnClickListener {
            selectFilterMode(
                FilterMode.NOSE
            )
        }

        binding.tvFilterMouth.setOnClickListener {
            selectFilterMode(
                FilterMode.MOUTH
            )
        }

        binding.tvZoomWide.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ZoomChanged(
                    0.5f
                )
            )
        }

        binding.tvZoomMain.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ZoomChanged(
                    1f
                )
            )
        }
        binding.tvZoom2x.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ZoomChanged(
                    2f
                )
            )
        }
        binding.toolFilter.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ColorFilterClicked
            )
        }
        binding.tvColorFilterBack.setOnClickListener {
            viewModel.onEvent(
                CameraUiEvent.ClosePanelClicked
            )
        }

        binding.tvFilterOriginal.setOnClickListener {
            selectColorFilter(
                CameraColorFilter.ORIGINAL
            )
        }

        binding.tvFilterWarm.setOnClickListener {
            selectColorFilter(
                CameraColorFilter.WARM
            )
        }

        binding.tvFilterCool.setOnClickListener {
            selectColorFilter(
                CameraColorFilter.COOL
            )
        }

        binding.tvFilterBw.setOnClickListener {
            selectColorFilter(
                CameraColorFilter.BLACK_WHITE
            )
        }

        binding.tvFilterVintage.setOnClickListener {
            selectColorFilter(
                CameraColorFilter.VINTAGE
            )
        }

        binding.tvFilterFade.setOnClickListener {
            selectColorFilter(
                CameraColorFilter.FADE
            )
        }
    }

    private fun selectColorFilter(
        filter: CameraColorFilter
    ) {
        viewModel.onEvent(
            CameraUiEvent.ColorFilterSelected(
                filter
            )
        )
    }

    private fun selectFilterMode(
        mode: FilterMode
    ) {
        viewModel.onEvent(
            CameraUiEvent.FilterModeChanged(
                mode
            )
        )
    }

    private fun setupExposure() {
        binding.seekExposure.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(
                seekBar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                if (!fromUser) {
                    return
                }

                val state = viewModel.uiState.value

                viewModel.onEvent(
                    CameraUiEvent.ExposureChanged(
                        state.exposureMin + progress
                    )
                )
            }

            override fun onStartTrackingTouch(
                seekBar: SeekBar?
            ) = Unit

            override fun onStopTrackingTouch(
                seekBar: SeekBar?
            ) = Unit
        })
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    viewModel.onEvent(
                        CameraUiEvent.BackClicked
                    )
                }
            })
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uiState.collect { state ->

                    renderState(
                        state
                    )
                }
            }
        }
    }

    private fun renderState(
        state: CameraUiState
    ) {
        renderCameraState(
            state
        )

        renderPanels(
            state
        )

        renderFlash(
            state
        )

        renderTimer(
            state
        )

        renderCameraSize(
            state
        )

        renderGrid(
            state
        )

        renderExposure(
            state
        )

        renderZoom(
            state
        )

        renderFilters(
            state
        )
        renderColorFilter(
            state
        )
    }

    private fun renderCameraState(
        state: CameraUiState
    ) {
        binding.progressCamera.visibility = if (state.isCameraReady) {
            View.GONE
        } else {
            View.VISIBLE
        }

        binding.cardCapture.isEnabled = state.isCameraReady && !state.isTakingPicture

        binding.cardCapture.alpha = if (state.isTakingPicture) {
            0.45f
        } else {
            1f
        }

        binding.cardSwitchCamera.isEnabled = !state.isTakingPicture

        binding.cardTimer.isEnabled = !state.isTakingPicture

        binding.cardTools.isEnabled = !state.isTakingPicture

        binding.overlayView.setCountdown(
            state.countdownValue
        )
    }


    private fun renderPanels(
        state: CameraUiState
    ) {
        binding.cardToolsPanel.visibility = if (state.showToolsPanel) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.llBrightness.visibility = if (state.showExposurePanel) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.llFilterControls.visibility = if (state.showFilterPanel) {
            View.VISIBLE
        } else {
            View.GONE
        }

        val hideZoom =
            state.isTakingPicture || state.showToolsPanel || state.showExposurePanel || state.showFilterPanel || state.showColorFilterPanel

        binding.llZoomControls.visibility = if (hideZoom) {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.llColorFilterControls.visibility = if (state.showColorFilterPanel) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun renderColorFilter(
        state: CameraUiState
    ) {
        if (lastColorFilter == state.colorFilter) {
            return
        }

        lastColorFilter = state.colorFilter

        if (state.colorFilter == CameraColorFilter.ORIGINAL) {
            binding.previewView.setLayerType(
                View.LAYER_TYPE_NONE, null
            )

            return
        }

        val matrix = CameraColorFilterFactory.create(
            state.colorFilter
        )

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(
                matrix
            )
        }

        binding.previewView.setLayerType(
            View.LAYER_TYPE_HARDWARE, paint
        )
    }

    private fun renderFlash(
        state: CameraUiState
    ) {
        binding.cardFlash.isEnabled = state.hasFlash && !state.isTakingPicture

        binding.cardFlash.alpha = if (state.hasFlash) {
            1f
        } else {
            0.35f
        }

        when (state.flashState) {
            FlashState.OFF -> {
                binding.tvFlash.text = "⚡"

                binding.tvFlash.setTextColor(
                    Color.WHITE
                )
            }

            FlashState.ON -> {
                binding.tvFlash.text = "⚡"

                binding.tvFlash.setTextColor(
                    Color.parseColor(
                        "#FFD600"
                    )
                )
            }

            FlashState.AUTO -> {
                binding.tvFlash.text = "A"

                binding.tvFlash.setTextColor(
                    Color.parseColor(
                        "#FFD600"
                    )
                )
            }
        }
    }

    private fun renderTimer(
        state: CameraUiState
    ) {
        binding.tvTimer.text = if (state.timerState.seconds == 0) {
            "0s"
        } else {
            "${state.timerState.seconds}s"
        }

        binding.tvTimer.setTextColor(
            if (state.timerState.seconds == 0) {
                Color.WHITE
            } else {
                Color.parseColor(
                    "#FFD600"
                )
            }
        )
    }

    private fun renderCameraSize(
        state: CameraUiState
    ) {
        if (lastCameraSize != state.cameraSize) {
            lastCameraSize = state.cameraSize

            binding.overlayView.setCameraSize(
                state.cameraSize
            )
        }

        binding.tvCameraSize.text = when (state.cameraSize) {
            CameraSize.S1_1 -> "1:1"

            CameraSize.S4_3 -> "4:3"

            CameraSize.S16_9 -> "16:9"

            CameraSize.FULL -> "FULL"
        }
    }

    private fun renderGrid(
        state: CameraUiState
    ) {
        binding.overlayView.setGrid(
            state.gridEnabled
        )

        binding.tvGrid.setTextColor(
            if (state.gridEnabled) {
                Color.parseColor(
                    "#FFD600"
                )
            } else {
                Color.WHITE
            }
        )
    }

    private fun renderExposure(
        state: CameraUiState
    ) {
        val range = state.exposureMax - state.exposureMin

        binding.seekExposure.max = range.coerceAtLeast(
            0
        )

        val progress = state.exposureIndex - state.exposureMin

        if (binding.seekExposure.progress != progress) {
            binding.seekExposure.progress = progress.coerceIn(
                0, range.coerceAtLeast(0)
            )
        }

        binding.seekExposure.isEnabled = range > 0
    }

    private fun renderZoom(
        state: CameraUiState
    ) {
        val yellow = Color.parseColor(
            "#FFD600"
        )

        val white = Color.WHITE

        binding.tvZoomWide.visibility = if (state.zoomMinRatio <= 0.5f) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.tvZoomMain.visibility = if (state.zoomMinRatio <= 1f && state.zoomMaxRatio >= 1f) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.tvZoom2x.visibility = if (state.zoomMaxRatio >= 2f) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.tvZoomWide.setTextColor(
            if (isNearZoom(
                    state.zoomRatio, 0.5f
                )
            ) {
                yellow
            } else {
                white
            }
        )

        binding.tvZoomMain.setTextColor(
            if (isNearZoom(
                    state.zoomRatio, 1f
                )
            ) {
                yellow
            } else {
                white
            }
        )

        binding.tvZoom2x.setTextColor(
            if (isNearZoom(
                    state.zoomRatio, 2f
                )
            ) {
                yellow
            } else {
                white
            }
        )
    }

    private fun isNearZoom(
        first: Float, second: Float
    ): Boolean {
        return kotlin.math.abs(
            first - second
        ) < 0.08f
    }

    private fun renderFilters(
        state: CameraUiState
    ) {
        if (lastFilterMode != state.filterMode) {
            lastFilterMode = state.filterMode

            filterAdapter.submitList(
                FaceFilterRepository.getFilters(
                    state.filterMode
                )
            )
        }

        filterAdapter.setSelected(
            state.selectedFilterRes
        )

        if (!filterInitialized || lastOverlayMode != state.filterMode || lastFilterRes != state.selectedFilterRes) {
            filterInitialized = true

            lastOverlayMode = state.filterMode

            lastFilterRes = state.selectedFilterRes

            val bitmap = state.selectedFilterRes?.let { resource ->

                DrawableBitmapLoader.load(
                    context = this, resource = resource
                )
            }

            binding.faceOverlayView.updateFilter(
                mode = state.filterMode,

                bitmap = bitmap
            )
        }

        binding.tvFilterHead.setBackgroundResource(
            R.drawable.bg_filter_category
        )

        binding.tvFilterFace.setBackgroundResource(
            R.drawable.bg_filter_category
        )

        binding.tvFilterEyes.setBackgroundResource(
            R.drawable.bg_filter_category
        )

        binding.tvFilterNose.setBackgroundResource(
            R.drawable.bg_filter_category
        )

        binding.tvFilterMouth.setBackgroundResource(
            R.drawable.bg_filter_category
        )

        val selectedView = when (state.filterMode) {
            FilterMode.HEAD -> binding.tvFilterHead

            FilterMode.FACE -> binding.tvFilterFace

            FilterMode.EYES -> binding.tvFilterEyes

            FilterMode.NOSE -> binding.tvFilterNose

            FilterMode.MOUTH -> binding.tvFilterMouth
        }

        selectedView.setBackgroundResource(
            R.drawable.bg_filter_category_selected
        )
    }

    private fun observeEffect() {
        lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.effect.collect { effect ->

                    handleEffect(
                        effect
                    )
                }
            }
        }
    }

    private fun handleEffect(
        effect: CameraUiEffect
    ) {
        when (effect) {
            CameraUiEffect.RequestCameraPermission -> {
                requestCameraPermission()
            }

            is CameraUiEffect.ConfigureCamera -> {
                binding.faceOverlayView.clearFaces()

                cameraController.bind(
                    cameraFacing = effect.cameraFacing,

                    cameraSize = effect.cameraSize,

                    onReady = { hasFlash, exposureMin, exposureMax ->

                        viewModel.onEvent(
                            CameraUiEvent.CameraReady(
                                hasFlash = hasFlash,

                                exposureMin = exposureMin,

                                exposureMax = exposureMax
                            )
                        )
                    },

                    onError = { throwable ->

                        viewModel.onEvent(
                            CameraUiEvent.CameraError(
                                throwable.message ?: "Không thể mở Camera"
                            )
                        )
                    })
            }

            is CameraUiEffect.SetZoom -> {
                cameraController.setZoom(
                    effect.ratio
                )
            }

            is CameraUiEffect.SetFlash -> {
                cameraController.setFlash(
                    effect.flashState
                )
            }

            is CameraUiEffect.SetExposure -> {
                cameraController.setExposure(
                    effect.value
                )
            }


            CameraUiEffect.CapturePhoto -> {
                cameraController.takePhoto(
                    onSuccess = { uri ->

                        viewModel.onEvent(
                            CameraUiEvent.PhotoSaved(
                                uri
                            )
                        )
                    },

                    onError = { throwable ->

                        viewModel.onEvent(
                            CameraUiEvent.CameraError(
                                throwable.message ?: "Chụp ảnh thất bại"
                            )
                        )
                    })
            }

            is CameraUiEffect.ProcessCapturedPhoto -> {
                captureProcessor.process(
                    inputUri = effect.uri,

                    cameraSize = effect.cameraSize,

                    filterMode = effect.filterMode,

                    filterRes = effect.filterRes,

                    onSuccess = { uri ->

                        viewModel.onEvent(
                            CameraUiEvent.PhotoProcessed(
                                uri
                            )
                        )
                    },

                    onError = { throwable ->

                        viewModel.onEvent(
                            CameraUiEvent.CameraError(
                                throwable.message ?: "Xử lý ảnh thất bại"
                            )
                        )
                    })
            }

            is CameraUiEffect.OpenEditor -> {
                openEditor(
                    effect.uri
                )
            }

            is CameraUiEffect.ShowMessage -> {
                Toast.makeText(
                    this, effect.message, Toast.LENGTH_SHORT
                ).show()
            }

            CameraUiEffect.CloseScreen -> {
                finish()
            }

            CameraUiEffect.OpenGallery -> {
                galleryLauncher.launch(
                    "image/*"
                )
            }
        }
    }

    private fun requestCameraPermission() {
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            viewModel.onEvent(
                CameraUiEvent.PermissionResult(
                    true
                )
            )

            return
        }

        permissionLauncher.launch(
            Manifest.permission.CAMERA
        )
    }

    private fun openEditor(
        uri: Uri
    ) {
        startActivity(
            Intent(
                this, StickerActivity::class.java
            ).apply {
                putExtra(
                    "image", uri.toString()
                )
            })
    }

    override fun onDestroy() {
        if (::cameraController.isInitialized) {
            cameraController.close()
        }

        if (::faceDetector.isInitialized) {
            faceDetector.close()
        }

        if (::captureProcessor.isInitialized) {
            captureProcessor.close()
        }

        super.onDestroy()
    }

    private fun setupTapFocus() {
        val detector = GestureDetector(
            this, object : GestureDetector.SimpleOnGestureListener() {

                override fun onSingleTapConfirmed(
                    event: MotionEvent
                ): Boolean {
                    val state = viewModel.uiState.value

                    if (!state.isCameraReady || state.isTakingPicture) {
                        return false
                    }

                    showFocusIndicator(
                        event.x, event.y
                    )

                    cameraController.focusAt(
                        event.x, event.y
                    ) {
                        hideFocusIndicator()
                    }

                    return true
                }
            })

        binding.previewView.setOnTouchListener { _, event ->

            detector.onTouchEvent(
                event
            )

            false
        }
    }

    private fun showFocusIndicator(
        touchX: Float, touchY: Float
    ) {
        val indicator = binding.viewFocusIndicator

        indicator.animate().cancel()

        indicator.visibility = View.VISIBLE

        indicator.alpha = 1f

        indicator.scaleX = 1.35f

        indicator.scaleY = 1.35f

        indicator.x = touchX - indicator.width / 2f

        indicator.y = touchY - indicator.height / 2f

        indicator.animate().scaleX(
            1f
        ).scaleY(
            1f
        ).setDuration(
            180L
        ).start()
    }

    private fun hideFocusIndicator() {
        val indicator = binding.viewFocusIndicator

        indicator.animate().cancel()

        indicator.animate().alpha(
            0f
        ).setStartDelay(
            350L
        ).setDuration(
            250L
        ).withEndAction {
            indicator.visibility = View.GONE

            indicator.alpha = 1f
        }.start()
    }
}
