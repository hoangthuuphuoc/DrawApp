package com.example.draw.sticker.ui

import StickerUiEvent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.draw.R
import com.example.draw.adapter.CropAdapter
import com.example.draw.adapter.StickerAdapter
import com.example.draw.core.image.BitmapManager
import com.example.draw.data.reponsitory.CropRepository
import com.example.draw.data.reponsitory.FontRepository
import com.example.draw.data.reponsitory.StickerRepository
import com.example.draw.databinding.ActivitySticker1Binding
import com.example.draw.draw.ui.DrawTool
import com.example.draw.draw.ui.DrawUiEffect
import com.example.draw.draw.ui.DrawUiEvent
import com.example.draw.draw.ui.DrawUiState
import com.example.draw.draw.ui.DrawViewModel
import com.example.draw.sticker.adapter.FontAdapter
import com.example.draw.sticker.listener.TextSizeSeekBarListener
import com.example.draw.sticker.model.StickerCategory
import com.yalantis.ucrop.callback.BitmapCropCallback
import com.yalantis.ucrop.view.CropImageView
import com.yalantis.ucrop.view.OverlayView
import kotlinx.coroutines.launch
import java.io.File

class StickerActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySticker1Binding.inflate(layoutInflater)
    }
    private val stickerViewModel: StickerViewModel by viewModels()
    private val drawViewModel: DrawViewModel by viewModels()

    private val stickerList = StickerRepository.listSticker
    private val fontRepository = FontRepository()
    private val cropRepository = CropRepository()

    private lateinit var stickerAdapter: StickerAdapter

    private val bitmapManager by lazy {
        BitmapManager(this)
    }

    private var currentImage: Uri? = null
    private var currentMode = EditorMode.MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupWindowInsets()
        loadImage()
        setupClickListeners()
        setupRecyclerViews()
        observeViewModels()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right, systemBars.bottom
            )

            insets
        }
    }

    private fun setupClickListeners() {
        setupNavigationClicks()
        setupMainToolClicks()
        setupTextClicks()
        setupStickerClicks()
        setupDrawClicks()
        setupSaveClick()
    }

    private fun setupRecyclerViews() {
        setupStickerRecycler()
        setupCropRecycler()
        setupFontRecycler()
    }

    private fun setupNavigationClicks() {
        binding.ivBack.setOnClickListener {
            when (currentMode) {
                EditorMode.CROP -> closeCropMode()

                EditorMode.DRAW -> closeDrawMode()

                EditorMode.MAIN -> finish()

                else -> showMode(EditorMode.MAIN)
            }
        }

        binding.btnTick.setOnClickListener {
            when (currentMode) {
                EditorMode.STICKER -> {
                    applyCurrent()
                    showMode(EditorMode.MAIN)
                }

                EditorMode.TEXT -> {
                    applyCurrent()
                    showMode(EditorMode.MAIN)
                }

                EditorMode.DRAW -> {
                    applyCurrent()
                    closeDrawMode()
                }

                EditorMode.CROP -> {
                    executeCrop()
                }

                else -> Unit
            }
        }
    }

    private fun setupMainToolClicks() {
        binding.llAddText.setOnClickListener {
            stickerViewModel.onEvent(
                StickerUiEvent.AddTextClicked
            )
        }

        binding.llSticker.setOnClickListener {
            showMode(EditorMode.STICKER)

            stickerViewModel.onEvent(
                StickerUiEvent.AddBitmapClicked
            )
        }

        binding.llDrawMain.setOnClickListener {
            openDrawMode()
        }

        binding.llCrop.setOnClickListener {
            stickerViewModel.onEvent(
                StickerUiEvent.CropClicked
            )
        }
    }

    private fun setupTextClicks() {
        binding.llTextColor.setOnClickListener {
            stickerViewModel.onEvent(
                StickerUiEvent.TextColorClicked
            )
        }

        binding.llTextSize.setOnClickListener {
            stickerViewModel.onEvent(
                StickerUiEvent.TextSizeClicked
            )
        }

        binding.llTextStroke.setOnClickListener {
            stickerViewModel.onEvent(
                StickerUiEvent.TextStrokeClicked
            )
        }

        binding.llTextFont.setOnClickListener {
            stickerViewModel.onEvent(
                StickerUiEvent.TextFontClicked
            )
        }

        binding.vStrokeColor.setOnClickListener {
            showStrokeColorDialog()
        }

        binding.ivBackSize.setOnClickListener {
            showMode(EditorMode.TEXT)
        }

        binding.ivBackFont.setOnClickListener {
            showMode(EditorMode.TEXT)
        }

        binding.ivBackStroke.setOnClickListener {
            showMode(EditorMode.TEXT)
        }

        binding.llTextBack.setOnClickListener {
            showMode(EditorMode.MAIN)
        }

        setupTextSizeSeekBar()
        setupTextStrokeSeekBar()
    }

    private fun setupTextSizeSeekBar() {
        binding.sbTextSize.setOnSeekBarChangeListener(
            TextSizeSeekBarListener { size ->
                stickerViewModel.onEvent(
                    StickerUiEvent.TextSizeSelected(
                        size
                    )
                )
            })
    }

    private fun setupTextStrokeSeekBar() {
        binding.sbTextStroke.setOnSeekBarChangeListener(
            TextSizeSeekBarListener { width ->
                stickerViewModel.onEvent(
                    StickerUiEvent.TextStrokeWidthSelected(
                        width.toFloat()
                    )
                )
            })
    }

    private fun setupStickerClicks() {
        binding.tvStickerAll.setOnClickListener {
            selectStickerCategory(
                StickerCategory.ALL
            )
        }

        binding.tvStickerEmoji.setOnClickListener {
            selectStickerCategory(
                StickerCategory.EMOJI
            )
        }

        binding.tvStickerNature.setOnClickListener {
            selectStickerCategory(
                StickerCategory.NATURE
            )
        }

        binding.tvStickerArt.setOnClickListener {
            selectStickerCategory(
                StickerCategory.ART
            )
        }

        binding.ivBackSticker.setOnClickListener {
            showMode(EditorMode.MAIN)
        }
    }

    private fun selectStickerCategory(
        category: StickerCategory
    ) {
        stickerViewModel.onEvent(
            StickerUiEvent.StickerCategoryClicked(
                category.value
            )
        )
    }

    private fun setupDrawClicks() {
        binding.btnPencil.setOnClickListener {
            selectDrawTool(
                DrawTool.PENCIL
            )
        }

        binding.btnPen.setOnClickListener {
            selectDrawTool(
                DrawTool.PEN
            )
        }

        binding.btnMarker.setOnClickListener {
            selectDrawTool(
                DrawTool.MARKER
            )
        }

        binding.btnEraser.setOnClickListener {
            selectDrawTool(
                DrawTool.ERASER
            )
        }

        binding.btnPen5.setOnClickListener {
            selectDrawTool(
                DrawTool.PEN_5
            )
        }

        binding.btnColor.setOnClickListener {
            drawViewModel.onEvent(
                DrawUiEvent.ColorClicked
            )
        }

        binding.btnUndo.setOnClickListener {
            drawViewModel.onEvent(
                DrawUiEvent.UndoClicked
            )
        }

        binding.btnRedo.setOnClickListener {
            drawViewModel.onEvent(
                DrawUiEvent.RedoClicked
            )
        }

        binding.ivBackDraw.setOnClickListener {
            closeDrawMode()
        }

        binding.btnAdd.setOnClickListener {
            closeDrawMode()
        }
    }

    private fun selectDrawTool(
        tool: DrawTool
    ) {
        drawViewModel.onEvent(
            DrawUiEvent.ToolSelected(
                tool.value
            )
        )
    }

    private fun openDrawMode() {
        showMode(EditorMode.DRAW)

        drawViewModel.onEvent(
            DrawUiEvent.OpenDraw
        )

        binding.flTool.isVisible = false
        binding.llDraw.isVisible = true

        binding.svSticker.setTouchEnabled(false)
        binding.drawingView.setTouchEnabled(true)
    }

    private fun closeDrawMode() {
        showMode(EditorMode.MAIN)

        binding.flTool.isVisible = true
        binding.llDraw.isVisible = false

        binding.btnUndo.isVisible = false
        binding.btnRedo.isVisible = false

        binding.svSticker.setTouchEnabled(true)
        binding.drawingView.setTouchEnabled(false)
    }

    private fun setupStickerRecycler() {
        stickerAdapter = StickerAdapter(
            stickerList
        ) { item ->
            stickerViewModel.onEvent(
                StickerUiEvent.StickerSelected(
                    item.image
                )
            )
        }

        binding.rvSticker.apply {
            layoutManager = createHorizontalLayoutManager()

            adapter = stickerAdapter
        }
    }

    private fun setupCropRecycler() {
        binding.rvCrop.apply {
            layoutManager = createHorizontalLayoutManager()

            adapter = CropAdapter(
                cropRepository.listCrop
            ) { item ->
                stickerViewModel.onEvent(
                    StickerUiEvent.CropRatioSelected(
                        width = item.width, height = item.height, name = item.name
                    )
                )
            }
        }
    }

    private fun setupFontRecycler() {
        binding.rvFont.apply {
            layoutManager = createHorizontalLayoutManager()

            adapter = FontAdapter(
                fontRepository.fontList
            ) { item ->
                stickerViewModel.onEvent(
                    StickerUiEvent.TextFontSelected(
                        item.font
                    )
                )
            }
        }
    }

    private fun createHorizontalLayoutManager() = LinearLayoutManager(
        this, LinearLayoutManager.HORIZONTAL, false
    )

    private fun observeViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    stickerViewModel.uiState.collect { state ->
                        renderStickerState(
                            state
                        )
                    }
                }

                launch {
                    stickerViewModel.uiEffect.collect { effect ->
                        handleStickerEffect(
                            effect
                        )
                    }
                }

                launch {
                    drawViewModel.uiState.collect { state ->
                        renderDrawState(
                            state
                        )
                    }
                }

                launch {
                    drawViewModel.uiEffect.collect { effect ->
                        handleDrawEffect(
                            effect
                        )
                    }
                }
            }
        }
    }

    private fun renderStickerState(
        state: StickerUiState
    ) {
        val category = state.stickerUiState.selectedCategory

        updateCategoryTabUi(
            category
        )

        updateStickerList(
            category
        )
    }

    private fun updateStickerList(
        selectedCategory: String
    ) {
        val stickers = if (selectedCategory == StickerCategory.ALL.value) {
            stickerList
        } else {
            stickerList.filter { sticker ->
                sticker.type == selectedCategory
            }
        }

        stickerAdapter.submitList(
            stickers
        )
    }

    private fun updateCategoryTabUi(
        selectedCategory: String
    ) {
        val tabs = mapOf(
            StickerCategory.ALL to binding.tvStickerAll,

            StickerCategory.EMOJI to binding.tvStickerEmoji,

            StickerCategory.NATURE to binding.tvStickerNature,

            StickerCategory.ART to binding.tvStickerArt
        )

        tabs.forEach { (category, textView) ->
            val isSelected = category.value == selectedCategory

            textView.setBackgroundResource(
                if (isSelected) {
                    R.drawable.bg_category_null
                } else {
                    R.drawable.bg_sticker_category
                }
            )

            textView.setTextColor(
                if (isSelected) {
                    Color.YELLOW
                } else {
                    Color.WHITE
                }
            )
        }
    }

    private fun handleStickerEffect(
        effect: StickerUiEffect
    ) {
        when (effect) {
            StickerUiEffect.ShowTextDialog -> {
                showTextDialog()
            }

            is StickerUiEffect.AddTextSticker -> {
                addTextSticker(
                    effect
                )
            }

            is StickerUiEffect.AddBitmapSticker -> {
                addBitmapSticker(
                    effect
                )
            }

            StickerUiEffect.ShowColorDialog -> {
                showTextColorDialog()
            }

            is StickerUiEffect.ChangeTextColor -> {
                binding.svSticker.setTextColor(
                    effect.color
                )
            }

            StickerUiEffect.ShowTextSize -> {
                showMode(
                    EditorMode.TEXT_SIZE
                )
            }

            is StickerUiEffect.ChangeTextSize -> {
                binding.svSticker.setTextSize(
                    effect.size
                )
            }

            StickerUiEffect.ShowTextStroke -> {
                showMode(
                    EditorMode.TEXT_STROKE
                )
            }

            is StickerUiEffect.ChangeTextStrokeWidth -> {
                binding.svSticker.setTextStrokeWidth(
                    effect.width
                )
            }

            is StickerUiEffect.ChangeTextStrokeColor -> {
                binding.svSticker.setTextStrokeColor(
                    effect.color
                )
            }

            StickerUiEffect.ShowTextFont -> {
                showMode(
                    EditorMode.TEXT_FONT
                )
            }

            is StickerUiEffect.ChangeTextFont -> {
                changeTextFont(
                    effect.font
                )
            }

            StickerUiEffect.ShowCrop -> {
                showMode(
                    EditorMode.CROP
                )
            }

            is StickerUiEffect.StartCrop -> {
                startCrop(
                    effect.width, effect.height
                )
            }
        }
    }

    private fun addTextSticker(
        effect: StickerUiEffect.AddTextSticker
    ) {
        val color = ContextCompat.getColor(
            this, effect.color
        )

        binding.svSticker.addTextSticker(
            effect.text, color
        )

        binding.svSticker.setTextStrokeWidth(
            0f
        )

        showMode(
            EditorMode.TEXT
        )
    }

    private fun addBitmapSticker(
        effect: StickerUiEffect.AddBitmapSticker
    ) {
        val bitmap = BitmapFactory.decodeResource(
            resources, effect.image
        )

        val size = dpToPx(
            150
        )

        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap, size, size, true
        )

        binding.svSticker.addBitmapSticker(
            scaledBitmap
        )
    }
    private fun dpToPx(
        dp: Int
    ): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun changeTextFont(
        fontRes: Int
    ) {
        val typeface = ResourcesCompat.getFont(
            this, fontRes
        ) ?: return

        binding.svSticker.setTextFont(
            typeface
        )
    }

    private fun renderDrawState(
        state: DrawUiState
    ) {
        binding.drawingView.setTool(
            state.selectedTool
        )
        binding.drawingView.setColor(
            state.selectedColor
        )

        renderDrawMode(
            state.isDrawMode
        )
        renderSelectedDrawTool(
            state.selectedTool
        )
    }
    private fun renderDrawMode(
        isDrawMode: Boolean
    ) {
        binding.flTool.isVisible = !isDrawMode

        binding.llDraw.isVisible = isDrawMode

        binding.btnUndo.isVisible = isDrawMode

        binding.btnRedo.isVisible = isDrawMode

        binding.svSticker.setTouchEnabled(
            !isDrawMode
        )

        binding.drawingView.setTouchEnabled(
            isDrawMode
        )
    }

    private fun renderSelectedDrawTool(
        selectedTool: Int
    ) {
        val selectedView = when (selectedTool) {
            DrawTool.PENCIL.value -> binding.btnPencil

            DrawTool.PEN.value -> binding.btnPen

            DrawTool.MARKER.value -> binding.btnMarker

            DrawTool.ERASER.value -> binding.btnEraser

            DrawTool.PEN_5.value -> binding.btnPen5

            else -> return
        }

        animateSelectedTool(
            selectedView
        )
    }

    private fun animateSelectedTool(
        selectedView: View
    ) {
        val tools = listOf(
            binding.btnPencil, binding.btnPen, binding.btnMarker, binding.btnEraser, binding.btnPen5
        )

        tools.forEach { view ->
            view.animate().cancel()

            val isSelected = view == selectedView

            view.animate().translationY(
                if (isSelected) {
                    -16f
                } else {
                    0f
                }
            ).scaleX(
                if (isSelected) {
                    1.08f
                } else {
                    1f
                }
            ).scaleY(
                if (isSelected) {
                    1.08f
                } else {
                    1f
                }
            ).setDuration(
                if (isSelected) {
                    250
                } else {
                    180
                }
            ).start()
        }
    }

    private fun handleDrawEffect(
        effect: DrawUiEffect
    ) {
        when (effect) {
            DrawUiEffect.ShowColorDialog -> {
                showDrawColorDialog()
            }

            DrawUiEffect.Undo -> {
                binding.drawingView.undo()
            }

            DrawUiEffect.Redo -> {
                binding.drawingView.redo()
            }
        }
    }

    private fun startCrop(
        width: Int, height: Int
    ) {
        val sourceUri = currentImage ?: return

        val destinationUri = Uri.fromFile(
            File(
                cacheDir, "crop_${System.currentTimeMillis()}.jpg"
            )
        )

        binding.flContent.isVisible = false

        binding.uCropView.isVisible = true

        showMode(
            EditorMode.CROP
        )

        val cropImageView = binding.uCropView.cropImageView

        setupCropOverlay(
            binding.uCropView.overlayView
        )

        cropImageView.setImageUri(
            sourceUri, destinationUri
        )

        cropImageView.targetAspectRatio = if (width > 0 && height > 0) {
            width.toFloat() / height.toFloat()
        } else {
            CropImageView.SOURCE_IMAGE_ASPECT_RATIO
        }
    }

    private fun setupCropOverlay(
        overlayView: OverlayView
    ) {
        overlayView.apply {
            setShowCropGrid(
                true
            )

            setCropGridRowCount(
                2
            )

            setCropGridColumnCount(
                2
            )

            setShowCropFrame(
                true
            )

            setFreestyleCropMode(
                OverlayView.FREESTYLE_CROP_MODE_ENABLE
            )
        }
    }

    private fun executeCrop() {
        binding.uCropView.cropImageView.cropAndSaveImage(
            Bitmap.CompressFormat.JPEG, 90, object : BitmapCropCallback {

                override fun onBitmapCropped(
                    resultUri: Uri, offsetX: Int, offsetY: Int, imageWidth: Int, imageHeight: Int
                ) {
                    currentImage = resultUri

                    binding.ivBackground.setImageURI(
                        resultUri
                    )

                    closeCropMode()
                }

                override fun onCropFailure(
                    throwable: Throwable
                ) {
                    showToast(
                        "Lỗi cắt ảnh: ${throwable.message}"
                    )
                }
            })
    }

    private fun closeCropMode() {
        binding.uCropView.isVisible = false

        binding.flContent.isVisible = true

        showMode(
            EditorMode.MAIN
        )
    }

    private fun applyCurrent() {


        val bitmap = bitmapManager.capture(
            binding.flContent
        )

        val uri = bitmapManager.saveToCache(
            bitmap
        )

        currentImage = uri

        binding.ivBackground.setImageURI(
            uri
        )
    }

    private fun setupSaveClick() {
        binding.tvSave.setOnClickListener {
            saveCurrentImage()
        }
    }

    private fun saveCurrentImage() {


        val bitmap = bitmapManager.capture(
            binding.flContent
        )

        val saved = bitmapManager.saveToGallery(
            bitmap
        )
        if (saved) {
            showToast(
                "Lưu ảnh thành công!"
            )

            finish()
        } else {
            showToast(
                "Lưu ảnh thất bại"
            )
        }
    }

    private fun showTextDialog() {
        DiaLogCustomer.show(
            this, title = "Add Text", showEdt = true
        ) { text ->
            stickerViewModel.onEvent(
                StickerUiEvent.ConfirmTextClicked(
                    text
                )
            )
        }
    }

    private fun showTextColorDialog() {
        BottomSheet.show(
            this
        ) { color ->
            stickerViewModel.onEvent(
                StickerUiEvent.TextColorSelected(
                    color
                )
            )
        }
    }

    private fun showStrokeColorDialog() {
        BottomSheet.show(
            this
        ) { color ->
            stickerViewModel.onEvent(
                StickerUiEvent.TextStrokeColorSelected(
                    color
                )
            )
        }
    }

    private fun showDrawColorDialog() {
        BottomSheet.show(
            this
        ) { color ->
            drawViewModel.onEvent(
                DrawUiEvent.ColorSelected(
                    color
                )
            )
        }
    }

    private fun showToast(
        message: String
    ) {
        Toast.makeText(
            this, message, Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadImage() {
        currentImage = intent.getStringExtra("image")?.let(Uri::parse)

        currentImage?.let { uri ->
            val bitmap = contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input)
            } ?: return@let

            binding.ivBackground.setImageBitmap(bitmap)

            binding.editorViewport.post {
                resizeCanvas(
                    bitmap.width, bitmap.height
                )
            }
        }
    }

    private fun resizeCanvas(
        imageWidth: Int, imageHeight: Int
    ) {
        val viewportWidth = binding.editorViewport.width
        val viewportHeight = binding.editorViewport.height

        if (viewportWidth <= 0 || viewportHeight <= 0 || imageWidth <= 0 || imageHeight <= 0) {
            return
        }

        val imageRatio = imageWidth.toFloat() / imageHeight.toFloat()

        val viewportRatio = viewportWidth.toFloat() / viewportHeight.toFloat()

        val canvasWidth: Int
        val canvasHeight: Int

        if (imageRatio > viewportRatio) {
            canvasWidth = viewportWidth
            canvasHeight = (viewportWidth / imageRatio).toInt()
        } else {
            canvasHeight = viewportHeight
            canvasWidth = (viewportHeight * imageRatio).toInt()
        }

        binding.flContent.layoutParams = binding.flContent.layoutParams.apply {
            width = canvasWidth
            height = canvasHeight
        }
    }

    private fun showMode(
        mode: EditorMode
    ) {
        currentMode = mode

        binding.llMainTool.isVisible = mode == EditorMode.MAIN

        binding.llStickerTool.isVisible = mode == EditorMode.STICKER

        binding.llTextTool.isVisible = mode == EditorMode.TEXT

        binding.llTextSizeTool.isVisible = mode == EditorMode.TEXT_SIZE

        binding.llTextStrokeTool.isVisible = mode == EditorMode.TEXT_STROKE

        binding.llTextFontTool.isVisible = mode == EditorMode.TEXT_FONT

        binding.llCropTool.isVisible = mode == EditorMode.CROP

        val showConfirm =
            mode == EditorMode.STICKER || mode == EditorMode.TEXT || mode == EditorMode.CROP || mode == EditorMode.DRAW

        binding.btnTick.isVisible = showConfirm

        binding.tvSave.isVisible = mode == EditorMode.MAIN
    }
}