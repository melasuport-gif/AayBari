package com.package.aaybari.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.package.aaybari.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Simple PDF generator for Bill and Ledger. This is a starter implementation — adapt fonts,
 * localization and styling to match your app's requirements.
 */
object PdfGenerator {

    private const val POINTS_PER_INCH = 72f

    data class BillItem(val date: String, val description: String, val amount: Double)

    // Generate a single bill PDF (landscape-like small size). Returns saved File or null on error.
    suspend fun generateBill(context: Context, billNumber: String, dateStr: String, items: List<BillItem>, outputFileName: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                // Use bill size: 8.27" x 5.3" (width x height)
                val width = (8.27f * POINTS_PER_INCH).toInt()
                val height = (5.3f * POINTS_PER_INCH).toInt()

                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val margin = (0.5f * POINTS_PER_INCH).toInt()
                var y = margin

                val paint = Paint()
                paint.isAntiAlias = true

                // Header: app name and bill title
                paint.textSize = 18f
                paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                canvas.drawText("AayBari", margin.toFloat(), y.toFloat(), paint)

                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("Bill No: $billNumber", (width - margin - 200).toFloat(), y.toFloat(), paint)
                y += 30

                // Date
                paint.textSize = 11f
                canvas.drawText("Date: $dateStr", margin.toFloat(), y.toFloat(), paint)
                y += 25

                // Draw table header
                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                val col1X = margin
                val col2X = margin + 120
                val col3X = width - margin - 100
                canvas.drawText("Date", col1X.toFloat(), y.toFloat(), paint)
                canvas.drawText("Description", col2X.toFloat(), y.toFloat(), paint)
                canvas.drawText("Amount", col3X.toFloat(), y.toFloat(), paint)
                y += 18

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textSize = 11f

                var total = 0.0
                for (item in items) {
                    canvas.drawText(item.date, col1X.toFloat(), y.toFloat(), paint)
                    canvas.drawText(item.description, col2X.toFloat(), y.toFloat(), paint)
                    canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.amount), col3X.toFloat(), y.toFloat(), paint)
                    y += 16
                    total += item.amount
                }

                y += 8
                paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                canvas.drawText("Total:", col2X.toFloat(), y.toFloat(), paint)
                canvas.drawText(String.format(Locale.getDefault(), "%.2f", total), col3X.toFloat(), y.toFloat(), paint)
                y += 30

                // Amount in words (Bangla)
                val words = BengaliNumberToWords.toWords(total)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textSize = 11f
                canvas.drawText("Amount (in words): $words", margin.toFloat(), y.toFloat(), paint)
                y += 40

                // Barcode at bottom
                val barcodeBitmap = BarcodeHelper.generateCode128Bitmap(billNumber, 600, 80)
                if (barcodeBitmap != null) {
                    val bx = (width - barcodeBitmap.width) / 2
                    canvas.drawBitmap(barcodeBitmap, bx.toFloat(), y.toFloat(), null)
                }

                pdfDocument.finishPage(page)

                // Save file
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
                if (!dir.exists()) dir.mkdirs()
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val file = File(dir, "${outputFileName}_$timestamp.pdf")
                FileOutputStream(file).use { fos ->
                    pdfDocument.writeTo(fos)
                }
                pdfDocument.close()
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Generate a paginated ledger (A4). Simple starter implementation.
    suspend fun generateLedger(context: Context, title: String, rows: List<BillItem>, outputFileName: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val width = (8.27f * POINTS_PER_INCH).toInt()
                val height = (11.69f * POINTS_PER_INCH).toInt()

                val pdfDocument = PdfDocument()
                val paint = Paint()
                paint.isAntiAlias = true

                val margin = (0.5f * POINTS_PER_INCH).toInt()
                val col1X = margin
                val col2X = margin + 120
                val col3X = width - margin - 120

                val rowsPerPage = 40
                val pages = (rows.size + rowsPerPage - 1) / rowsPerPage
                for (p in 0 until pages) {
                    val pageInfo = PdfDocument.PageInfo.Builder(width, height, p + 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    val canvas = page.canvas
                    var y = margin

                    // Header
                    paint.textSize = 18f
                    paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                    canvas.drawText("$title", margin.toFloat(), y.toFloat(), paint)
                    y += 30

                    paint.textSize = 12f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    canvas.drawText("Page ${p + 1} of $pages", (width - margin - 150).toFloat(), margin.toFloat(), paint)
                    y += 10

                    // Table header
                    paint.textSize = 12f
                    paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                    canvas.drawText("Date", col1X.toFloat(), y.toFloat(), paint)
                    canvas.drawText("Description", col2X.toFloat(), y.toFloat(), paint)
                    canvas.drawText("Amount", col3X.toFloat(), y.toFloat(), paint)
                    y += 18

                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    paint.textSize = 11f

                    val start = p * rowsPerPage
                    val end = minOf(start + rowsPerPage, rows.size)
                    var total = 0.0
                    for (i in start until end) {
                        val row = rows[i]
                        canvas.drawText(row.date, col1X.toFloat(), y.toFloat(), paint)
                        canvas.drawText(row.description, col2X.toFloat(), y.toFloat(), paint)
                        canvas.drawText(String.format(Locale.getDefault(), "%.2f", row.amount), col3X.toFloat(), y.toFloat(), paint)
                        y += 16
                        total += row.amount
                    }

                    y += 10
                    paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                    canvas.drawText("Page Total:", col2X.toFloat(), y.toFloat(), paint)
                    canvas.drawText(String.format(Locale.getDefault(), "%.2f", total), col3X.toFloat(), y.toFloat(), paint)

                    pdfDocument.finishPage(page)
                }

                val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
                if (!dir.exists()) dir.mkdirs()
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val file = File(dir, "${outputFileName}_$timestamp.pdf")
                FileOutputStream(file).use { fos -> pdfDocument.writeTo(fos) }
                pdfDocument.close()
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
