# 📁 Archives

> A full-featured Android student productivity app for managing tasks, schedules, course files, and user profiles — built with Kotlin using modern Android architecture patterns.

---

## 📖 Description

**Archives** is a native Android application designed for students to stay organized throughout their academic life. It combines task management, a weekly schedule planner, course folder organization with real file storage, and a personalized user profile — all backed by a local Room database and built with clean architecture principles including MVVM, Hilt dependency injection, and the Navigation Component.

This is the second and fully-featured iteration of the Archives project, developed for a Mobile Development course at **Cebu Institute of Technology - University (CIT-U)**.

---

## ✨ Features

- 🔐 **Authentication** — Register and log in with local credential storage via Room DB and SharedPreferences
- 🏠 **Home / Task Manager** — Create, view, complete, and delete tasks with emoji icons; tabs for To-Do and Completed tasks
- 📚 **Course File Manager** — Organize files into course folders, import any file type from device storage, open files with external apps, and swipe-to-delete with undo
- 📅 **Schedule Planner** — Weekly calendar view for managing class schedules with time slots, location, and color coding
- 👤 **Profile & Settings** — View and edit your profile photo, full name, birthday, program, and school
- 👨‍💻 **Developer Page** — Info about the developers behind the app
- 🌙 **Dark Mode Support** — Night theme included

---

## 🏗️ Architecture & Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Architecture | MVVM (ViewModel + StateFlow) |
| Dependency Injection | Hilt (Dagger) |
| Local Database | Room (SQLite) with DAOs |
| Navigation | Jetpack Navigation Component |
| UI | XML Layouts + ViewBinding |
| Async | Kotlin Coroutines |
| Image Loading | Glide |
| Image Cropping | uCrop |
| Calendar View | Android-Week-View |
| Emoji Picker | Jetpack Emoji2 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| Build System | Gradle (Kotlin DSL + Version Catalogs) |

---

## 🗄️ Database Schema

```
users       ─┬─< folders   ─┬─< files
             ├─< tasks      │
             └─< schedules  │
```

| Table | Key Fields |
|-------|-----------|
| `users` | userId, username, password, fullName, birthday, program, school, pictureFilePath |
| `tasks` | taskId, title, description, emojiIcon, isComplete, userId |
| `schedules` | scheduleId, title, location, colorType, date, startTime, endTime, userId |
| `folders` | folderId, name, iconRes, userId |
| `files` | fileId, fileName, fileType, filePath, folderId |

---

## 📂 Project Structure

```
app/src/main/java/com/android/archives/
├── application/        # ArchivesApplication (Hilt entry point)
├── constants/          # Enums (ScheduleColorType)
├── data/
│   ├── dao/            # Room DAOs (User, Task, Schedule, Folder, File)
│   ├── db/             # ArchivesDatabase + TypeConverters
│   ├── model/          # Entity data classes
│   └── service/        # SharedPrefsService
├── di/                 # Hilt AppModule (DI bindings)
├── ui/
│   ├── activity/       # StartupActivity, AuthActivity, MainActivity
│   ├── adapter/        # RecyclerView & WeekView adapters
│   ├── event/          # UI events (Task, File, Folder, Schedule, User)
│   ├── fragment/
│   │   ├── auth/       # Landing, Onboarding, Login, Register
│   │   └── main/       # Home, Tasks, Courses, Files, Schedule, Settings, Profile, Developer
│   └── viewmodel/      # ViewModels per feature
└── utils/              # FileHelper, extension functions
```
## 👨‍💻 Developers

| Name | Background |
|------|-----------|
| **Ivann James Paradero** | Layout designer & UI enthusiast from Lapu-Lapu City. Passionate about Figma, hiking, and diving. |
| **Carl Angelo T. Pepino** | CS student at CIT-U, Cebu City. Competitive programmer-in-training who loves cooking, exercising, and coding. |

---

## 📄 License

This project was developed for academic purposes as part of a Mobile Development course at Cebu Institute of Technology - University.
