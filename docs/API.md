# API Documentation

## Public APIs

### MainViewModel

State management for the main screen.

#### Properties

```kotlin
val frameTime: StateFlow<Float>
```
Current frame rendering time in milliseconds.

```kotlin
val averageFrameTime: StateFlow<Float>
```
Average frame time across all frames.

```kotlin
val minFrameTime: StateFlow<Float>
```
Minimum (best) frame time recorded.

```kotlin
val maxFrameTime: StateFlow<Float>
```
Maximum (worst) frame time recorded.

```kotlin
val showMetrics: StateFlow<Boolean>
```
Whether performance metrics overlay is visible.

#### Methods

```kotlin
fun updateFrameTime(timeMs: Float)
```
Updates frame time and recalculates statistics.
- **Parameters**: `timeMs` - Frame rendering time in milliseconds
- **Thread Safety**: Safe to call from any thread

```kotlin
fun toggleMetrics()
```
Toggles the visibility of performance metrics overlay.

```kotlin
fun resetMetrics()
```
Resets all performance statistics to initial values.

---

### GLCameraRenderer

OpenGL ES renderer for camera preview with filters.

#### Constructor

```kotlin
GLCameraRenderer(
    context: Context,
    onFrameRendered: (Float) -> Unit
)
```
- **Parameters**:
  - `context` - Android context for resource access
  - `onFrameRendered` - Callback invoked after each frame with render time

#### Methods

```kotlin
fun setFilter(filter: GLFilter)
```
Changes the active filter. Thread-safe.
- **Parameters**: `filter` - New filter to apply
- **Error Handling**: Falls back to NormalFilter on failure

```kotlin
fun getSurfaceTexture(): SurfaceTexture?
```
Returns the surface texture for camera binding.
- **Returns**: SurfaceTexture or null if not initialized

```kotlin
override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?)
```
GLSurfaceView.Renderer callback. Initializes OpenGL resources.

```kotlin
override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int)
```
GLSurfaceView.Renderer callback. Handles surface size changes.

```kotlin
override fun onDrawFrame(gl: GL10?)
```
GLSurfaceView.Renderer callback. Renders each frame.

---

### CameraXController

Manages CameraX lifecycle and camera operations.

#### Constructor

```kotlin
CameraXController(
    context: Context,
    lifecycleOwner: LifecycleOwner
)
```

#### Methods

```kotlin
fun startCamera(surfaceTexture: SurfaceTexture)
```
Initializes and starts the camera preview.
- **Parameters**: `surfaceTexture` - Target surface for camera frames
- **Throws**: Exception on camera initialization failure

```kotlin
fun stopCamera()
```
Stops the camera preview and releases resources.

```kotlin
fun releaseCamera()
```
Completely releases camera resources. Call on activity destroy.

---

### GLFilter Interface

Base interface for all OpenGL filters.

```kotlin
interface GLFilter {
    fun initialize(context: Context)
    fun draw(textureId: Int, mvpMatrix: FloatArray)
    fun release()
}
```

#### Methods

```kotlin
fun initialize(context: Context)
```
Initializes the filter (loads and compiles shaders).
- **Parameters**: `context` - Android context for resource access
- **Throws**: RuntimeException on shader compilation failure

```kotlin
fun draw(textureId: Int, mvpMatrix: FloatArray)
```
Renders a frame with the filter applied.
- **Parameters**:
  - `textureId` - OpenGL texture ID (OES texture)
  - `mvpMatrix` - Model-View-Projection matrix (16 floats)

```kotlin
fun release()
```
Releases OpenGL resources. Must be called on GL thread.

---

### BaseFilter

Abstract base class implementing common filter functionality.

#### Constructor

```kotlin
abstract class BaseFilter(
    @RawRes protected val vertexRes: Int,
    @RawRes protected val fragmentRes: Int
) : GLFilter
```
- **Parameters**:
  - `vertexRes` - Resource ID of vertex shader
  - `fragmentRes` - Resource ID of fragment shader

#### Protected Properties

```kotlin
protected var program: Int = 0
```
OpenGL shader program handle.

```kotlin
protected var positionHandle: Int = 0
protected var texCoordHandle: Int = 0
protected var mvpMatrixHandle: Int = 0
protected var textureHandle: Int = 0
```
Shader attribute and uniform handles.

---

### Built-in Filters

#### NormalFilter
Pass-through filter with no processing.

```kotlin
class NormalFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_oes
)
```

#### GrayFilter
Converts image to grayscale using luminance formula.

```kotlin
class GrayFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_gray
)
```

#### SepiaFilter
Applies sepia tone effect.

```kotlin
class SepiaFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_sepia
)
```

---

## Utility Classes

### GLHelper

OpenGL utility functions.

```kotlin
object GLHelper {
    fun checkGLError(tag: String, operation: String)
}
```
Checks for OpenGL errors and logs them.
- **Parameters**:
  - `tag` - Log tag
  - `operation` - Description of the operation
- **Throws**: RuntimeException if GL error detected

### ShaderLoader

GLSL shader compilation utilities.

```kotlin
object ShaderLoader {
    fun loadShader(type: Int, shaderCode: String): Int
    fun createProgram(vertexShader: Int, fragmentShader: Int): Int
    fun loadShaderFromResource(context: Context, resourceId: Int): String
}
```

#### Methods

```kotlin
fun loadShader(type: Int, shaderCode: String): Int
```
Compiles a shader.
- **Parameters**:
  - `type` - GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
  - `shaderCode` - GLSL source code
- **Returns**: Shader handle or 0 on failure

```kotlin
fun createProgram(vertexShader: Int, fragmentShader: Int): Int
```
Links shaders into a program.
- **Returns**: Program handle or 0 on failure

```kotlin
fun loadShaderFromResource(context: Context, resourceId: Int): String
```
Loads shader source from raw resource.
- **Returns**: Shader source code as string

---

## Usage Examples

### Basic Setup

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var renderer: GLCameraRenderer
    private lateinit var cameraController: CameraXController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        renderer = GLCameraRenderer(this) { frameTime ->
            viewModel.updateFrameTime(frameTime)
        }
        cameraController = CameraXController(this, this)
    }
}
```

### Switching Filters

```kotlin
// In your composable or activity
fun onFilterSelected(filterName: String) {
    val filter = when (filterName) {
        "Normal" -> NormalFilter()
        "Gray" -> GrayFilter()
        "Sepia" -> SepiaFilter()
        else -> NormalFilter()
    }
    renderer.setFilter(filter)
}
```

### Monitoring Performance

```kotlin
// Collect metrics in composable
val frameTime by viewModel.frameTime.collectAsState()
val avgFrameTime by viewModel.averageFrameTime.collectAsState()
val fps = if (frameTime > 0) 1000f / frameTime else 0f

Text("FPS: ${fps.toInt()}")
Text("Avg: ${"%.2f".format(avgFrameTime)} ms")
```

### Custom Filter Implementation

```kotlin
class BlurFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_blur
) {
    private var blurRadiusHandle: Int = 0
    var blurRadius: Float = 5.0f
    
    override fun initialize(context: Context) {
        super.initialize(context)
        blurRadiusHandle = GLES20.glGetUniformLocation(program, "uBlurRadius")
    }
    
    override fun draw(textureId: Int, mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)
        GLES20.glUniform1f(blurRadiusHandle, blurRadius)
        super.draw(textureId, mvpMatrix)
    }
}
```

---

## Thread Safety

### Main Thread
- All ViewModel methods
- UI state updates
- Composable functions

### GL Thread
- GLCameraRenderer methods
- Filter draw() calls
- OpenGL operations

### Camera Thread
- CameraX callbacks
- Frame capture

**Important**: Never call OpenGL functions from non-GL threads. Use `GLSurfaceView.queueEvent()` for thread-safe GL operations.

---

## Error Codes

### Camera Errors
- `ERROR_CAMERA_IN_USE` - Camera already in use by another app
- `ERROR_MAX_CAMERAS_IN_USE` - Device camera limit reached
- `ERROR_CAMERA_DISABLED` - Camera disabled by policy
- `ERROR_CAMERA_FATAL_ERROR` - Unrecoverable camera error

### OpenGL Errors
- `GL_INVALID_ENUM` - Invalid enum parameter
- `GL_INVALID_VALUE` - Invalid value parameter
- `GL_INVALID_OPERATION` - Invalid operation for current state
- `GL_OUT_OF_MEMORY` - Not enough memory

All errors are logged with appropriate tags for debugging.
