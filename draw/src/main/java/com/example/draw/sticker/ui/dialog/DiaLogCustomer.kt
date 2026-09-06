package com.example.draw.sticker.ui

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.example.draw.databinding.DialogAddTextBinding

object DiaLogCustomer {

    fun show(
        activity: Activity,
        title: String = "Add Text",
        showEdt: Boolean = true,
        onConfirm: (String) -> Unit
    ) {
        val dialog = Dialog(activity)
        val binding = DialogAddTextBinding.inflate(activity.layoutInflater)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        binding.tvTitle.text = title

        if (showEdt) {
            binding.edtText.visibility = View.VISIBLE
        } else {
            binding.edtText.visibility = View.GONE
        }

        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            val text = binding.edtText.text.toString().trim()
            if (!showEdt || text.isNotEmpty()) {
                onConfirm(text)
                dialog.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.window?.setLayout(
            (activity.resources.displayMetrics.widthPixels * 0.9f).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}