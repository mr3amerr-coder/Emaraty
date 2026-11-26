# Build Amaraty APK with GitHub Actions

This repository builds a debug APK through the workflow:

`.github/workflows/android-apk.yml`

## Automatic build

The workflow starts automatically whenever code is pushed to `main` or another branch. You can also start it manually:

1. Open the [Emaraty repository](https://github.com/mr3amerr-coder/Emaraty).
2. Select **Actions**.
3. Select **Build Amaraty APK**.
4. Choose **Run workflow** if you want to start a new build manually.
5. Open the latest run and wait for the build and tests to finish.

## Download the APK

After a successful run:

1. Open the workflow run summary.
2. Scroll down to **Artifacts**.
3. Download `amaraty-debug-apk`.
4. Extract the downloaded ZIP.
5. Install `app-debug.apk` on your Android phone. Android may ask you to allow installation from the app you used to open the file.

Artifacts are retained for 14 days. A new push creates a new build.

## What GitHub runs

- JDK 17
- Gradle 9.9.2
- Android 17 SDK (API 37.1 or newer; the workflow selects the newest available 37.x platform)
- Android SDK Build Tools 37.0.0
- `:app:assembleDebug`
- `:app:testDebugUnitTest`
- `:app:lintDebug`
- `:core:domain:test`

The checked-in `gradlew` is a bootstrap shim rather than the official Gradle Wrapper. The workflow installs Gradle 9.9.2 on the runner first, and the shim delegates to that installation. CI disables Gradle's configuration cache because the repository's custom money-domain verification task currently captures a script object that Gradle cannot serialize into that cache.

## Important limits

- The output is a **debug APK for testing**, not a signed release APK for Play Store publishing.
- This source tree is milestone M1 (foundation), not the complete apartment/building/owners' union application. Several production modules are not implemented yet. See [README.md](README.md) and [TEST_REPORT.md](TEST_REPORT.md).
- The first successful GitHub run will be the first actual Android build verification. Do not use this milestone as the building's sole financial record.
