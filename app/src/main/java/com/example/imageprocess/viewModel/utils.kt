import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.math.min

suspend fun processImage(image: Bitmap, kernelSize: Pair<Int, Int>): Bitmap = withContext(Dispatchers.Default) {
    val (kernelWidth, kernelHeight) = kernelSize
    val width = image.width
    val height = image.height

    val isGrayscale = isImageGrayscale(image)
    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val numThreads = Runtime.getRuntime().availableProcessors()
    val rowsPerThread = height / numThreads

    val jobs = List(numThreads) { threadIndex ->
        async {
            val startY = threadIndex * rowsPerThread
            val endY = if (threadIndex == numThreads - 1) height else (threadIndex + 1) * rowsPerThread

            for (y in startY until endY step kernelHeight) {
                for (x in 0 until width step kernelWidth) {
                    val kernelPixels = IntArray(kernelWidth * kernelHeight)
                    val actualKernelWidth = min(kernelWidth, width - x)
                    val actualKernelHeight = min(kernelHeight, height - y)

                    image.getPixels(kernelPixels, 0, kernelWidth, x, y, actualKernelWidth, actualKernelHeight)

                    val averageColor = if (isGrayscale) {
                        getAverageGrayscale(kernelPixels, actualKernelWidth * actualKernelHeight)
                    } else {
                        getAverageColor(kernelPixels, actualKernelWidth * actualKernelHeight)
                    }

                    for (ky in 0 until actualKernelHeight) {
                        for (kx in 0 until actualKernelWidth) {
                            resultBitmap.setPixel(x + kx, y + ky, averageColor)
                        }
                    }
                }
            }
        }
    }

    jobs.forEach { it.await() }
    resultBitmap
}

private fun isImageGrayscale(image: Bitmap): Boolean {
    val pixels = IntArray(100)
    image.getPixels(pixels, 0, 10, 0, 0, 10, 10)
    return pixels.all { pixel ->
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)
        red == green && green == blue
    }
}

private fun getAverageColor(pixels: IntArray, pixelCount: Int): Int {
    var sumRed = 0
    var sumGreen = 0
    var sumBlue = 0

    for (pixel in pixels) {
        sumRed += Color.red(pixel)
        sumGreen += Color.green(pixel)
        sumBlue += Color.blue(pixel)
    }

    return Color.rgb(
        sumRed / pixelCount,
        sumGreen / pixelCount,
        sumBlue / pixelCount
    )
}

private fun getAverageGrayscale(pixels: IntArray, pixelCount: Int): Int {
    var sum = 0
    for (pixel in pixels) {
        sum += Color.red(pixel) // Gri tonlamalı resimde R, G, B değerleri aynıdır
    }
    val average = sum / pixelCount
    return Color.rgb(average, average, average)
}