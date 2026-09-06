package com.example.draw.sticker.ui

import android.app.Activity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.draw.adapter.ColorAdapter
import com.example.draw.data.ColorList
import com.example.draw.databinding.BottomSheetColorBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

object BottomSheet{

    fun show(
        activity: Activity,
        onColorSelected: (Int) -> Unit
    ) {
        val dialog = BottomSheetDialog(activity)
        val binding = BottomSheetColorBinding.inflate(activity.layoutInflater)
        dialog.setContentView(binding.root)
        binding.colorRecyclerView.layoutManager = GridLayoutManager(activity, 6)
        binding.colorRecyclerView.adapter = ColorAdapter(ColorList.getColors(activity)) { color ->
            val colorPreview = binding.colorPreview.background
            if (colorPreview is android.graphics.drawable.GradientDrawable) {
                colorPreview.setColor(color)
            } else {
                binding.colorPreview.setBackgroundColor(color)
            }

            onColorSelected(color)
            dialog.dismiss()
        }

        binding.btnCloseColor.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}