package com.example.imageprocess.histogram

import androidx.lifecycle.ViewModel


import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class HistogramOperationsViewModel : ViewModel() {

    fun applyHistogramStretching(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val stretchedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        var isGrayscale = true

        // Check if the image is grayscale by examining the first few pixels
        outer@ for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                if (red != green || green != blue) {
                    isGrayscale = false
                    break@outer
                }
            }
        }

        if (isGrayscale) {
            // Apply grayscale stretching
            var minGray = 255
            var maxGray = 0

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = bitmap.getPixel(x, y)
                    val gray = Color.red(pixel)  // or green or blue, they are equal in grayscale

                    if (gray < minGray) minGray = gray
                    if (gray > maxGray) maxGray = gray
                }
            }

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = bitmap.getPixel(x, y)
                    val gray = Color.red(pixel)

                    val stretchedGray = ((gray - minGray) * 255 / (maxGray - minGray)).coerceIn(0, 255)
                    stretchedBitmap.setPixel(x, y, Color.rgb(stretchedGray, stretchedGray, stretchedGray))
                }
            }
        } else {
            // Apply RGB stretching for each channel
            var minRed = 255
            var maxRed = 0
            var minGreen = 255
            var maxGreen = 0
            var minBlue = 255
            var maxBlue = 0

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = bitmap.getPixel(x, y)
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    if (red < minRed) minRed = red
                    if (red > maxRed) maxRed = red
                    if (green < minGreen) minGreen = green
                    if (green > maxGreen) maxGreen = green
                    if (blue < minBlue) minBlue = blue
                    if (blue > maxBlue) maxBlue = blue
                }
            }

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = bitmap.getPixel(x, y)
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    val stretchedRed = ((red - minRed) * 255 / (maxRed - minRed)).coerceIn(0, 255)
                    val stretchedGreen = ((green - minGreen) * 255 / (maxGreen - minGreen)).coerceIn(0, 255)
                    val stretchedBlue = ((blue - minBlue) * 255 / (maxBlue - minBlue)).coerceIn(0, 255)

                    stretchedBitmap.setPixel(x, y, Color.rgb(stretchedRed, stretchedGreen, stretchedBlue))
                }
            }
        }

        return stretchedBitmap
    }


    fun applyHistogramEqualization(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val equalizedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val histogram = IntArray(256)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                histogram[gray]++
            }
        }

        val cumulativeDistribution = IntArray(256)
        cumulativeDistribution[0] = histogram[0]
        for (i in 1 until 256) {
            cumulativeDistribution[i] = cumulativeDistribution[i - 1] + histogram[i]
        }

        val scaleFactor = 255.0 / (width * height)
        val lut = IntArray(256) { (cumulativeDistribution[it] * scaleFactor).toInt() }

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                val equalizedGray = lut[gray]
                equalizedBitmap.setPixel(x, y, Color.rgb(equalizedGray, equalizedGray, equalizedGray))
            }
        }

        return equalizedBitmap
    }

    fun applyTransferFunction(bitmap: Bitmap, whiteLevel: Int, blackLevel: Int): Bitmap {
        // Create lookup table based on the transfer function
        val lookupTable = IntArray(256) { i ->
            when {
                i >= whiteLevel -> 255
                i <= blackLevel -> 0
                else -> i  // Keep the original intensity for values in between
            }
        }

        // Create a mutable copy of the bitmap
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Get the pixels as an array for faster processing
        val pixels = IntArray(resultBitmap.width * resultBitmap.height)
        resultBitmap.getPixels(pixels, 0, resultBitmap.width, 0, 0, resultBitmap.width, resultBitmap.height)

        // Process each pixel using the lookup table
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val alpha = Color.alpha(pixel)
            val red = lookupTable[Color.red(pixel)]
            val green = lookupTable[Color.green(pixel)]
            val blue = lookupTable[Color.blue(pixel)]

            pixels[i] = Color.argb(alpha, red, green, blue)
        }

        // Set processed pixels back to the bitmap
        resultBitmap.setPixels(pixels, 0, resultBitmap.width, 0, 0, resultBitmap.width, resultBitmap.height)

        return resultBitmap
    }


}
