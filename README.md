# DateFinder - Sedris Vision Assignment

## 📱 Objective

**DateFinder** is an Android app that extracts and audibly announces the most relevant date from user-supplied images. It uses fully offline processing, no internet connection required.

---

## 🧩 Core Features

* **Image Selection**: Users can select images from the device gallery.
* **Offline OCR**: Uses on-device ML Kit to extract text from the image.
* **Date Extraction**: Scans extracted text to find the most relevant date.
* **Audio Feedback**: Uses TextToSpeech to announce the found date.
* **No Date Found**: Announces "No date detected" if nothing valid is found.
* **Offline Only**: Zero network calls. Works 100% offline.

---

## 🧠 Architecture

**Pattern**: MVVM (Model-View-ViewModel)

* `MainActivity`: UI & image selection
* `DateFinderViewModel`: Handles state and logic
* `OCRProcessor`: Performs offline OCR with ML Kit
* `DateExtractor`: Uses regex to parse dates from OCR output
* `TTSHelper`: Announces result using TextToSpeech with auto-retry

**Libraries Used**:

* ML Kit Text Recognition (Offline)
* AndroidX Lifecycle
* Kotlin Coroutines

---

## 🔍 Date Extraction Logic

* All potential date patterns (dd/MM/yyyy, dd MMM yyyy, etc.) are detected using regex.
* Dates are parsed with `SimpleDateFormat`
* Heuristic: **latest valid date** is selected as the most relevant one

---

## 🚀 How to Build and Run

### Prerequisites

* Android Studio Giraffe+ (recommended)
* Android SDK 24+

### Steps

1. Clone the repo or unzip the `Source/` directory
2. Open in Android Studio
3. Let Gradle sync
4. `Run > Run app`
5. Select an image from gallery

### Build APK

1. Go to `Build > Build Bundle(s) / APK(s)`
2. Select `Build APK(s)`
3. Find it in `app/build/outputs/apk/debug/`

---

## 📦 Submission Contents

```
DateFinder/
├── APK/
│   └── datefinder.apk
├── Source/
│   └── (Full Android Studio project)
├── Docs/
│   ├── Architecture.md
│   ├── Algorithm.md
│   └── Setup-Testing.md
└── README.md
```

---

## 📄 Documentation Summary

### Architecture.md

* Describes MVVM, role of each class, data flow, and lifecycle considerations.

### Algorithm.md

* Regex-based extraction
* Prioritization of most recent date
* Fallback logic

### Setup-Testing.md

* Steps to build, run, test
* Sample test images
* Edge cases covered

---

## 📆 Suggested Timeline

* Completed within 7 days
* Fully functional, testable offline

If you need further changes, feature enhancements, or additional docs (e.g., diagrams or testing scripts), feel free to request!

---

*Developed with ❤️ in Kotlin, Offline-first.*
