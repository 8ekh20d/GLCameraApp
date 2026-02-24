# Contributing to GLCameraApp

Thanks for your interest in contributing! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

Be respectful, inclusive, and professional. We're all here to learn and build cool stuff together.

## How to Contribute

### Reporting Bugs

**Before submitting a bug report**:
- Check existing issues to avoid duplicates
- Test on the latest version
- Gather relevant information (device, Android version, logs)

**Bug report should include**:
- Clear, descriptive title
- Steps to reproduce
- Expected vs actual behavior
- Screenshots/videos if applicable
- Device information
- Logcat output (if available)

**Template**:
```markdown
**Device**: Samsung Galaxy S21
**Android Version**: 13
**App Version**: 1.0.0

**Steps to Reproduce**:
1. Open app
2. Switch to Sepia filter
3. Rotate device

**Expected**: Filter should remain applied
**Actual**: Filter resets to Normal

**Logs**:
[Paste relevant logcat output]
```

### Suggesting Features

**Feature requests should include**:
- Clear use case
- Why it's valuable
- Proposed implementation (optional)
- Mockups/examples (if UI-related)

**Template**:
```markdown
**Feature**: Blur filter

**Use Case**: Users want to blur backgrounds for privacy

**Proposed Implementation**:
- Add Gaussian blur GLSL shader
- Add blur radius parameter (adjustable)
- Add UI slider for intensity

**Examples**: Similar to Instagram blur effect
```

### Pull Requests

**Before starting work**:
1. Check if issue exists (create one if not)
2. Comment on issue to claim it
3. Wait for maintainer approval
4. Fork the repository

**Development workflow**:
1. Create feature branch: `git checkout -b feature/blur-filter`
2. Make changes following code style
3. Add/update tests
4. Update documentation
5. Commit with clear messages
6. Push and create PR

**PR checklist**:
- [ ] Code follows Kotlin conventions
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] No compiler warnings
- [ ] Tested on physical device
- [ ] Screenshots included (if UI changes)

**PR template**:
```markdown
## Description
Brief description of changes

## Related Issue
Fixes #123

## Changes Made
- Added blur filter
- Updated filter selector UI
- Added unit tests

## Testing
- Tested on Pixel 6 (Android 13)
- Tested on Samsung S21 (Android 12)
- All unit tests passing

## Screenshots
[Add screenshots if applicable]
```

## Development Setup

### Prerequisites
- Android Studio Ladybug | 2024.2.1+
- JDK 11+
- Android SDK (API 24-35)
- Physical Android device (recommended)

### Setup Steps

1. **Fork and clone**:
```bash
git clone https://github.com/YOUR_USERNAME/GLCameraApp.git
cd GLCameraApp
```

2. **Open in Android Studio**:
```bash
# File → Open → Select GLCameraApp directory
```

3. **Sync dependencies**:
```bash
# Android Studio will prompt automatically
# Or: File → Sync Project with Gradle Files
```

4. **Run tests**:
```bash
./gradlew test
```

5. **Run app**:
```bash
./gradlew installDebug
# Or click Run button in Android Studio
```

## Code Style

### Kotlin Conventions

Follow [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html):

**Naming**:
- Classes: `PascalCase`
- Functions: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Private properties: `_camelCase` (if backing property)

**Formatting**:
- Indentation: 4 spaces
- Line length: 120 characters max
- Braces: K&R style

**Example**:
```kotlin
class MyFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_custom
) {
    private var intensityHandle: Int = 0
    
    override fun initialize(context: Context) {
        super.initialize(context)
        intensityHandle = GLES20.glGetUniformLocation(program, "uIntensity")
    }
    
    fun setIntensity(value: Float) {
        // Implementation
    }
}
```

### Comments

**When to comment**:
- Complex algorithms
- Non-obvious decisions
- OpenGL-specific operations
- Workarounds for bugs

**When NOT to comment**:
- Obvious code
- Redundant explanations
- Commented-out code (delete it)

**Good comments**:
```kotlin
// Use luminance formula for perceptually accurate grayscale
val gray = dot(color.rgb, vec3(0.299, 0.587, 0.114))

// OES texture requires external texture extension
#extension GL_OES_EGL_image_external : require
```

**Bad comments**:
```kotlin
// Set the filter
setFilter(filter)

// i is the loop counter
for (i in 0..10) { }
```

### Architecture Guidelines

**MVVM Pattern**:
- ViewModels hold UI state
- Views observe state and emit events
- Models handle data/business logic

**Separation of Concerns**:
- UI code in `ui/` package
- Business logic in `viewmodel/` package
- Data/camera in `camera/` package
- Rendering in `gl/` package

**Dependency Direction**:
```
UI → ViewModel → Domain Logic → Data Layer
```

Never reverse dependencies (e.g., ViewModel shouldn't know about UI).

## Testing Guidelines

### Unit Tests

**What to test**:
- ViewModel logic
- State management
- Calculations (FPS, statistics)
- Edge cases

**Example**:
```kotlin
@Test
fun `updateFrameTime with zero should not crash`() {
    viewModel.updateFrameTime(0f)
    assertEquals(0f, viewModel.frameTime.value)
}

@Test
fun `FPS calculation should be accurate`() {
    viewModel.updateFrameTime(16.67f)
    val fps = 1000f / 16.67f
    assertEquals(fps, viewModel.fps.value, 0.1f)
}
```

**Test naming**: Use backticks for readable test names

**Coverage target**: Aim for >80% on business logic

### Manual Testing

**Test on multiple devices**:
- Different manufacturers (Samsung, Pixel, OnePlus)
- Different Android versions (API 24-35)
- Different screen sizes (phone, tablet)

**Test scenarios**:
- Fresh install
- Permission denial → grant
- Low memory conditions
- Poor lighting
- Rapid filter switching
- Device rotation
- App backgrounding/foregrounding

## Adding New Filters

### Step-by-Step Guide

**1. Create GLSL shader** (`app/src/main/res/raw/fragment_yourfilter.glsl`):
```glsl
#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2 vTexCoord;
uniform samplerExternalOES uTexture;

void main() {
    vec4 color = texture2D(uTexture, vTexCoord);
    
    // Your filter logic here
    vec4 filtered = color; // Replace with actual processing
    
    gl_FragColor = filtered;
}
```

**2. Create filter class** (`app/src/main/java/.../filters/YourFilter.kt`):
```kotlin
package com.peopleinnet.glcameraapp.filters

import com.peopleinnet.glcameraapp.R

class YourFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_yourfilter
)
```

**3. Register in MainActivity**:
```kotlin
private fun applyFilter(filterName: String) {
    val filter = when (filterName) {
        "Normal" -> NormalFilter()
        "Gray" -> GrayFilter()
        "Sepia" -> SepiaFilter()
        "Your Filter" -> YourFilter() // Add this
        else -> NormalFilter()
    }
    renderer.setFilter(filter)
}
```

**4. Add UI button** (`FilterSelector.kt`):
```kotlin
FilterChip(
    selected = selectedFilter == "Your Filter",
    onClick = { onFilterSelected("Your Filter") },
    label = { Text("Your Filter") }
)
```

**5. Test**:
- Compile and run
- Switch to your filter
- Verify visual output
- Check performance metrics
- Test on multiple devices

### Filter Best Practices

**Performance**:
- Keep shader code simple
- Avoid conditionals in shaders
- Use built-in GLSL functions
- Test on low-end devices

**Quality**:
- Preserve alpha channel
- Handle edge cases (black/white images)
- Maintain color accuracy
- Avoid artifacts

**Example filters to implement**:
- Invert colors
- Brightness/contrast adjustment
- Saturation control
- Vignette effect
- Edge detection
- Blur (Gaussian)

## Documentation

### When to Update Docs

**Always update**:
- README.md (if adding features)
- CHANGELOG.md (all changes)
- API.md (if changing public APIs)
- ARCHITECTURE.md (if changing structure)

**Code documentation**:
- Public APIs need KDoc comments
- Complex functions need explanations
- GLSL shaders need comments

**KDoc example**:
```kotlin
/**
 * Applies a custom filter to the camera preview.
 *
 * @param filter The filter to apply. Must be initialized before calling.
 * @throws IllegalStateException if called before surface is created
 */
fun setFilter(filter: GLFilter) {
    // Implementation
}
```

## Git Workflow

### Branch Naming

- Feature: `feature/blur-filter`
- Bug fix: `fix/camera-crash`
- Documentation: `docs/api-documentation`
- Refactor: `refactor/renderer-cleanup`

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

**Format**: `type(scope): description`

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting (no code change)
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance

**Examples**:
```
feat(filters): add blur filter with adjustable radius
fix(camera): prevent crash on permission denial
docs(readme): update installation instructions
refactor(renderer): simplify texture binding logic
test(viewmodel): add edge case tests for FPS calculation
```

**Good commits**:
- Atomic (one logical change)
- Descriptive
- Present tense
- Imperative mood

**Bad commits**:
- "Fixed stuff"
- "WIP"
- "asdf"
- Multiple unrelated changes

## Review Process

### For Contributors

**After submitting PR**:
- Respond to feedback promptly
- Make requested changes
- Push updates to same branch
- Re-request review when ready

**Be patient**: Maintainers review in their free time

### For Reviewers

**What to check**:
- Code quality and style
- Test coverage
- Documentation updates
- Performance impact
- Security implications

**Provide constructive feedback**:
- Explain "why" not just "what"
- Suggest alternatives
- Acknowledge good work
- Be respectful

## Getting Help

**Questions about**:
- Code: Open a discussion on GitHub
- Bugs: Create an issue
- Features: Create a feature request
- General: Comment on relevant issue

**Resources**:
- [Android Developer Docs](https://developer.android.com/)
- [OpenGL ES Reference](https://www.khronos.org/opengles/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [CameraX Guide](https://developer.android.com/training/camerax)

## Recognition

Contributors will be:
- Listed in README.md
- Credited in release notes
- Mentioned in CHANGELOG.md

Thank you for contributing! 🎉
