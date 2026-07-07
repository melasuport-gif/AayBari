PDF Generation — AayBari

This branch adds a starter PDF generation feature for bills and A4 ledger reports.

Files added in feature/pdf-generation:
- app/src/main/java/com/package/aaybari/pdf/PdfGenerator.kt
- app/src/main/java/com/package/aaybari/pdf/BarcodeHelper.kt
- app/src/main/java/com/package/aaybari/pdf/BengaliNumberToWords.kt
- app/build.gradle (updated to include ZXing and Gson)

How to test locally
1. Checkout branch:
   git fetch && git checkout feature/pdf-generation
2. Make sure build.gradle changes are applied and sync Gradle in Android Studio
3. Build and run the app on a device/emulator
4. Call PdfGenerator.generateBill(...) from any Activity/Coroutine scope with sample data.
   Example snippet you can run from a button click:

   CoroutineScope(Dispatchers.Main).launch {
       val items = listOf(
           PdfGenerator.BillItem("2026-07-01", "Rent for July", 5000.0),
           PdfGenerator.BillItem("2026-07-02", "Water Charge", 300.0)
       )
       val file = PdfGenerator.generateBill(this@MainActivity, "AAY-202607-0001", "2026-07-07", items, "bill_sample")
       if (file != null) Toast.makeText(this@MainActivity, "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
   }

Notes
- The generator uses Android PdfDocument and ZXing for barcode rendering.
- Amount-in-words uses a simple Bengali converter; please review grammar and adjust for your locale.
- If you want a custom logo or font, add assets in res/raw or assets/ and I will integrate them into the layout.
