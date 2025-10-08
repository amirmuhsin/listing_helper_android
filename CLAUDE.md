# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Listing Helper is an Android application that helps users create product listings with photos. The app captures photos, removes backgrounds using the PhotoRoom API, and uploads product images. It follows a multi-step workflow from product creation through photo capture, background cleaning, and final upload.

## Build & Test Commands

```bash
# Build the project
./gradlew build

# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK (signed)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run specific variant tests
./gradlew testDebugUnitTest

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedDebugAndroidTest

# Lint checks
./gradlew lint

# Auto-fix lint issues
./gradlew lintFix

# Install debug build to device
./gradlew installDebug

# Clean build artifacts
./gradlew clean
```

## Architecture

### Core Application Structure

- **Single Activity Architecture**: `MainActivity` hosts a Navigation Component with multiple fragments
- **MVVM Pattern**: Each screen has a Fragment + ViewModel + optional ViewModelFactory
- **Base Classes**:
  - `BaseViewModel`: Provides loading states, snackbar messages, and command flow for all ViewModels
  - `BaseFragment`: Handles lifecycle, progress dialogs, snackbars, and keyboard management
  - `BaseActivity`: Similar to BaseFragment for Activity implementations

### Data Layer

**Room Database** (`AppDatabase.kt`):
- Two main entities: `ProductEntity` and `PhotoPairEntity`
- Repositories follow Repository pattern with domain models separate from database entities
- Mappers convert between domain models and database entities

**Network Layer**:
- Retrofit for API calls
- `PhotoRoomNetworkModule`: PhotoRoom API integration with sandbox/live mode toggle
- `ProductNetworkModule`: Product and image upload services
- API keys are hardcoded in `PhotoRoomNetworkModule.kt` (lines 17-18)

### Domain Models

**Product** (`domain/product/Product.kt`):
- Core business entity representing a product listing
- Status enum: DRAFT, DONE, HAS_FAILURE

**PhotoPair** (`domain/photo/PhotoPair.kt`):
- Represents a pair of original and background-cleaned photos
- Tracks background cleaning status (PENDING, PROCESSING, COMPLETED, FAILED)
- Tracks upload status (PENDING, UPLOADING, UPLOADED, FAILED)

### UI Flow (Screen Navigation)

The app follows a numbered workflow pattern (s1, s2, s3, s4, s5):

1. **s1_home** (`HomeFragment`): Product list view, create new products
2. **s2_0_product_detail** (`ProductDetailFragment`): View/edit product details and photos
3. **s2_1_barcode_scanner** (`BarcodeScannerActivity`): Scan barcodes for product lookup
4. **s3_photo_capture** (`PhotoCaptureFragment`): Capture photos using CameraX
5. **s4_bg_clean** (`BgCleanerFragment`): Background removal using PhotoRoom API
6. **s5_review_upload** (`ReviewUploadFragment`): Review and upload final images with drag-to-reorder

Navigation is managed via `nav_main_graph.xml` with actions defined between screens.

### Command Pattern

The app uses a Command pattern for cross-component communication:
- ViewModels emit `Command` objects via `BaseViewModel.sendCommand()`
- Fragments/Activities handle commands in `handleCommand(command: Command)`
- `GlobalEventManager`: Singleton for app-wide events (logout, version updates)

Each screen has its own command definitions (e.g., `HomeCommands`, `MainCommands`, `ReviewUploadCommands`)

### View Binding

All layouts use ViewBinding (enabled in `app/build.gradle:47`). Bindings are created in Fragment/Activity constructors:
```kotlin
class HomeFragment: BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
)
```

### Image Handling

- `ImageStore`: Utility for copying shared images to app-private storage
- `ImageUtil`: Image manipulation utilities
- CameraX for photo capture
- Coil library for image loading

### Key Features

**Intent Handling** (`MainActivity.kt:41-64`):
- Receives shared images via ACTION_SEND and ACTION_SEND_MULTIPLE
- Automatically creates new product with shared images
- Uses proper API level handling for Parcelable extras

**PhotoRoom Integration**:
- Sandbox mode toggle available in HomeFragment
- API key managed in `PhotoRoomNetworkModule`
- Background removal processed in `BgCleanerFragment`

**Drag-to-Reorder** (`ReviewUploadFragment`):
- Uses ItemTouchHelper with custom callback
- Adapter implements `ItemTouchHelperAdapter` interface

## Development Notes

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35
- **Java Version**: 18
- **Core Libraries**: AndroidX, Navigation Component, Room, Retrofit, CameraX, Coil
- **Barcode Scanning**: ZXing library (`journeyapps:zxing-android-embedded`)
- **Release Signing**: Keystore at root level `lh_release.jks` with credentials in `app/build.gradle:24-27`

## Repository Pattern

Each domain entity has:
1. Domain model (e.g., `Product`)
2. Database entity (e.g., `ProductEntity`)
3. Mapper (e.g., `ProductMapper`)
4. Local repository interface (e.g., `ProductLocalRepository`)
5. Local repository implementation (e.g., `ProductLocalRepositoryImpl`)
6. Remote repository interface (e.g., `ProductRemoteRepository`)
7. Remote repository implementation (e.g., `ProductRemoteRepositoryImpl`)

## Testing

- Unit tests located in `app/src/test/`
- Instrumented tests in `app/src/androidTest/`
- Currently minimal test coverage with example tests only
