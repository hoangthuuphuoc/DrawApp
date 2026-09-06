package com.example.draw.data

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.draw.R

object ColorList {

    fun getColors(context: Context): List<Int> {
        return listOf(
            ContextCompat.getColor(context, R.color.den),
            ContextCompat.getColor(context, R.color.xam_than),
            ContextCompat.getColor(context, R.color.xam_dam),
            ContextCompat.getColor(context, R.color.xam),
            ContextCompat.getColor(context, R.color.xam_nhat),
            ContextCompat.getColor(context, R.color.trang),

            ContextCompat.getColor(context, R.color.do_tuoi),
            ContextCompat.getColor(context, R.color.do_dam),
            ContextCompat.getColor(context, R.color.do_cam),

            ContextCompat.getColor(context, R.color.hong_dam),
            ContextCompat.getColor(context, R.color.hong),
            ContextCompat.getColor(context, R.color.hong_nhat),
            ContextCompat.getColor(context, R.color.hong_pastel),

            ContextCompat.getColor(context, R.color.tim_dam),
            ContextCompat.getColor(context, R.color.tim),
            ContextCompat.getColor(context, R.color.tim_nhat),
            ContextCompat.getColor(context, R.color.tim_pastel),

            ContextCompat.getColor(context, R.color.indigo),
            ContextCompat.getColor(context, R.color.xanh_navy),
            ContextCompat.getColor(context, R.color.xanh_duong_dam),
            ContextCompat.getColor(context, R.color.xanh_duong),
            ContextCompat.getColor(context, R.color.xanh_da_troi),
            ContextCompat.getColor(context, R.color.xanh_duong_nhat),
            ContextCompat.getColor(context, R.color.xanh_cyan),

            ContextCompat.getColor(context, R.color.xanh_ngoc),
            ContextCompat.getColor(context, R.color.xanh_teal_dam),
            ContextCompat.getColor(context, R.color.xanh_la_dam),
            ContextCompat.getColor(context, R.color.xanh_la),
            ContextCompat.getColor(context, R.color.xanh_la_nhat),
            ContextCompat.getColor(context, R.color.xanh_mint),
            ContextCompat.getColor(context, R.color.xanh_reu),
            ContextCompat.getColor(context, R.color.xanh_olive),

            ContextCompat.getColor(context, R.color.vang_chanh),
            ContextCompat.getColor(context, R.color.vang),
            ContextCompat.getColor(context, R.color.vang_dam),
            ContextCompat.getColor(context, R.color.vang_nhat),
            ContextCompat.getColor(context, R.color.vang_gold),

            ContextCompat.getColor(context, R.color.cam_dam),
            ContextCompat.getColor(context, R.color.cam),
            ContextCompat.getColor(context, R.color.cam_nhat),

            ContextCompat.getColor(context, R.color.nau_dam),
            ContextCompat.getColor(context, R.color.nau),
            ContextCompat.getColor(context, R.color.nau_nhat),
            ContextCompat.getColor(context, R.color.be),
            ContextCompat.getColor(context, R.color.kem),

            ContextCompat.getColor(context, R.color.dao),
            ContextCompat.getColor(context, R.color.san_ho),
            ContextCompat.getColor(context, R.color.xanh_xam),
            ContextCompat.getColor(context, R.color.bac),
            ContextCompat.getColor(context, R.color.vang_kim)
        )
    }
}