package com.example.draw.data.reponsitory

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.example.draw.data.model.ImageItem

class ImageRepository {

    fun loadAllImageUri(
        contentResolver: ContentResolver
    ): MutableList<ImageItem> {

        val listImage = mutableListOf<ImageItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID, MediaStore.Images.Media.IS_FAVORITE
        )

        val sortOrder = "${MediaStore.Images.Media._ID} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder
        )?.use { cursor ->

            val idCol = cursor.getColumnIndexOrThrow(
                MediaStore.Images.Media._ID
            )

            val favouriteCol = cursor.getColumnIndexOrThrow(
                MediaStore.Images.Media.IS_FAVORITE
            )

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idCol)

                val favourite = cursor.getInt(favouriteCol)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )

                listImage.add(
                    ImageItem(
                        uri, favourite == 1
                    )
                )
            }
        }

        return listImage
    }
}