package com.example.imageprocess.viewModel

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class ImageProcessViewModel : ViewModel() {


    // Seçili height ve width değerlerini tutan StateFlow
    private val _height = MutableStateFlow(0)
    val height: StateFlow<Int> = _height

    private val _width = MutableStateFlow(0)
    val width: StateFlow<Int> = _width







    // Height ve width güncelleme
    fun updateDimensions(newHeight: Int, newWidth: Int) {
        _height.value = newHeight
        _width.value = newWidth
    }








    suspend fun convertColorSpace(inputBitmap: Bitmap, fromColorSpace: String, toColorSpace: String): Bitmap {
        return withContext(Dispatchers.Default) {
            val width = inputBitmap.width
            val height = inputBitmap.height
            val transformedBitmap = Bitmap.createBitmap(width, height, inputBitmap.config)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = inputBitmap.getPixel(x, y)

                    val r = Color.red(pixel).toFloat() / 255
                    val g = Color.green(pixel).toFloat() / 255
                    val b = Color.blue(pixel).toFloat() / 255

                    val transformedPixel = when (toColorSpace) {
                        "HSI" -> convertToHSI(r, g, b)  // HSI'ye dönüştür
                        "HSV" -> convertToHSV(r, g, b)  // HSV'ye dönüştür
                        "LAB" -> convertToLAB(r, g, b)  // LAB'ye dönüştür
                        "YIQ" -> convertToYIQ(r, g, b)  // YIQ'ye dönüştür
                        else -> pixel  // Eğer desteklenmeyen bir dönüşümse orijinal resmi döndür
                    }

                    transformedBitmap.setPixel(x, y, transformedPixel)
                }
            }

            transformedBitmap
        }
    }


    fun convertToHSI(r: Float, g: Float, b: Float): Int {
        val min = minOf(r, g, b)
        val intensity = (r + g + b) / 3f
        val saturation = if (intensity > 0) 1 - min / intensity else 0f

        val hue = when {
            r == g && g == b -> 0f
            else -> {
                var hue = atan2(sqrt(3.0) * (g - b), (2 * r - g - b).toDouble()).toFloat()
                if (hue < 0) hue += 2 * PI.toFloat()
                hue * 180 / PI.toFloat()
            }
        }

        return Color.HSVToColor(floatArrayOf(hue, saturation, intensity))
    }

    fun convertToHSV(r: Float, g: Float, b: Float): Int {
        val hsv = FloatArray(3)
        Color.RGBToHSV((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), hsv)
        return Color.HSVToColor(hsv)
    }

    fun convertToLAB(r: Float, g: Float, b: Float): Int {
        // RGB to XYZ
        fun gammaCorrect(c: Float): Float = if (c > 0.04045f) ((c + 0.055f) / 1.055f).pow(2.4f) else c / 12.92f
        val rLinear = gammaCorrect(r)
        val gLinear = gammaCorrect(g)
        val bLinear = gammaCorrect(b)

        val x = 0.4124564f * rLinear + 0.3575761f * gLinear + 0.1804375f * bLinear
        val y = 0.2126729f * rLinear + 0.7151522f * gLinear + 0.0721750f * bLinear
        val z = 0.0193339f * rLinear + 0.1191920f * gLinear + 0.9503041f * bLinear

        // XYZ to LAB
        fun f(t: Float): Float = if (t > 0.008856f) t.pow(1f / 3f) else 7.787f * t + 16f / 116f

        val xr = x / 0.95047f
        val yr = y
        val zr = z / 1.08883f

        val L = 116 * f(yr) - 16
        val a = 500 * (f(xr) - f(yr))
        val b = 200 * (f(yr) - f(zr))

        // Normalize LAB values to RGB range for visualization
        return Color.rgb(
            (L * 255 / 100).toInt().coerceIn(0, 255),
            ((a + 128) * 255 / 255).toInt().coerceIn(0, 255),
            ((b + 128) * 255 / 255).toInt().coerceIn(0, 255)
        )
    }

    fun convertToYIQ(r: Float, g: Float, b: Float): Int {
        val y = 0.299f * r + 0.587f * g + 0.114f * b
        val i = 0.596f * r - 0.274f * g - 0.322f * b
        val q = 0.211f * r - 0.523f * g + 0.312f * b

        // Normalize YIQ values to RGB range for visualization
        return Color.rgb(
            (y * 255).toInt().coerceIn(0, 255),
            ((i + 0.5957f) * 255 / 1.1914f).toInt().coerceIn(0, 255),
            ((q + 0.5226f) * 255 / 1.0452f).toInt().coerceIn(0, 255)
        )
    }

    suspend fun applyReflection(inputBitmap: Bitmap, reflectionType: String): Bitmap {
        return withContext(Dispatchers.Default) {
            val matrix = Matrix()
            when (reflectionType) {
                "Reflection (Vertical)" -> matrix.preScale(1f, -1f)
                "Reflection (Horizontal)" -> matrix.preScale(-1f, 1f)
                "Reflection (Both)" -> matrix.preScale(-1f, -1f)
                else -> return@withContext inputBitmap // No reflection if type is not recognized
            }

            Bitmap.createBitmap(
                inputBitmap,
                0,
                0,
                inputBitmap.width,
                inputBitmap.height,
                matrix,
                true
            )
        }
    }

    suspend fun applyResize(inputBitmap: Bitmap, startX: Int, endX: Int, startY: Int, endY: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            val width = endX - startX
            val height = endY - startY
            Bitmap.createScaledBitmap(inputBitmap, width, height, true)
        }
    }

    suspend fun applyCrop(inputBitmap: Bitmap, startX: Int, endX: Int, startY: Int, endY: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            Bitmap.createBitmap(inputBitmap, startX, startY, endX - startX, endY - startY)
        }
    }

    suspend fun applyShifting(inputBitmap: Bitmap, startX: Int, endX: Int, startY: Int, endY: Int): Bitmap {
        return withContext(Dispatchers.Default) {
            val matrix = Matrix().apply {
                setTranslate((endX - startX).toFloat(), (endY - startY).toFloat())
            }
            Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height, matrix, true)
        }
    }



}
