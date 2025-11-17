
# **Sanas Video Recorder App**

A simple & clean **CameraX Video Recording App** built with:

- **Kotlin**
- **MVVM + Clean Architecture**
- **Hilt Dependency Injection**
- **MediaStore API for saving videos**
- **CameraX VideoCapture for recording**
- **History screen listing saved videos**

---

## 🎥 **Demo Video**

> The recorded demo video is included in this repository.  
If you upload it to GitHub, place it in `/assets` and update the link below.

➡️ **Demo:** `assets/Screen_recording_20251117_001802.mp4`

---

## 📁 **Project Structure**

```
app/
 ├── data/
 │    ├── media/
 │    │     └── MediaStoreVideoDataSource.kt
 │    └── repo/
 │          └── CameraRepository.kt
 │
 ├── di/
 │    └── CameraModule.kt
 │
 ├── domain/
 │    └── model/
 │          └── RecordedVideo.kt
 │
 ├── ui/
 │    ├── camera/
 │    │      └── CameraFragment.kt
 │    ├── history/
 │    │      ├── HistoryFragment.kt
 │    │      └── VideoAdapter.kt
 │    └── viewmodel/
 │           ├── CameraViewModel.kt
 │           └── HistoryViewModel.kt
 │
 ├── MainActivity.kt
 └── SanasApp.kt
```

---

## 🏛️ **Architecture Overview**

The app follows **Clean Architecture + MVVM**, separated into:

### 🔹 **Data Layer**
- Responsible for low‑level operations
- Uses **MediaStore** to save & query video files
- `MediaStoreVideoDataSource` handles reading from the device

### 🔹 **Domain Layer**
- Holds the **business models** (e.g., `RecordedVideo`)
- Decoupled from Android SDK

### 🔹 **UI Layer**
- `CameraFragment` manages CameraX preview & recording
- `HistoryFragment` lists saved videos
- `VideoAdapter` maps domain models to UI recyclerview rows
- `CameraViewModel` & `HistoryViewModel` expose UI-ready state via `StateFlow`

### 🧩 **Hilt Dependency Injection**
- `CameraModule` provides CameraX Recorder + Repository instances
- ViewModels use `@HiltViewModel` for constructor injection
- Clean, testable, lifecycle‑aware dependency graph

---

## 🎯 **Key Features**

### ✔️ **CameraX Recording**
- Uses `Recorder` + `MediaStoreOutputOptions`
- Videos saved to:  
  `Movies/com.sanas.video/`

### ✔️ **Dynamic Red Recording Dot**
Shown when recording starts, hidden when stopped.

### ✔️ **History List**
- Auto‑loaded using MediaStore query
- Sorted by latest date
- Tap video → system video player chooser

### ✔️ **System Video Player**
Instead of custom playback, the app lets user choose:

```
Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(uri, "video/*")
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}
```

---

## ⚙️ **Why MediaStore Instead of File API?**

| Feature | MediaStore | File API |
|--------|------------|----------|
| Visible in Gallery | ✅ Yes | ❌ No |
| No storage permission on Android 10+ | ✅ Yes | ❌ Needs MANAGE_FILES |
| Auto indexing | ✅ Yes | ❌ No |
| Clean removal | Easy | Hard |

---

## ⚖️ **Architecture Decisions**

### ⭐ Chosen: Hilt + Clean Architecture
**Reasons:**
- Makes CameraViewModel & HistoryViewModel fully testable
- Repository abstracts MediaStore operations
- Easy to replace with custom backend later
- Reduces Fragment complexity

### ⭐ Chosen: System Video Player Instead of In-app Player
**Reasons:**
- Reduces complexity
- Let users pick VLC, Google Photos, MX Player, etc.
- No need for ExoPlayer setup

---

## ⚠️ **Known Limitations**

### ❌ **Videos are not deleted from MediaStore in UI**
- Currently listing only
- No delete/rename options yet

### ❌ **No custom in-app player**
- Can be added with ExoPlayer if desired

### ❌ **No background upload sync**
- Local-only for now

---

## 🚀 **Future Enhancements**

- Add **ExoPlayer** preview inside HistoryFragment
- Add **Swipe-to-delete**
- Add **Cloud upload** support
- Add camera switch (front/back)
- Add video trimming
- Add settings screen for bitrate/resolution

---

## 📄 **License**
MIT — free to use & modify.

---

## 🧑‍💻 **Author**
Built by **Bhaumik** (2025)

