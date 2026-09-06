package com.example.draw.data.reponsitory

import com.example.draw.R
import com.example.draw.main.model.CreativeTool
import com.example.draw.main.model.CreativeToolType
import com.example.draw.main.model.InspirationItem

object MainRepository {
    val inspirationItems = listOf(
        InspirationItem(
            image = R.drawable.img_landscape1,
            title = "Khám phá phong cách mới",
            description = "Thử một cách chỉnh sửa khác cho bức ảnh của bạn"
        ), InspirationItem(
            image = R.drawable.img_landscape2,
            title = "Lưu giữ khoảnh khắc",
            description = "Biến những khoảnh khắc quen thuộc thành kỷ niệm đặc biệt"
        ), InspirationItem(
            image = R.drawable.img_landscape3,
            title = "Sáng tạo theo cách riêng",
            description = "Khám phá màu sắc và phong cách phù hợp với bạn"
        )
    )

    val creativeTools = listOf(
        CreativeTool(
            icon = R.drawable.img_28,
            title = "Vẽ trên ảnh",
            description = "Tùy chỉnh cọ vẽ",
            type = CreativeToolType.DRAW
        ), CreativeTool(
            icon = R.drawable.img_27,
            title = "Cắt & Xoay",
            description = "Điều chỉnh kích thước",
            type = CreativeToolType.CROP
        ), CreativeTool(
            icon = R.drawable.img_34,
            title = "Bộ lọc",
            description = "Hàng trăm màu sắc",
            type = CreativeToolType.FILTER
        ), CreativeTool(
            icon = R.drawable.img_35,
            title = "Điều chỉnh",
            description = "Sáng, tối, tương phản",
            type = CreativeToolType.ADJUST
        )
    )
}