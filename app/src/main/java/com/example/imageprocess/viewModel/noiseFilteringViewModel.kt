package com.example.imageprocess.viewModel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.math.pow
import kotlin.math.sqrt

class NoiseFilteringViewModel : ViewModel() {



    fun applyNoiseFilter(bitmap: Bitmap, filterType: String): Bitmap {
        return when (filterType) {
            "Min" -> applyMinFilterGrayscale(bitmap)
            "Max" -> applyMaxFilterGrayscale(bitmap)
            "Average" -> applyAverageFilterGrayscale(bitmap)
            "Median" -> applyMedianFilterGrayscale(bitmap)
            else -> bitmap
        }
    }


    private fun isGrayscaleImage(bitmap: Bitmap): Boolean {
        val width = bitmap.width
        val height = bitmap.height

        // Rastgele piksel örnekleri al
        val sampleSize = 10
        for (i in 0 until sampleSize) {
            val x = (Math.random() * width).toInt()
            val y = (Math.random() * height).toInt()
            val pixel = bitmap.getPixel(x, y)

            // RGB değerleri eşit değilse renkli görüntüdür
            if (Color.red(pixel) != Color.green(pixel) ||
                Color.green(pixel) != Color.blue(pixel)) {
                return false
            }
        }
        return true
    }

    fun applyLaplacianFilter(bitmap: Bitmap): Bitmap {
        return applyLaplacianFilterGrayscale(bitmap)
    }

    fun applySobelGradientFilter(bitmap: Bitmap): Bitmap {
        return applySobelGradientFilterGrayscale(bitmap)
    }

    fun applySmoothingFilter(bitmap: Bitmap): Bitmap {
        return applySmoothingFilterGrayscale(bitmap)
    }

    fun applyMasking(bitmap: Bitmap): Bitmap {
        return applyMaskingGrayscale(bitmap)
    }

    fun applySharpening(bitmap: Bitmap): Bitmap {
        return applySharpeningGrayscale(bitmap)
    }

    fun applyPowerLawTransformation(bitmap: Bitmap, gamma: Double = 1.5): Bitmap {
        return applyPowerLawTransformationGrayscale(bitmap, gamma)
    }

    fun applyPowerLawTransformationGrayscale(bitmap: Bitmap, gamma: Double = 1.5): Bitmap {
        // Yeni bitmap oluştur
        val transformedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        // Sabit çarpan
        val c = 1.0

        // Her piksel için işlemi uygula
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                // Pikselin orijinal gri tonlama değerini al
                val pixel = bitmap.getPixel(x, y)
                val gray = Color.red(pixel) // Grayscale olduğu için red, green veya blue değerinden birini alabiliriz

                // Güç yasası dönüşümü uygula: s = c * r^γ
                val newGray = (c * gray.toDouble().pow(gamma)).toInt().coerceIn(0, 255)

                // Yeni gri tonlama değerini piksele ayarla
                val newPixel = Color.rgb(newGray, newGray, newGray)
                transformedBitmap.setPixel(x, y, newPixel)
            }
        }

        return transformedBitmap
    }


    private fun applyLaplacianFilterGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        // Laplacian kernel
        val kernel = arrayOf(
            intArrayOf(0, 1, 0),
            intArrayOf(1, -4, 1),
            intArrayOf(0, 1, 0)
        )

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sum = 0

                // Kernel convolution
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        val value = Color.red(pixel)
                        sum += value * kernel[i + 1][j + 1]
                    }
                }

                // Normalize and clamp values
                val finalValue = sum.coerceIn(0, 255)
                result.setPixel(x, y, Color.rgb(finalValue, finalValue, finalValue))
            }
        }
        return result
    }



    // Sobel Gradient Filter implementations
    private fun applySobelGradientFilterGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        // Sobel kernels
        val kernelX = arrayOf(
            intArrayOf(-1, 0, 1),
            intArrayOf(-2, 0, 2),
            intArrayOf(-1, 0, 1)
        )
        val kernelY = arrayOf(
            intArrayOf(-1, -2, -1),
            intArrayOf(0, 0, 0),
            intArrayOf(1, 2, 1)
        )

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sumX = 0
                var sumY = 0

                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        val value = Color.red(pixel)
                        sumX += value * kernelX[i + 1][j + 1]
                        sumY += value * kernelY[i + 1][j + 1]
                    }
                }

                val magnitude = sqrt((sumX * sumX + sumY * sumY).toDouble()).toInt().coerceIn(0, 255)
                result.setPixel(x, y, Color.rgb(magnitude, magnitude, magnitude))
            }
        }
        return result
    }



    // Smoothing Filter implementations
    private fun applySmoothingFilterGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        // Gaussian kernel 3x3
        val kernel = arrayOf(
            intArrayOf(1, 2, 1),
            intArrayOf(2, 4, 2),
            intArrayOf(1, 2, 1)
        )
        val kernelSum = 16 // Sum of all kernel values

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sum = 0

                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        sum += Color.red(pixel) * kernel[i + 1][j + 1]
                    }
                }

                val finalValue = (sum / kernelSum).coerceIn(0, 255)
                result.setPixel(x, y, Color.rgb(finalValue, finalValue, finalValue))
            }
        }
        return result
    }



    // Masking implementations
    private fun applyMaskingGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        // High-pass filter mask
        val kernel = arrayOf(
            intArrayOf(-1, -1, -1),
            intArrayOf(-1, 9, -1),
            intArrayOf(-1, -1, -1)
        )

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sum = 0

                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        sum += Color.red(pixel) * kernel[i + 1][j + 1]
                    }
                }

                val finalValue = sum.coerceIn(0, 255)
                result.setPixel(x, y, Color.rgb(finalValue, finalValue, finalValue))
            }
        }
        return result
    }



    // Sharpening implementations
    private fun applySharpeningGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        val kernel = arrayOf(
            intArrayOf(0, -1, 0),
            intArrayOf(-1, 5, -1),
            intArrayOf(0, -1, 0)
        )

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sum = 0

                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        sum += Color.red(pixel) * kernel[i + 1][j + 1]
                    }
                }

                val finalValue = sum.coerceIn(0, 255)
                result.setPixel(x, y, Color.rgb(finalValue, finalValue, finalValue))
            }
        }
        return result
    }


    // Grayscale görüntüler için filtreler
    private fun applyMinFilterGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var minValue = 255

                // 3x3 kernel taraması
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        val value = Color.red(pixel) // Grayscale'de herhangi bir kanal yeterli
                        minValue = minOf(minValue, value)
                    }
                }

                // Grayscale değeri RGB'nin her kanalına aynı değeri atayarak oluştur
                result.setPixel(x, y, Color.rgb(minValue, minValue, minValue))
            }
        }
        return result
    }

    private fun applyMaxFilterGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var maxValue = 0

                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        val value = Color.red(pixel)
                        maxValue = maxOf(maxValue, value)
                    }
                }

                result.setPixel(x, y, Color.rgb(maxValue, maxValue, maxValue))
            }
        }
        return result
    }

    private fun applyAverageFilterGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sum = 0

                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        sum += Color.red(pixel)
                    }
                }

                val average = sum / 9
                result.setPixel(x, y, Color.rgb(average, average, average))
            }
        }
        return result
    }

    private fun applyMedianFilterGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                val values = mutableListOf<Int>()

                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        values.add(Color.red(pixel))
                    }
                }

                values.sort()
                val median = values[4] // 9 elemandan ortadaki
                result.setPixel(x, y, Color.rgb(median, median, median))
            }
        }
        return result
    }


    fun applyMinFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var minR = 255
                var minG = 255
                var minB = 255

                // 3x3 kernel taraması
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        minR = minOf(minR, Color.red(pixel))
                        minG = minOf(minG, Color.green(pixel))
                        minB = minOf(minB, Color.blue(pixel))
                    }
                }

                result.setPixel(x, y, Color.rgb(minR, minG, minB))
            }
        }
        return result
    }

    fun applyMaxFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var maxR = 0
                var maxG = 0
                var maxB = 0

                // 3x3 kernel taraması
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        maxR = maxOf(maxR, Color.red(pixel))
                        maxG = maxOf(maxG, Color.green(pixel))
                        maxB = maxOf(maxB, Color.blue(pixel))
                    }
                }

                result.setPixel(x, y, Color.rgb(maxR, maxG, maxB))
            }
        }
        return result
    }

    fun applyAverageFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sumR = 0
                var sumG = 0
                var sumB = 0

                // 3x3 kernel taraması
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        sumR += Color.red(pixel)
                        sumG += Color.green(pixel)
                        sumB += Color.blue(pixel)
                    }
                }

                // 9 piksel için ortalama
                val avgR = sumR / 9
                val avgG = sumG / 9
                val avgB = sumB / 9

                result.setPixel(x, y, Color.rgb(avgR, avgG, avgB))
            }
        }
        return result
    }

    fun applyMedianFilter(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val result = Bitmap.createBitmap(width, height, source.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                val rValues = mutableListOf<Int>()
                val gValues = mutableListOf<Int>()
                val bValues = mutableListOf<Int>()

                // 3x3 kernel taraması
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        rValues.add(Color.red(pixel))
                        gValues.add(Color.green(pixel))
                        bValues.add(Color.blue(pixel))
                    }
                }

                // Medyan değerleri
                rValues.sort()
                gValues.sort()
                bValues.sort()

                val medianR = rValues[4] // 9 elemandan ortadaki (4. indeks)
                val medianG = gValues[4]
                val medianB = bValues[4]

                result.setPixel(x, y, Color.rgb(medianR, medianG, medianB))
            }
        }
        return result
    }



}