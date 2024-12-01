package com.example.imageprocess.noiseAdditionAndRemoving

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import android.graphics.Color
import android.widget.Toast
import kotlin.math.min
import kotlin.math.max
import kotlin.random.Random

class NoiseAddtionAndRemovingViewModel : ViewModel() {

    // Add Noise Methods
    fun addSaltNoise(bitmap: Bitmap, noiseLevel: Double): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val noisyBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val adjustedNoiseLevel = noiseLevel / 100 // Yüzdelik değeri 0.0 ile 1.0 arasına ölçekle

        for (x in 0 until width) {
            for (y in 0 until height) {
                val originalPixel = bitmap.getPixel(x, y)
                val noise = java.util.Random().nextDouble()

                val pixel = if (noise < adjustedNoiseLevel) {
                    // Salt noise (white)
                    Color.WHITE
                } else {
                    originalPixel
                }

                noisyBitmap.setPixel(x, y, pixel)
            }
        }

        return noisyBitmap
    }


    fun addPepperNoise(bitmap: Bitmap, noiseLevel: Double): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val noisyBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val adjustedNoiseLevel = noiseLevel / 100 // Yüzdelik değeri 0.0 ile 1.0 arasına ölçekle

        for (x in 0 until width) {
            for (y in 0 until height) {
                val originalPixel = bitmap.getPixel(x, y)
                val noise = java.util.Random().nextDouble()

                val pixel = if (noise < adjustedNoiseLevel) {
                    // Pepper noise (black)
                    Color.BLACK
                } else {
                    originalPixel
                }

                noisyBitmap.setPixel(x, y, pixel)
            }
        }

        return noisyBitmap
    }

    fun addGaussianNoise(bitmap: Bitmap, noiseLevel: Double): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val noisyBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val adjustedNoiseLevel = noiseLevel / 100 // Yüzdelik değeri 0.0 ile 1.0 arasına ölçekle
        val random = java.util.Random() // Gaussian noise için java.util.Random kullanılıyor

        for (x in 0 until width) {
            for (y in 0 until height) {
                val originalPixel = bitmap.getPixel(x, y)

                // Gaussian noise üretimi
                val noise = random.nextGaussian() * adjustedNoiseLevel * 255

                val r = min(max(Color.red(originalPixel) + noise.toInt(), 0), 255)
                val g = min(max(Color.green(originalPixel) + noise.toInt(), 0), 255)
                val b = min(max(Color.blue(originalPixel) + noise.toInt(), 0), 255)

                val noisyPixel = Color.rgb(r, g, b)
                noisyBitmap.setPixel(x, y, noisyPixel)
            }
        }

        return noisyBitmap
    }


    fun addUniformNoise(bitmap: Bitmap, noiseLevel: Double): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val noisyBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        val adjustedNoiseLevel = noiseLevel / 100 // Yüzdelik değeri 0.0 ile 1.0 arasına ölçekle

        for (x in 0 until width) {
            for (y in 0 until height) {
                val originalPixel = bitmap.getPixel(x, y)

                // Rastgele bir noise değeri oluştur
                val noise = (Math.random() - 0.5) * 2 * adjustedNoiseLevel * 255

                val r = min(max(Color.red(originalPixel) + noise.toInt(), 0), 255)
                val g = min(max(Color.green(originalPixel) + noise.toInt(), 0), 255)
                val b = min(max(Color.blue(originalPixel) + noise.toInt(), 0), 255)

                val noisyPixel = Color.rgb(r, g, b)
                noisyBitmap.setPixel(x, y, noisyPixel)
            }
        }

        return noisyBitmap
    }


    // Remove Noise Methods
    fun applyMedianFilter(bitmap: Bitmap, kernelSize: Int = 3): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val filteredBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        val halfKernel = kernelSize / 2

        for (x in halfKernel until width - halfKernel) {
            for (y in halfKernel until height - halfKernel) {
                val redValues = mutableListOf<Int>()
                val greenValues = mutableListOf<Int>()
                val blueValues = mutableListOf<Int>()

                for (dx in -halfKernel..halfKernel) {
                    for (dy in -halfKernel..halfKernel) {
                        val pixel = bitmap.getPixel(x + dx, y + dy)
                        redValues.add(Color.red(pixel))
                        greenValues.add(Color.green(pixel))
                        blueValues.add(Color.blue(pixel))
                    }
                }

                redValues.sort()
                greenValues.sort()
                blueValues.sort()

                val medianRed = redValues[redValues.size / 2]
                val medianGreen = greenValues[greenValues.size / 2]
                val medianBlue = blueValues[blueValues.size / 2]

                val medianPixel = Color.rgb(medianRed, medianGreen, medianBlue)
                filteredBitmap.setPixel(x, y, medianPixel)
            }
        }

        return filteredBitmap
    }

    fun applyGaussianFilter(bitmap: Bitmap, kernelSize: Int = 3): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val filteredBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        val kernel = generateGaussianKernel(kernelSize)
        val halfKernel = kernelSize / 2

        for (x in halfKernel until width - halfKernel) {
            for (y in halfKernel until height - halfKernel) {
                var sumRed = 0.0
                var sumGreen = 0.0
                var sumBlue = 0.0
                var kernelSum = 0.0

                for (dx in -halfKernel..halfKernel) {
                    for (dy in -halfKernel..halfKernel) {
                        val pixel = bitmap.getPixel(x + dx, y + dy)
                        val weight = kernel[dx + halfKernel][dy + halfKernel]

                        sumRed += Color.red(pixel) * weight
                        sumGreen += Color.green(pixel) * weight
                        sumBlue += Color.blue(pixel) * weight
                        kernelSum += weight
                    }
                }

                val filteredRed = min(max((sumRed / kernelSum).toInt(), 0), 255)
                val filteredGreen = min(max((sumGreen / kernelSum).toInt(), 0), 255)
                val filteredBlue = min(max((sumBlue / kernelSum).toInt(), 0), 255)

                val filteredPixel = Color.rgb(filteredRed, filteredGreen, filteredBlue)
                filteredBitmap.setPixel(x, y, filteredPixel)
            }
        }

        return filteredBitmap
    }

    private fun generateGaussianKernel(size: Int): Array<DoubleArray> {
        val kernel = Array(size) { DoubleArray(size) }
        val sigma = size / 6.0
        val center = size / 2

        var sum = 0.0
        for (x in 0 until size) {
            for (y in 0 until size) {
                val xDistance = x - center
                val yDistance = y - center
                kernel[x][y] = Math.exp(-(xDistance * xDistance + yDistance * yDistance) / (2 * sigma * sigma))
                sum += kernel[x][y]
            }
        }

        // Normalize the kernel
        for (x in 0 until size) {
            for (y in 0 until size) {
                kernel[x][y] /= sum
            }
        }

        return kernel
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

                // 3x3 kernel scan
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)

                        // Handle grayscale
                        if (r == g && g == b) {
                            minR = minOf(minR, r)
                            minG = minOf(minG, g)
                            minB = minOf(minB, b)
                        } else {
                            minR = minOf(minR, r)
                            minG = minOf(minG, g)
                            minB = minOf(minB, b)
                        }
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

                // 3x3 kernel scan
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)

                        // Handle grayscale
                        if (r == g && g == b) {
                            maxR = maxOf(maxR, r)
                            maxG = maxOf(maxG, g)
                            maxB = maxOf(maxB, b)
                        } else {
                            maxR = maxOf(maxR, r)
                            maxG = maxOf(maxG, g)
                            maxB = maxOf(maxB, b)
                        }
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

                // 3x3 kernel scan
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = source.getPixel(x + i, y + j)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)

                        // Add all channels
                        sumR += r
                        sumG += g
                        sumB += b
                    }
                }

                // Average for 9 pixels
                val avgR = sumR / 9
                val avgG = sumG / 9
                val avgB = sumB / 9

                result.setPixel(x, y, Color.rgb(avgR, avgG, avgB))
            }
        }
        return result
    }

}
