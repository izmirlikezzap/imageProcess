import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.math.max
import kotlin.math.min

class ImageRestorationViewModel : ViewModel() {

    fun applyInverseFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = 255 - Color.red(pixel)
                val green = 255 - Color.green(pixel)
                val blue = 255 - Color.blue(pixel)

                resultBitmap.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }

        return resultBitmap
    }

    fun applyWeinerFilter(bitmap: Bitmap, noiseVariance: Double = 0.01): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        val kernelSize = 3 // Window size
        val halfKernel = kernelSize / 2

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Calculate local mean and variance
                val (localMean, localVariance) = calculateLocalStatistics(bitmap, x, y, kernelSize)

                val pixel = bitmap.getPixel(x, y)
                val intensity = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3

                // Apply Wiener filter formula
                val restoredIntensity = if (localVariance > noiseVariance) {
                    localMean + ((localVariance - noiseVariance) / max(localVariance, noiseVariance)) * (intensity - localMean)
                } else {
                    localMean
                }

                val clampedIntensity = min(255, max(0, restoredIntensity.toInt()))
                resultBitmap.setPixel(x, y, Color.rgb(clampedIntensity, clampedIntensity, clampedIntensity))
            }
        }

        return resultBitmap
    }

    private fun calculateLocalStatistics(
        bitmap: Bitmap,
        centerX: Int,
        centerY: Int,
        kernelSize: Int
    ): Pair<Double, Double> {
        val halfKernel = kernelSize / 2
        var sum = 0.0
        var sumSquared = 0.0
        var count = 0

        for (x in max(0, centerX - halfKernel)..min(bitmap.width - 1, centerX + halfKernel)) {
            for (y in max(0, centerY - halfKernel)..min(bitmap.height - 1, centerY + halfKernel)) {
                val pixel = bitmap.getPixel(x, y)
                val intensity = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                sum += intensity
                sumSquared += intensity * intensity
                count++
            }
        }

        val mean = sum / count
        val variance = (sumSquared / count) - (mean * mean)

        return Pair(mean, max(variance, 0.0)) // Ensure variance is non-negative
    }





    fun applyLeastSquaresFilter(bitmap: Bitmap): Bitmap {
        // Placeholder for the Least Squares filter algorithm
        // Add image restoration logic here
        return bitmap
    }

    fun applyGeometricMeanFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var product = 1.0
                var count = 0
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = bitmap.getPixel(x + i, y + j)
                        val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                        product *= gray.toDouble()
                        count++
                    }
                }
                val geometricMean = Math.pow(product, 1.0 / count)
                val newPixel = Color.rgb(geometricMean.toInt(), geometricMean.toInt(), geometricMean.toInt())
                resultBitmap.setPixel(x, y, newPixel)
            }
        }

        return resultBitmap
    }

    fun applyArithmeticMeanFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var sum = 0
                var count = 0
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixel = bitmap.getPixel(x + i, y + j)
                        val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                        sum += gray
                        count++
                    }
                }
                val mean = sum / count
                val newPixel = Color.rgb(mean, mean, mean)
                resultBitmap.setPixel(x, y, newPixel)
            }
        }

        return resultBitmap
    }
}
