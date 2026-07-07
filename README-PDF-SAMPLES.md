Sample PDFs generation and test instructions

This branch adds a small helper to programmatically generate sample PDF bill and ledger files using the existing PdfGenerator.

Files added:
- app/src/main/java/com/package/aaybari/samples/SamplesGenerator.kt

How to generate samples locally (developer steps):
1) Install the app on a device/emulator with API 21+.
2) From an Activity, call:
   SamplesGenerator.generateSamples(this) { paths ->
       // paths contains absolute paths to generated PDF files in app's external files dir
   }
   Or use SamplesGenerator.generateSamplesBlocking(context) in quick debug code.
3) Retrieve the files under: adb shell "run-as <package-name> ls /sdcard/Android/data/<package-name>/files/Download/"
   Or use Android Studio Device File Explorer to download the generated PDFs.
4) Take screenshots of the PDF preview or open on device, then add them to the repo or attach to PR comments.

I cannot generate sample PDFs from this environment — please run the helper on-device and upload the generated files/screenshots. Once you upload them, I can update the PR to include the artifacts and finalize the merge.
