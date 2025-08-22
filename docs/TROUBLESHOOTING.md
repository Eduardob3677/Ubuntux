# Troubleshooting Build Issues

## NumberFormatException: Not a number: 33x

If you encounter this error during build:

```
java.lang.NumberFormatException: Not a number: 33x
    at com.sun.xml.bind.DatatypeConverterImpl._parseInt(DatatypeConverterImpl.java:95)
    at com.android.repository.impl.manager.LocalRepoLoaderImpl.parsePackage(LocalRepoLoaderImpl.java:315)
```

This error occurs during Android SDK repository parsing when there's a malformed version number in the Android SDK metadata.

### Root Cause
The error happens when the Android SDK manager encounters corrupted or incomplete SDK package metadata containing "33x" instead of a valid version number like "33.0" or "33-ext4".

### Solutions

1. **Clear Android SDK Cache:**
   ```bash
   rm -rf ~/.android/cache
   rm -rf ~/.gradle/caches
   ```

2. **Update Android SDK:**
   ```bash
   sdkmanager --update
   ```

3. **Reinstall Android SDK Components:**
   ```bash
   sdkmanager --uninstall "platforms;android-33"
   sdkmanager "platforms;android-33"
   ```

4. **Check SDK Manager Repository:**
   - Open Android Studio
   - Go to Tools > SDK Manager
   - Refresh the repository and reinstall any corrupted packages

5. **Environment Variables:**
   Ensure your `ANDROID_HOME` is correctly set:
   ```bash
   export ANDROID_HOME=/path/to/android/sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

### Prevention
This issue typically occurs due to:
- Interrupted SDK downloads
- Network connectivity issues during SDK updates
- Corrupted SDK installation
- Mismatched SDK versions

Regular SDK maintenance and using stable internet connections can prevent this issue.