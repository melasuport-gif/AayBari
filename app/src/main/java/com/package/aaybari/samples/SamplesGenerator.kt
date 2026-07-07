package com.package.aaybari.samples

import android.content.Context
import com.package.aaybari.pdf.PdfGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Helper to generate sample PDFs (bill + ledger) programmatically.
 * Usage: call SamplesGenerator.generateSamples(context) from an Activity or test.
 */
object SamplesGenerator {
    fun generateSamples(context: Context, onComplete: (List<String>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val billItems = listOf(
                PdfGenerator.BillItem("2026-07-01", "ভাড়া — জুলাই", 5000.0),
                PdfGenerator.BillItem("2026-07-02", "পানি বিল", 300.0),
                PdfGenerator.BillItem("2026-07-03", "বিদ্যুৎ বিল", 1200.5)
            )

            val ledgerRows = (1..120).map { i -> PdfGenerator.BillItem("2026-07-${(i % 30) + 1}", "Item #$i", (i * 10).toDouble()) }

            val billFile = PdfGenerator.generateBill(context, "AAY-202607-0001", "2026-07-07", billItems, "sample_bill")
            val ledgerFile = PdfGenerator.generateLedger(context, "Sample Ledger", ledgerRows, "sample_ledger")

            val paths = mutableListOf<String>()
            billFile?.let { paths.add(it.absolutePath) }
            ledgerFile?.let { paths.add(it.absolutePath) }

            onComplete(paths)
        }
    }

    // Blocking convenience method for quick tests (runBlocking) — only for debug use
    fun generateSamplesBlocking(context: Context): List<String> = runBlocking {
        val result = mutableListOf<String>()
        val billItems = listOf(
            PdfGenerator.BillItem("2026-07-01", "ভাড়া — জুলাই", 5000.0),
            PdfGenerator.BillItem("2026-07-02", "পানি বিল", 300.0),
            PdfGenerator.BillItem("2026-07-03", "বিদ্যুৎ বিল", 1200.5)
        )
        val ledgerRows = (1..120).map { i -> PdfGenerator.BillItem("2026-07-${(i % 30) + 1}", "Item #$i", (i * 10).toDouble()) }
        val billFile = PdfGenerator.generateBill(context, "AAY-202607-0001", "2026-07-07", billItems, "sample_bill")
        val ledgerFile = PdfGenerator.generateLedger(context, "Sample Ledger", ledgerRows, "sample_ledger")
        billFile?.let { result.add(it.absolutePath) }
        ledgerFile?.let { result.add(it.absolutePath) }
        result
    }
}
