package com.package.aaybari.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.package.aaybari.R
import com.package.aaybari.pdf.PdfGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Simple UI to generate, preview and share sample PDF bills.
 * - Uses PdfGenerator.generateBill(...) from the feature/pdf-generation code.
 * - FileProvider is used to share the generated PDF with other apps.
 */
class PdfUiActivity : AppCompatActivity() {

    private lateinit var btnGenerate: Button
    private lateinit var btnPreview: Button
    private lateinit var btnShare: Button
    private lateinit var progress: ProgressBar
    private lateinit var tvStatus: TextView

    private var lastFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_ui)

        btnGenerate = findViewById(R.id.btnGenerate)
        btnPreview = findViewById(R.id.btnPreview)
        btnShare = findViewById(R.id.btnShare)
        progress = findViewById(R.id.progress)
        tvStatus = findViewById(R.id.tvStatus)

        btnPreview.isEnabled = false
        btnShare.isEnabled = false

        btnGenerate.setOnClickListener {
            generateSamplePdf()
        }

        btnPreview.setOnClickListener {
            lastFile?.let { openPdf(it) } ?: Toast.makeText(this, "No PDF to preview", Toast.LENGTH_SHORT).show()
        }

        btnShare.setOnClickListener {
            lastFile?.let { sharePdf(it) } ?: Toast.makeText(this, "No PDF to share", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateSamplePdf() {
        progress.visibility = View.VISIBLE
        tvStatus.text = "Generating..."
        btnGenerate.isEnabled = false

        CoroutineScope(Dispatchers.Main).launch {
            val items = listOf(
                PdfGenerator.BillItem("2026-07-01", "ভাড়া — জুলাই", 5000.0),
                PdfGenerator.BillItem("2026-07-02", "পানি বিল", 300.0),
                PdfGenerator.BillItem("2026-07-03", "বিদ্যুৎ বিল", 1200.5)
            )

            val billNumber = generateBillNumber()
            val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            val file = withContext(Dispatchers.IO) {
                PdfGenerator.generateBill(applicationContext, billNumber, dateStr, items, "aaybari_bill")
            }

            progress.visibility = View.GONE
            btnGenerate.isEnabled = true

            if (file != null && file.exists()) {
                lastFile = file
                btnPreview.isEnabled = true
                btnShare.isEnabled = true
                tvStatus.text = "Saved: ${file.absolutePath}"
                Toast.makeText(this@PdfUiActivity, "PDF saved: ${file.name}", Toast.LENGTH_LONG).show()
            } else {
                tvStatus.text = "Failed to generate PDF"
                Toast.makeText(this@PdfUiActivity, "PDF generation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateBillNumber(): String {
        // Default format: AAY-YYYYMM-0001 — simple incremental suffix is not persisted here, this is a sample
        val sdf = java.text.SimpleDateFormat("yyyyMM", java.util.Locale.getDefault())
        val prefix = "AAY-${sdf.format(java.util.Date())}"
        val suffix = "0001"
        return "$prefix-$suffix"
    }

    private fun openPdf(file: File) {
        val uri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No app found to open PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sharePdf(file: File) {
        val uri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(intent, "Share PDF"))
    }
}
