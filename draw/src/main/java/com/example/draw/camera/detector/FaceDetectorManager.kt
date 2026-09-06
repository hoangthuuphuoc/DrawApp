package com.example.draw.camera.detector

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorManager(
    context: Context,
    private val onFacesDetected: (List<Face>) -> Unit,
    private val onError: (Throwable) -> Unit = {}
) {

    private val detector: FaceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()

            .setPerformanceMode(
                FaceDetectorOptions.PERFORMANCE_MODE_FAST
            )

            .setLandmarkMode(
                FaceDetectorOptions.LANDMARK_MODE_ALL
            )

            .setContourMode(
                FaceDetectorOptions.CONTOUR_MODE_NONE
            )

            .setClassificationMode(
                FaceDetectorOptions.CLASSIFICATION_MODE_NONE
            )

            .enableTracking()

            .build()
    )

    private val executor = ContextCompat.getMainExecutor(
        context
    )

    val analyzer: ImageAnalysis.Analyzer = MlKitAnalyzer(
        listOf(
            detector
        ),

        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,

        executor
    ) { result ->

        val error = result.getThrowable(
            detector
        )

        if (error != null) {
            onError(
                error
            )

            return@MlKitAnalyzer
        }

        val faces = result.getValue(
            detector
        ) ?: emptyList()

        onFacesDetected(
            faces
        )
    }

    fun close() {
        detector.close()
    }
}