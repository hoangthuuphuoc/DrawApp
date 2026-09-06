package com.example.draw.data.reponsitory

import com.example.draw.data.model.CropItem

class CropRepository {
    val listCrop = listOf(
        CropItem("Free", 0, 0),
        CropItem("16:9", 16, 9),
        CropItem("9:16", 9, 16),
        CropItem("4:3", 4, 3),
        CropItem("3:2", 3, 2),
    )
}