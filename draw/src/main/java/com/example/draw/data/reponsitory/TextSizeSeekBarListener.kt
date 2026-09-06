package com.example.draw.sticker.listener

import android.widget.SeekBar

class TextSizeSeekBarListener(
    private val onTextSizeChanged: (Float) -> Unit
) : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        if (!fromUser) return

        onTextSizeChanged(
            progress.toFloat()
        )
    }

    override fun onStartTrackingTouch(
        seekBar: SeekBar?
    ) {
    }

    override fun onStopTrackingTouch(
        seekBar: SeekBar?
    ) {
    }
}