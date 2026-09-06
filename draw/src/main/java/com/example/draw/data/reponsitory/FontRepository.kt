package com.example.draw.data.reponsitory

import com.example.draw.R
import com.example.draw.sticker.model.FontItem

class FontRepository {
    val fontList = listOf(
        FontItem(
            "Bold",
            R.font.font_bold
        ),
        FontItem(
            "Script",
            R.font.font_light
        ),
        FontItem(
            "Serif",
            R.font.font_regular
        )
    )
}