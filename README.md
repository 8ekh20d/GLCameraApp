# 📸 GLCameraApp

A high-performance Android camera application with real-time OpenGL ES 2.0 filters. Built with Jetpack Compose, CameraX, and custom GLSL shaders for professional-grade image processing.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## ✨ Features

- **Real-time OpenGL Rendering** - Hardware-accelerated camera preview with OpenGL ES 2.0
- **Custom GLSL Filters** - Grayscale, Sepia, and Normal filters with extensible architecture
- **CameraX Integration** - Modern camera API with lifecycle-aware components
- **Jetpack Compose UI** - Declarative UI with Material 3 design
- **Performance Metrics** - Real-time FPS monitoring and frame time analysis
- **Efficient Rendering** - OES texture streaming for zero-copy camera frames
- **MVVM Architecture** - Clean separation of concerns with ViewModel pattern
- **Permission Handling** - Runtime camera permission with user-friendly prompts
- **Lifecycle Management** - Proper resource cleanup and state handling

## 🛠️ Technologies

### Core Framework
- **Kotlin** - 100% Kotlin codebase with coroutines support
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material 3** - Latest Material Design components
- **CameraX** - Unified camera API (v1.3.2)

### Graphics & Rendering
- **OpenGL ES 2.0** - Hardware-accelerated graphics rendering
- **GLSL Shaders** - Custom vertex and fragment shaders
- **SurfaceTexture** - Efficient camera frame streaming
- **GLSurfaceView** - OpenGL rendering surface

### Architecture Components
- **ViewModel** - UI state management
- **Lifecycle** - Lifecycle-aware components
- **Compose Runtime** - Reactive state management

### Build & Tools
- **Gradle 8.10.1** - Build automation
- **Android Gradle Plugin 8.10.1** - Android build system
- **Kotlin 2.0.21** - Latest Kotlin compiler

## 📁 Project Structure

```
GLCameraApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/peopleinnet/glcameraapp/
│   │   │   │   ├── camera/
│   │   │   │   │   └── CameraXController.kt      # CameraX lifecycle management
│   │   │   │   ├── filters/
│   │   │   │   │   ├── BaseFilter.kt             # Abstract filter base class
│   │   │   │   │   ├── GLFilter.kt               # Filter interface
│   │   │   │   │   ├── GrayFilter.kt             # Grayscale filter
│   │   │   │   │   ├── NormalFilter.kt           # Pass-through filter
│   │   │   │   │   └── SepiaFilter.kt            # Sepia tone filter
│   │   │   │   ├── gl/
│   │   │   │   │   ├── GLCameraRenderer.kt       # OpenGL renderer
│   │   │   │   │   ├── GLHelper.kt               # OpenGL utilities
│   │   │   │   │   └── ShaderLoader.kt           # GLSL shader loader
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── CameraPreview.kt      # GL surface view wrapper
│   │   │   │   │   │   ├── FilterSection.kt      # Filter UI section
│   │   │   │   │   │   ├── FilterSelector.kt     # Filter selection chips
│   │   │   │   │   │   ├── MainScreen.kt         # Main composable screen
│   │   │   │   │   │   ├── MetricsOverlay.kt     # Performance metrics display
│   │   │   │   │   │   └── MetricsToggleButton.kt # Metrics toggle button
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt              # Color palette
│   │   │   │   │       ├── Theme.kt              # App theme
│   │   │   │   │       └── Type.kt               # Typography
│   │   │   │   ├── viewmodel/
│   │   │   │   │   └── MainViewModel.kt          # UI state management
│   │   │   │   ├── GLCameraApp.kt                # Application class
│   │   │   │   └── MainActivity.kt               # Main activity
│   │   │   ├── res/
│   │   │   │   ├── raw/
│   │   │   │   │   ├── fragment_gray.glsl        # Grayscale fragment shader
│   │   │   │   │   ├── fragment_oes.glsl         # OES texture fragment shader
│   │   │   │   │   ├── fragment_sepia            # Sepia fragment shader
│   │   │   │   │   └── vertex_shader.glsl        # Vertex shader
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml                # Color resources
│   │   │   │       ├── strings.xml               # String resources
│   │   │   │       └── themes.xml                # Theme resources
│   │   │   └── AndroidManifest.xml               # App manifest
│   │   ├── androidTest/                          # Instrumented tests
│   │   └── test/                                 # Unit tests
│   ├── build.gradle.kts                          # App build configuration
│   └── proguard-rules.pro                        # ProGuard rules
├── gradle/
│   ├── libs.versions.toml                        # Version catalog
│   └── wrapper/                                  # Gradle wrapper
├── build.gradle.kts                              # Root build configuration
├── settings.gradle.kts                           # Project settings
└── README.md                                     # Project documentation
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** - Ladybug | 2024.2.1 or later
- **JDK** - Java 11 or higher
- **Android SDK** - API 24 (Android 7.0) or higher
- **Physical Device** - Recommended for camera testing (emulator camera support is limited)

### Installation

1. Clone the repository
```bash
git clone https://github.com/peopleinnet/glcameraapp.git
cd glcameraapp
```

2. Open the project in Android Studio
```bash
# Open Android Studio and select "Open an Existing Project"
# Navigate to the cloned directory
```

3. Sync Gradle dependencies
```bash
# Android Studio will automatically prompt to sync
# Or manually: File → Sync Project with Gradle Files
```

4. Connect your Android device
```bash
# Enable USB debugging on your device
# Connect via USB and authorize the computer
```

5. Run the application
```bash
# Click the "Run" button in Android Studio
# Or use: ./gradlew installDebug
```

## 🎨 Architecture

### MVVM Pattern

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   View      │────────▶│  ViewModel   │────────▶│   Model     │
│  (Compose)  │◀────────│ (StateHolder)│◀────────│  (Camera)   │
└─────────────┘         └──────────────┘         └─────────────┘
```

### OpenGL Rendering Pipeline

```
Camera Frame → SurfaceTexture → OES Texture → GLSL Shader → GLSurfaceView
                                      ↓
                              Filter Processing
                              (Gray/Sepia/Normal)
```

### Component Interaction

```
MainActivity
    ├── MainViewModel (State Management)
    ├── GLCameraRenderer (OpenGL Rendering)
    │   ├── SurfaceTexture (Camera Frames)
    │   └── GLFilter (Shader Programs)
    └── CameraXController (Camera Lifecycle)
```

## 🎯 Key Components

### GLCameraRenderer
- Manages OpenGL ES rendering context
- Handles OES texture creation and binding
- Processes camera frames in real-time
- Applies GLSL filters dynamically
- Tracks frame metrics (FPS, frame time)

### CameraXController
- Manages CameraX lifecycle
- Configures camera preview use case
- Binds camera to SurfaceTexture
- Handles camera start/stop/release

### Filter System
- **BaseFilter** - Abstract base with shader compilation
- **NormalFilter** - Pass-through (no processing)
- **GrayFilter** - Grayscale conversion
- **SepiaFilter** - Sepia tone effect

### MainViewModel
- Manages UI state reactively
- Tracks performance metrics
- Calculates FPS from frame time
- Provides min/max/average statistics

## 🔧 Adding Custom Filters

1. Create a new GLSL fragment shader in `res/raw/`
```glsl
#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTexCoord;
uniform samplerExternalOES uTexture;

void main() {
    vec4 color = texture2D(uTexture, vTexCoord);
    // Apply your custom effect here
    gl_FragColor = color;
}
```

2. Create a filter class extending `BaseFilter`
```kotlin
class CustomFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_custom
)
```

3. Register the filter in `MainActivity`
```kotlin
when (filter) {
    "Custom" -> renderer.setFilter(CustomFilter())
    // ... other filters
}
```

4. Add UI button in `FilterSelector.kt`
```kotlin
FilterChip(
    selected = selectedFilter == "Custom",
    onClick = { onFilterSelected("Custom") },
    label = { Text("Custom") }
)
```

## 📊 Performance Metrics

The app provides real-time performance monitoring:

- **Frame Time** - Time to render each frame (ms)
- **FPS** - Frames per second (calculated from frame time)
- **Average** - Mean frame time across all frames
- **Min/Max** - Best and worst frame times

Toggle metrics with the button in the top-right corner.

## 🔐 Permissions

The app requires the following permissions:

- **CAMERA** - Access device camera for preview and capture

Permissions are requested at runtime with proper rationale and settings guidance.

## 🏗️ Build Variants

### Debug
```bash
./gradlew assembleDebug
```
- Debugging enabled
- No code obfuscation
- Larger APK size

### Release
```bash
./gradlew assembleRelease
```
- Optimized for production
- ProGuard/R8 enabled (when configured)
- Smaller APK size

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## 📱 Supported Devices

- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 35 (Android 15)
- **Architecture**: ARM, ARM64, x86, x86_64
- **OpenGL ES**: 2.0 or higher required

## 🐛 Troubleshooting

### Camera not working
- Ensure camera permissions are granted
- Check if device has a camera
- Test on a physical device (emulator camera is limited)

### Black screen
- Verify OpenGL ES 2.0 support
- Check shader compilation logs
- Ensure proper lifecycle management

### Performance issues
- Reduce preview resolution in `CameraXController`
- Optimize GLSL shader code
- Test on different devices

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**8ekh20d**

- GitHub: [@8ekh20d](https://github.com/8ekh20d/GLCameraApp)

## 🙏 Acknowledgments

- [CameraX](https://developer.android.com/training/camerax) - Modern camera API
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Declarative UI toolkit
- [OpenGL ES](https://www.khronos.org/opengles/) - Graphics rendering API
- [Material Design 3](https://m3.material.io/) - Design system

## 📚 Resources

- [Android Camera Documentation](https://developer.android.com/training/camera)
- [OpenGL ES for Android](https://developer.android.com/guide/topics/graphics/opengl)
- [GLSL Shader Reference](https://www.khronos.org/opengl/wiki/OpenGL_Shading_Language)
- [Jetpack Compose Samples](https://github.com/android/compose-samples)

## 🔮 Roadmap

- [ ] Add more filters (Blur, Edge Detection, Vignette)
- [ ] Implement photo capture functionality
- [ ] Add video recording with filters
- [ ] Support for multiple cameras (front/back)
- [ ] Real-time filter parameter adjustment
- [ ] Save/load custom filter presets
- [ ] Export performance metrics
- [ ] Add unit and integration tests

---

⭐ Star this repository if you find it helpful!
