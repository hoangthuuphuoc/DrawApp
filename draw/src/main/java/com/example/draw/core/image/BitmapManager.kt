package com.example.draw.core.image

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import java.io.File
import java.io.FileOutputStream

class BitmapManager(
    private val context: Context
) {

    fun capture(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.WHITE)
        view.draw(canvas)

        return bitmap
    }

    fun saveToCache(bitmap: Bitmap): Uri {
        val file = File(
            context.cacheDir,
            "edited_${System.currentTimeMillis()}.png"
        )

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )
        }

        return Uri.fromFile(file)
    }

    fun saveToGallery(bitmap: Bitmap): Boolean {
        return try {
            val filename =
                "IMG_${System.currentTimeMillis()}.jpg"

            val outputStream =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    val values = ContentValues().apply {
                        put(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            filename
                        )

                        put(
                            MediaStore.MediaColumns.MIME_TYPE,
                            "image/jpeg"
                        )

                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_PICTURES}/DrawStickerApp"
                        )
                    }

                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )

                    uri?.let {
                        context.contentResolver.openOutputStream(it)
                    }

                } else {

                    val directory =
                        Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        )

                    val file = File(
                        directory,
                        filename
                    )

                    FileOutputStream(file)
                }

            outputStream?.use { stream ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    stream
                )
            }

            outputStream != null

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}