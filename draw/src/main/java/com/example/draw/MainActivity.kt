package com.example.draw

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.draw.adapter.ColorAdapter
import com.example.draw.data.ColorList
import com.example.draw.databinding.ActivityMainBinding
import com.example.draw.databinding.BottomSheetColorBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var selectedColor = Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
        binding.btnPencil.setOnClickListener {
            binding.drawingView.setTool(1)
            selectTool(binding.btnPencil)
        }

        binding.btnPen.setOnClickListener {
            binding.drawingView.setTool(2)
            selectTool(binding.btnPen)
        }

        binding.btnMarker.setOnClickListener {
            binding.drawingView.setTool(3)
            selectTool(binding.btnMarker)
        }

        binding.btnEraser.setOnClickListener {
            binding.drawingView.setTool(4)
            selectTool(binding.btnEraser)
        }

        binding.btnPen5.setOnClickListener {
            binding.drawingView.setTool(5)
            selectTool(binding.btnPen5)
        }

        binding.btnMore.setOnClickListener {
            binding.drawingView.undo()
        }

        binding.btnDone.setOnClickListener {
            binding.drawingView.redo()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnColor.setOnClickListener {
            showColorDialog()
        }

        binding.drawingView.setTool(2)
        binding.drawingView.setColor(selectedColor)
        selectTool(binding.btnPen)
    }

    private fun showColorDialog() {
        val dialog = BottomSheetDialog(this)
        val colorBinding = BottomSheetColorBinding.inflate(
            layoutInflater
        )

        dialog.setContentView(colorBinding.root)

        colorBinding.colorRecyclerView.layoutManager =
            GridLayoutManager(this, 6)

        colorBinding.colorRecyclerView.adapter =
            ColorAdapter(ColorList.getColors(this)) { color ->
                selectedColor = color
                binding.drawingView.setColor(selectedColor)
                dialog.dismiss()
            }

        colorBinding.btnCloseColor.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun selectTool(selectedView: View) {
        val listTool = listOf(
            binding.btnPencil,
            binding.btnPen,
            binding.btnMarker,
            binding.btnEraser,
            binding.btnPen5
        )

        listTool.forEach { view ->
            view.animate().cancel()

            if (view == selectedView) {
                view.animate()
                    .translationY(-16f)
                    .scaleX(1.08f)
                    .scaleY(1.08f)
                    .setDuration(250)
                    .start()
            } else {
                view.animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .start()
            }
        }
    }
}