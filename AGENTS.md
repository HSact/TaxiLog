# TaxiLog - AI Agent Project Guide

**Persona:** You are an expert Senior Android Developer specializing in Clean Architecture and offline-first synchronization.

This document serves as a comprehensive overview of the TaxiLog project architecture, tech stack, and development patterns to assist AI agents in understanding and contributing to the codebase.

## 📖 Domain Context & Terminology

- **Shift**: The core entity representing a taxi driver's work period. Includes time, finance (earnings, tips, costs), and vehicle state (mileage, fuel).
- **Offline-First**: All data is initially persisted to the local Room DB. Synchronization with Firebase happens in the background.
- **Synced State**: A shift is considered "synced" only when it exists in Firebase and matches the local version. In Firebase, `isSynced` is always `true`.

## 🏗 Project Architecture
... (rest of the file remains same or slightly adjusted)

The project follows **Clean Architecture** principles and is divided into four main Gradle modules:

### 1. `:domain` (Pure Kotlin)
The core of the application. It contains business logic and is independent of any frameworks (Android, Firebase, etc.).
- **Models**: Business entities like `Shift`, `ShiftMeta`, `CarSnapshot`.
- **Repository Interfaces**: Define the contracts for data operations (e.g., `ShiftRepository`).
- **Use Cases**: Single-action classes that encapsulate specific business rules (e.g., `AddShiftUseCase`, `SyncShiftsUseCase`).

### 2. `:data` (Android Library)
Implements the repository interfaces defined in the `:domain` module.
- **Local Data**: `Room` database (`ShiftDao`, `ShiftEntity`).
- **Remote Data**: `Firebase Firestore` (`FirebaseShiftDataSource`).
- **Synchronization**: `ShiftSyncManager` handles bidirectional sync between Room and Firebase.
- **Mappers**: Convert between Entity/Firebase models and Domain models.

### 3. `:app` (Android App)
The UI layer, following the **MVVM** pattern.
- **UI Framework**: **Jetpack Compose** (Preferred for all new code). Legacy **XML Views** exist in the project but should not be used for new features.
- **ViewModels**: Manage UI state and interact with Use Cases.
- **State Management**: **StateFlow** (Preferred).

### 4. `:di` (Android Library)
Centralized Dependency Injection module using **Hilt**.

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **Dependency Injection**: Hilt
- **Database**: Room
- **Backend**: Firebase Firestore & Auth
- **Concurrency**: Coroutines & Flow
- **Background Tasks**: WorkManager (for sync)
- **UI**: Jetpack Compose & XML (Fragments/Activities)

---

## 📋 Development Best Practices

### 1. Separation of Concerns
- **Domain First**: Business logic belongs in `:domain`. Never add Android dependencies to this module.
- **Use Cases**: UI should interact with repositories *only* through Use Cases. This makes the logic reusable and testable.

### 2. Data Mapping
- Always use **Mappers** when passing data between layers.
- `Entity` -> `Domain` (in `:data`)
- `FirebaseDTO` -> `Domain` (in `:data`)
- `Domain` -> `UiModel` (in `:app`)

### 3. Synchronization Pattern
The app follows a strict **Offline-First** approach with Unidirectional Data Flow (UDF) in the UI:
1. Data is saved locally to Room via Use Cases.
2. `WorkManager` (or `ShiftSyncManager`) is triggered to push changes to Firebase.
3. Successful sync updates the local `isSynced` flag and `remoteId`.
4. The `isSynced` flag in Firebase should always be `true`.

### 4. Error Handling
- Use `Result` or similar wrappers for network/database operations.
- Ensure logging is meaningful (use `Log.d` or `Log.e` with clear tags).

---

## 📂 Key File Locations

- **Use Cases**: `domain/src/main/java/com/hsact/domain/usecase/`
- **Domain Models**: `domain/src/main/java/com/hsact/domain/model/`
- **Sync Logic**: `data/src/main/java/com/hsact/data/sync/ShiftSyncManager.kt`
- **Room Dao**: `data/src/main/java/com/hsact/data/db/ShiftDao.kt`
- **Firebase DataSource**: `data/src/main/java/com/hsact/data/firebase/datasource/`
- **UI Components**: `app/src/main/java/com/hsact/taxilog/ui/`

---

## 🤖 Guide for AI Agents

When assisting with this project:
- **Persona Role**: Act as a Senior Developer. Be concise, prioritize safety (don't break offline data), and follow Clean Architecture.
- **Use Modern Stack for New Code**: All new UI components must be written in **Jetpack Compose**. Use **StateFlow** or **SharedFlow** for state management in new ViewModels.
- **Prioritize Clean Architecture**: If adding a new feature, start with the `:domain` models and use cases.
- **Maintain Consistency**: Check existing mappers and repository implementations before creating new ones.
- **Check DI**: Ensure new components are properly annotated with `@Inject` and provided in Hilt modules if needed.
- **Lifecycle Awareness**: Use `viewModelScope` for coroutines in ViewModels and `collectAsStateWithLifecycle` in Compose.
- **Documentation**: Provide **KDoc** for all new classes, interfaces, and non-obvious public methods. KDoc for internal or private methods is only required if the logic is complex.
- **Theming & Colors**: Do not hardcode colors (e.g., `Color.Black`, `Color(0xFF...)`). Always use theme-aware colors from `MaterialTheme.colorScheme` (e.g., `onSurface`, `primary`) to ensure support for both light and dark themes.
- **UI State Structure**: When defining UI states (e.g., `UiState`), use a `sealed interface`. Always nest the state implementations (e.g., `data object Loading`, `data class Success`) directly inside the interface to maintain a clean, grouped structure.
