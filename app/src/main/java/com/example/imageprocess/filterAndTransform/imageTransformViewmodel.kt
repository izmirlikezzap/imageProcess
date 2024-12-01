package com.example.imageprocess.filterAndTransform

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.math.pow
import kotlin.math.sqrt

class ImageTransformViewModel : ViewModel() {

    fun applyHighPassFilter(bitmap: Bitmap): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        for (x in 1 until bitmap.width - 1) {
            for (y in 1 until bitmap.height - 1) {
                val pixel = bitmap.getPixel(x, y)
                val avg = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                val newColor = if (avg > 128) Color.WHITE else Color.BLACK
                result.setPixel(x, y, newColor)
            }
        }
        return result
    }

    fun applyThresholding(bitmap: Bitmap, threshold: Int = 128): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val avg = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                val newColor = if (avg >= threshold) Color.WHITE else Color.BLACK
                result.setPixel(x, y, newColor)
            }
        }
        return result
    }

    fun applyButterworthFilter(bitmap: Bitmap, cutoffFrequency: Int = 50, order: Int = 2): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val centerX = width / 2
        val centerY = height / 2

        for (x in 0 until width) {
            for (y in 0 until height) {
                val dx = x - centerX
                val dy = y - centerY
                val distance = sqrt((dx * dx + dy * dy).toDouble())
                val factor = 1.0 / (1.0 + (distance / cutoffFrequency).pow(2.0 * order))
                val pixel = bitmap.getPixel(x, y)
                val red = (Color.red(pixel) * factor).toInt()
                val green = (Color.green(pixel) * factor).toInt()
                val blue = (Color.blue(pixel) * factor).toInt()
                result.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }
        return result
    }

    fun applyInverseFourierTransform(bitmap: Bitmap): Bitmap {
        // Simulated Inverse Fourier Transform
        return bitmap // Placeholder for actual inverse Fourier transform
    }
}
