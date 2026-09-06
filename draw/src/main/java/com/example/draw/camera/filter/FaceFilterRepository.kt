package com.example.draw.camera.filter
import com.example.draw.R
import com.example.draw.camera.data.FaceFilterItem

import com.example.draw.camera.model.FilterMode

object FaceFilterRepository {

    fun getFilters(
        mode: FilterMode
    ): List<FaceFilterItem> {

        val none =
            FaceFilterItem(
                previewRes =
                    R.drawable.ic_filter_none,

                filterRes =
                    null,

                mode =
                    mode
            )

        return when (mode) {

            FilterMode.HEAD ->
                listOf(
                    none,

                    FaceFilterItem(
                        R.drawable.img_36,
                        R.drawable.img_36,
                        mode
                    )
                )

            FilterMode.FACE ->
                listOf(
                    none,

                    FaceFilterItem(
                        R.drawable.img_37,
                        R.drawable.img_37,
                        mode
                    )
                )

            FilterMode.EYES ->
                listOf(
                    none,

                    FaceFilterItem(
                        R.drawable.img_37,
                        R.drawable.img_37,
                        mode
                    )
                )

            FilterMode.NOSE ->
                listOf(
                    none,

                    FaceFilterItem(
                        R.drawable.img_38,
                        R.drawable.img_38,
                        mode
                    )
                )

            FilterMode.MOUTH ->
                listOf(
                    none,

                    FaceFilterItem(
                        R.drawable.img_39,
                        R.drawable.img_39,
                        mode
                    )
                )
        }
    }
}