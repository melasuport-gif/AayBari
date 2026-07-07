PDF UI — generate, preview, and share

This branch adds a minimal UI screen (PdfUiActivity) that demonstrates generating a sample bill PDF using the existing PdfGenerator code, previewing it with an intent, and sharing via FileProvider.

Files added in feature/pdf-ui:
- app/src/main/java/com/package/aaybari/ui/PdfUiActivity.kt
- app/src/main/res/layout/activity_pdf_ui.xml
- app/src/main/res/xml/file_paths.xml (FileProvider paths)
- app/src/main/res/drawable/aaybari_logo.svg (placeholder — replace with high-res PNG if available)
- README-PDF-UI.md (this file)

Manual steps to wire into the app
1) AndroidManifest: register the activity and FileProvider (add inside <application>):

<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>

<activity android:name="com.package.aaybari.ui.PdfUiActivity" />

2) (Optional) Add the activity entry point (e.g., launch from MainActivity via Intent)

3) If you want better Bengali font rendering, add a TTF/OTF to res/font (e.g., noto_sans_bengali.ttf) and update PdfGenerator/Paint to use it.

How to test
- Run the app, open PdfUiActivity, tap "Generate Sample Bill". The PDF will be saved into app's external files downloads folder and a Toast will show its path. Use Preview or Share to open/share it.

Notes
- The logo included here is a placeholder SVG. Replace app/src/main/res/drawable/aaybari_logo.svg with the high-res logo PNG you uploaded if you prefer (keep the same resource name to avoid code changes).
