Integrate Noto Sans Bengali (font placeholder) and add unit tests.

Notes:
- Please add the actual font file to app/src/main/res/font/noto_sans_bengali.ttf (download from Google Fonts) before relying on the Bengali font rendering in PDFs.
- Unit tests for BengaliNumberToWords are added under app/src/test/. These are JVM unit tests and do not require an Android device.
