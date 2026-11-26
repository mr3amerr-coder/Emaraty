# Test and build report

**Prepared:** 26 November 2026  
**Status:** M1 foundation is partially verified. No Android build or APK is claimed as successful.

## Environment

| Tool | Observed result |
|---|---|
| Java | OpenJDK 21.0.12.1 |
| Kotlin CLI | `kotlinc-jvm 1.9.0` |
| Gradle | Not installed (`command -v gradle` returned no executable) |
| Android SDK | `ANDROID_HOME` and `ANDROID_SDK_ROOT` unset; no SDK found in the checked standard locations |
| Network | DNS/network access unavailable during preparation; Gradle wrapper distribution/JAR could not be retrieved |

## Executed successfully

The pure Kotlin domain sources were compiled and exercised using the checked-in standalone script:

```bash
./scripts/verify-domain.sh
```

Observed output:

```text
DOMAIN_SMOKE_OK: 7 valid inputs, 6 invalid inputs, 2 formats, 3 golden vectors, 4008 generated allocation cases
```

The script compiles the pure domain sources with the locally installed Kotlin CLI and runs exact parser/formatter cases, all three unambiguous allocation vectors, and 4,008 generated allocation cases. This verifies those domain sources against Kotlin 1.9.0 only. It is not a substitute for the project's configured Kotlin 2.4.21/Gradle build or the JUnit test task.

Additional local static checks completed:

```text
XML_WELL_FORMED_OK: 8 files
MANIFEST_PRIVACY_SOURCE_OK: INTERNET is a manifest-merger removal directive, not an active request
MONEY_STATIC_SCAN_OK: no Float/Double tokens in money/allocation packages
```

## Not executed / not passed

The required Android gate:

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

could not run because Gradle and the Android SDK are absent. The exact result was `EXIT_CODE=127`. The supplied `gradlew` is a bootstrap shim and prints that Gradle is not installed; it is not the official Gradle Wrapper.

The following also remain unexecuted:

- Gradle dependency resolution / Android Studio sync.
- `assembleDebug` and APK generation.
- JVM JUnit tests through Gradle.
- Android Room instrumentation tests on API 26+ emulator/device.
- `lintDebug`, Detekt and Spotless/ktlint.
- Release/R8 build, accessibility checks, RTL device review, app-lock device testing and backup/restore testing.

## Current result

- Pure Kotlin money/allocation compile: **passed with local CLI**.
- Manual domain smoke run: **passed**.
- Android project build: **not verified**.
- Android/Room tests: **not run**.
- APK: **not generated**.

Do not treat M1 as a production-ready financial ledger or use it as the building's sole record. Complete the Android green gate and subsequent milestones before operational use.
