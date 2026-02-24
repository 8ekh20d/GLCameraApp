# Deployment Guide

## Pre-Deployment Checklist

### Code Quality
- [ ] All unit tests passing
- [ ] No compiler warnings
- [ ] Code reviewed and approved
- [ ] Documentation up to date
- [ ] CHANGELOG.md updated

### Security
- [ ] ProGuard rules configured
- [ ] No hardcoded secrets or API keys
- [ ] Permissions properly declared
- [ ] Network security config set
- [ ] Code obfuscation enabled

### Performance
- [ ] App size optimized (<50MB)
- [ ] Memory leaks checked (LeakCanary)
- [ ] Battery usage tested
- [ ] Startup time <2 seconds
- [ ] No ANRs (Application Not Responding)

### Testing
- [ ] Tested on multiple devices (min API 24, target API 35)
- [ ] Tested on different screen sizes
- [ ] Tested with different camera hardware
- [ ] Tested in low memory conditions
- [ ] Tested with poor lighting conditions

---

## Build Configuration

### Release Build Setup

Update `app/build.gradle.kts`:

```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Signing config
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    // Signing configuration
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}
```

### ProGuard Rules

Add to `proguard-rules.pro`:

```proguard
# Keep OpenGL classes
-keep class android.opengl.** { *; }
-keep class javax.microedition.khronos.** { *; }

# Keep CameraX classes
-keep class androidx.camera.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}

# Keep filter classes
-keep class com.peopleinnet.glcameraapp.filters.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
```

---

## Building Release APK

### Command Line

```bash
# Clean build
./gradlew clean

# Build release APK
./gradlew assembleRelease

# Output location
# app/build/outputs/apk/release/app-release.apk
```

### Android Studio

1. Build → Generate Signed Bundle/APK
2. Select APK
3. Choose or create keystore
4. Select release build variant
5. Click Finish

---

## Building App Bundle (AAB)

For Google Play Store submission:

```bash
# Build release bundle
./gradlew bundleRelease

# Output location
# app/build/outputs/bundle/release/app-release.aab
```

---

## Keystore Management

### Generate Keystore

```bash
keytool -genkey -v -keystore release.keystore \
  -alias glcameraapp \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### Keystore Security

**DO NOT commit keystore to version control!**

Add to `.gitignore`:
```
*.keystore
*.jks
keystore.properties
```

Store credentials securely:
- Use environment variables
- Use CI/CD secrets
- Use Android Studio's encrypted storage

---

## Version Management

### Update Version

In `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2  // Increment for each release
    versionName = "1.1.0"  // Semantic versioning
}
```

### Versioning Strategy

Follow [Semantic Versioning](https://semver.org/):
- MAJOR: Breaking changes
- MINOR: New features (backward compatible)
- PATCH: Bug fixes

Example:
- 1.0.0 → Initial release
- 1.1.0 → Added new filters
- 1.1.1 → Fixed camera crash
- 2.0.0 → Redesigned UI (breaking changes)

---

## Google Play Store Deployment

### 1. Create Play Console Account

- Go to [Google Play Console](https://play.google.com/console)
- Pay one-time $25 registration fee
- Complete account setup

### 2. Create App Listing

**Store Listing**:
- App name: GLCameraApp
- Short description: Real-time camera filters with OpenGL ES
- Full description: (Use README content)
- App icon: 512x512 PNG
- Feature graphic: 1024x500 PNG
- Screenshots: At least 2 (phone + tablet)
- App category: Photography
- Content rating: Everyone

**Screenshots to Include**:
1. Main screen with camera preview
2. Filter selection UI
3. Grayscale filter applied
4. Sepia filter applied
5. Performance metrics overlay

### 3. Content Rating

Complete questionnaire:
- No violence
- No user-generated content
- No social features
- No location sharing
- Camera access for preview only

Expected rating: Everyone

### 4. Pricing & Distribution

- Free app
- Available in all countries
- No ads (currently)
- No in-app purchases

### 5. App Content

**Privacy Policy**:
- Required for camera permission
- Host on GitHub Pages or your website
- Include: What data is collected (none), how it's used, user rights

**Data Safety**:
- No data collected
- No data shared
- Camera access: Required for app functionality

### 6. Upload Release

1. Go to Production → Create new release
2. Upload AAB file
3. Add release notes
4. Review and rollout

**Release Notes Template**:
```
Version 1.0.0
- Real-time camera preview with OpenGL ES
- Three filters: Normal, Grayscale, Sepia
- Performance metrics overlay
- 60 FPS rendering
- Material 3 design
```

### 7. Review Process

- Google review: 1-7 days
- Check for policy violations
- Respond to any issues promptly

---

## Alternative Distribution

### GitHub Releases

```bash
# Create release on GitHub
gh release create v1.0.0 \
  app/build/outputs/apk/release/app-release.apk \
  --title "GLCameraApp v1.0.0" \
  --notes "Initial release"
```

### F-Droid

1. Fork [F-Droid Data](https://gitlab.com/fdroid/fdroiddata)
2. Add metadata file
3. Submit merge request
4. Wait for review

### Direct APK Distribution

Host APK on your website:
- Provide SHA-256 checksum
- Include installation instructions
- Warn about "Unknown sources" setting

---

## Continuous Integration

### GitHub Actions Workflow

Create `.github/workflows/android.yml`:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Build debug APK
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### Automated Release

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Build release APK
      run: ./gradlew assembleRelease
      env:
        KEYSTORE_FILE: ${{ secrets.KEYSTORE_FILE }}
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
    
    - name: Create Release
      uses: softprops/action-gh-release@v1
      with:
        files: app/build/outputs/apk/release/app-release.apk
```

---

## Monitoring & Analytics

### Firebase Crashlytics

Add to `app/build.gradle.kts`:

```kotlin
plugins {
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

### Google Analytics

Track key events:
- App launches
- Filter switches
- Performance metrics
- Errors and crashes

---

## Post-Deployment

### Monitor Metrics

**Play Console Metrics**:
- Crash rate (<1% target)
- ANR rate (<0.5% target)
- Install/uninstall ratio
- User ratings and reviews
- Device compatibility

**Performance Metrics**:
- App startup time
- Frame rendering time
- Memory usage
- Battery consumption

### User Feedback

- Respond to reviews within 24 hours
- Track feature requests
- Monitor crash reports
- Update regularly

### Update Schedule

- Patch releases: As needed (critical bugs)
- Minor releases: Monthly (new features)
- Major releases: Quarterly (significant changes)

---

## Rollback Plan

If critical issues found:

1. **Immediate**: Halt rollout in Play Console
2. **Assess**: Determine severity and impact
3. **Fix**: Create hotfix branch
4. **Test**: Verify fix on affected devices
5. **Deploy**: Release patch version
6. **Communicate**: Update users via release notes

---

## Legal & Compliance

### Open Source License

Current: MIT License (permissive)

Ensure all dependencies are compatible:
- Apache 2.0 ✅
- MIT ✅
- BSD ✅
- GPL ❌ (requires app to be GPL)

### Privacy Compliance

**GDPR** (Europe):
- No personal data collected ✅
- Camera access disclosed ✅
- Privacy policy provided ✅

**CCPA** (California):
- No data selling ✅
- No tracking ✅

**COPPA** (Children):
- No data collection from children ✅
- Age-appropriate content ✅

### Attribution

Include in app:
- Open source licenses (Settings → About → Licenses)
- Third-party libraries
- Icon/asset credits

---

## Troubleshooting

### Build Fails

**Issue**: ProGuard errors
**Solution**: Check ProGuard rules, add keep rules for reflection

**Issue**: Signing fails
**Solution**: Verify keystore path and credentials

**Issue**: Out of memory
**Solution**: Increase Gradle heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

### Upload Fails

**Issue**: Version code conflict
**Solution**: Increment versionCode in build.gradle.kts

**Issue**: Bundle size too large
**Solution**: Enable R8 shrinking, remove unused resources

### Review Rejection

**Issue**: Privacy policy missing
**Solution**: Add privacy policy URL in Play Console

**Issue**: Permissions not justified
**Solution**: Add permission rationale in manifest

---

## Resources

- [Android Developer Guide](https://developer.android.com/distribute)
- [Play Console Help](https://support.google.com/googleplay/android-developer)
- [App Signing Best Practices](https://developer.android.com/studio/publish/app-signing)
- [ProGuard Manual](https://www.guardsquare.com/manual/home)
