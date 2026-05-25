package com.fitwell.app.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.fitwell.app.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var tvStatus: TextView
    private lateinit var btnCapture: Button
    private lateinit var btnCancel: Button

    private lateinit var cameraExecutor: ExecutorService
    private var captureNow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        viewFinder = findViewById(R.id.viewFinder)
        tvStatus   = findViewById(R.id.tvStatus)
        btnCapture = findViewById(R.id.btnCapture)
        btnCancel  = findViewById(R.id.btnCancel)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 200)
        }

        btnCapture.setOnClickListener {
            captureNow = true
            tvStatus.text = "Scanning... hold steady"
            btnCapture.isEnabled = false
        }
        btnCancel.setOnClickListener { finish() }
    }

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(viewFinder.surfaceProvider) }
            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also { ia -> ia.setAnalyzer(cameraExecutor) { proxy -> processFrame(proxy) } }
            try {
                provider.unbindAll()
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processFrame(proxy: ImageProxy) {
        if (!captureNow) { proxy.close(); return }
        val media = proxy.image
        if (media == null) { proxy.close(); return }
        val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { result ->
                val parsed = parseNutritionLabel(result.text)
                runOnUiThread {
                    if (parsed != null) {
                        setResult(RESULT_OK, Intent().apply {
                            putExtra("food_name", parsed["name"] ?: "Scanned Product")
                            putExtra("calories", parsed["calories"]?.toFloatOrNull() ?: 0f)
                            putExtra("protein",  parsed["protein"]?.toFloatOrNull() ?: 0f)
                            putExtra("carbs",    parsed["carbs"]?.toFloatOrNull() ?: 0f)
                            putExtra("fat",      parsed["fat"]?.toFloatOrNull() ?: 0f)
                            putExtra("fiber",    parsed["fiber"]?.toFloatOrNull() ?: 0f)
                            putExtra("sodium",   parsed["sodium"]?.toFloatOrNull() ?: 0f)
                        })
                        finish()
                    } else {
                        tvStatus.text = "Could not detect nutrition info.\nEnsure the label is fully visible and well-lit."
                        captureNow = false
                        btnCapture.isEnabled = true
                    }
                }
            }
            .addOnFailureListener {
                runOnUiThread {
                    tvStatus.text = "Scan failed. Try again."
                    captureNow = false
                    btnCapture.isEnabled = true
                }
            }
            .addOnCompleteListener { proxy.close() }
    }

    /**
     * Robust nutrition label parser:
     * - Handles "Calories 250", "Energy: 1045 kJ / 250 kcal", "Protein 12g", etc.
     * - Strips common suffixes (g, mg, kcal, kJ, %)
     * - Looks at the next line if a value isn't found on the same line
     * - Works with both Indian and international label formats
     */
    private fun parseNutritionLabel(rawText: String): Map<String, String>? {
        // Normalise: lowercase, collapse whitespace
        val lines = rawText.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val result = mutableMapOf<String, String>()
        val numRx = Regex("""(\d+(?:[.,]\d+)?)""")

        fun extractNumber(text: String): String? =
            numRx.find(text)?.value?.replace(",", ".")

        // For each line try to identify the nutrient, then extract a number
        // from that line OR the immediately following line
        for (i in lines.indices) {
            val raw = lines[i]
            val l = raw.lowercase()
            val next = lines.getOrNull(i + 1) ?: ""
            val combined = "$raw $next"

            fun grab() = extractNumber(raw) ?: extractNumber(next)

            when {
                // Calories / Energy
                (l.contains("calorie") || l.contains("energy") || l == "cal") &&
                        !l.contains("from") && "calories" !in result -> {
                    // Prefer kcal value — e.g. "105 kJ / 25 kcal" → take last number before kcal
                    val kcalMatch = Regex("""(\d+(?:[.,]\d+)?)\s*kcal""", RegexOption.IGNORE_CASE).find(combined)
                    val v = kcalMatch?.groupValues?.get(1) ?: grab()
                    v?.let { result["calories"] = it }
                }

                // Protein
                l.contains("protein") && "protein" !in result -> {
                    grab()?.let { result["protein"] = it }
                }

                // Carbohydrates
                (l.contains("total carb") || l.contains("carbohydrate") ||
                        l.startsWith("carb") || l == "carbs") &&
                        !l.contains("fiber") && !l.contains("fibre") && "carbs" !in result -> {
                    grab()?.let { result["carbs"] = it }
                }

                // Fat
                (l.contains("total fat") || (l.startsWith("fat") && !l.contains("saturated") && !l.contains("trans"))) &&
                        "fat" !in result -> {
                    grab()?.let { result["fat"] = it }
                }

                // Fiber
                (l.contains("dietary fiber") || l.contains("dietary fibre") ||
                        l.contains("fibre") || l == "fiber" || l == "fibre") &&
                        "fiber" !in result -> {
                    grab()?.let { result["fiber"] = it }
                }

                // Sodium
                l.contains("sodium") && "sodium" !in result -> {
                    grab()?.let { result["sodium"] = it }
                }
            }
        }

        // Need at least calories OR protein to consider a valid label
        if ("calories" !in result && "protein" !in result) return null

        result["name"] = "Scanned Product"
        return result
    }

    override fun onRequestPermissionsResult(rc: Int, perms: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(rc, perms, results)
        if (rc == 200 && results.firstOrNull() == PackageManager.PERMISSION_GRANTED) startCamera()
        else { Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show(); finish() }
    }

    override fun onDestroy() { super.onDestroy(); cameraExecutor.shutdown() }
}
