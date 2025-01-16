package com.example.imageprocess.morphologicalTransform

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel

class MorphologicalTransformViewModel : ViewModel() {

    fun applyDilation(inputBitmap: Bitmap): Bitmap {
        val width = inputBitmap.width
        val height = inputBitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, inputBitmap.config)

        // Kernel size (3x3)
        val kernelSize = 1

        // Process each pixel
        for (x in kernelSize until width - kernelSize) {
            for (y in kernelSize until height - kernelSize) {
                var maxRed = 0
                var maxGreen = 0
                var maxBlue = 0

                // Process kernel neighborhood
                for (i in -kernelSize..kernelSize) {
                    for (j in -kernelSize..kernelSize) {
                        val pixel = inputBitmap.getPixel(x + i, y + j)

                        // Get RGB values
                        val red = Color.red(pixel)
                        val green = Color.green(pixel)
                        val blue = Color.blue(pixel)

                        // Update maximum values
                        maxRed = maxOf(maxRed, red)
                        maxGreen = maxOf(maxGreen, green)
                        maxBlue = maxOf(maxBlue, blue)
                    }
                }

                // Set the new pixel value
                val newPixel = Color.rgb(maxRed, maxGreen, maxBlue)
                outputBitmap.setPixel(x, y, newPixel)
            }
        }

        // Handle border pixels
        for (x in 0 until width) {
            for (y in 0..kernelSize) {
                outputBitmap.setPixel(x, y, inputBitmap.getPixel(x, y))
                outputBitmap.setPixel(x, height - 1 - y, inputBitmap.getPixel(x, height - 1 - y))
            }
        }

        for (y in 0 until height) {
            for (x in 0..kernelSize) {
                outputBitmap.setPixel(x, y, inputBitmap.getPixel(x, y))
                outputBitmap.setPixel(width - 1 - x, y, inputBitmap.getPixel(width - 1 - x, y))
            }
        }

        return outputBitmap
    }

    fun applyErosion(inputBitmap: Bitmap): Bitmap {
        val width = inputBitmap.width
        val height = inputBitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, inputBitmap.config)

        // Kernel size (3x3)
        val kernelSize = 1

        // Process each pixel
        for (x in kernelSize until width - kernelSize) {
            for (y in kernelSize until height - kernelSize) {
                var minRed = 255
                var minGreen = 255
                var minBlue = 255

                // Process kernel neighborhood
                for (i in -kernelSize..kernelSize) {
                    for (j in -kernelSize..kernelSize) {
                        val pixel = inputBitmap.getPixel(x + i, y + j)

                        // Get RGB values
                        val red = Color.red(pixel)
                        val green = Color.green(pixel)
                        val blue = Color.blue(pixel)

                        // Update minimum values
                        minRed = minOf(minRed, red)
                        minGreen = minOf(minGreen, green)
                        minBlue = minOf(minBlue, blue)
                    }
                }

                // Set the new pixel value
                val newPixel = Color.rgb(minRed, minGreen, minBlue)
                outputBitmap.setPixel(x, y, newPixel)
            }
        }

        // Handle border pixels
        for (x in 0 until width) {
            for (y in 0..kernelSize) {
                outputBitmap.setPixel(x, y, inputBitmap.getPixel(x, y))
                outputBitmap.setPixel(x, height - 1 - y, inputBitmap.getPixel(x, height - 1 - y))
            }
        }

        for (y in 0 until height) {
            for (x in 0..kernelSize) {
                outputBitmap.setPixel(x, y, inputBitmap.getPixel(x, y))
                outputBitmap.setPixel(width - 1 - x, y, inputBitmap.getPixel(width - 1 - x, y))
            }
        }

        return outputBitmap
    }

    fun applyDilationThenErosion(inputBitmap: Bitmap): Bitmap {
        val dilatedBitmap = applyDilation(inputBitmap)
        return applyErosion(dilatedBitmap)
    }

    fun applyErosionThenDilation(inputBitmap: Bitmap): Bitmap {
        val erodedBitmap = applyErosion(inputBitmap)
        return applyDilation(erodedBitmap)
    }
}