# عمارتي | Amaraty

Arabic-first, offline-first Android project for apartment records, shared-building administration, owners' union finance, and household bills in Egypt.

> **Current delivery: Foundation milestone (M1), not a complete building-management release.** The repository contains a real Android source project, an independently compiled pure-Kotlin money/allocation layer, a first Room schema, local settings, a small Compose shell, and tests. Bills, union ledger, charge runs, meetings, elevator workflows, document vault, encrypted backup, and PDF/CSV exports are not implemented yet.

## What is implemented now

- Kotlin + Jetpack Compose Material 3, single Activity, typed Navigation Compose routes, Hilt, StateFlow and coroutines.
- Arabic-first UI, RTL layout, Arabic/English resources, light/dark themes, user choice of Arabic digits.
- Offline design: no backend, login, cloud service, ads, analytics or tracking. The app manifest contains a merger rule that strips any `INTERNET` permission contributed by a dependency; a device-side test checks the final merged package.
- Local preferences using DataStore; optional biometric/device-credential lock where Android supports the requested authenticator combination; optional `FLAG_SECURE`.
- Pure-Kotlin `Money` stored as integer piasters (`Long`), exact parser/formatter, localized Arabic digit/separator output, overflow validation, and largest-remainder charge allocator using `BigInteger` intermediate arithmetic.
- Room v1 with Property, Unit and AuditLog entities, foreign key and unique index. The DAO does not expose update/delete methods for audit entries.
- Unit tests for parser and allocator; Room instrumentation tests for foreign-key/unique-index behaviour and audit append/read access.
- Debug-only LeakCanary and StrictMode.

## Not implemented yet

The complete product scope remains larger than this foundation. In particular, this delivery does **not** contain the actual household bill manager, owner/tenant/party records, fund accounts, financial ledger and receipts, monthly charge runs, meeting/quorum workflows, maintenance/elevator logs, reminder workers, encrypted document storage, encrypted backup/restore, or printable reports. Do not use this build as the live financial record of a building.

## Requirements

- JDK 17 or newer (JDK 17 is the project toolchain target).
- Android SDK Platform 37 and Android Build Tools installed.
- Gradle 9.9.2.
- Android Studio with support for the selected Android Gradle Plugin.

The environment used to prepare this source archive had no Gradle installation, Android SDK, or network access. Consequently, the official Gradle Wrapper JAR could not be generated or bundled. `gradlew` and `gradlew.bat` are **bootstrap shims**, not the official Gradle Wrapper. They run a locally installed `gradle` command and fail clearly when it is absent.

## Open and build

1. Install JDK 17, Android Studio, Android SDK Platform 37, and Gradle 9.9.2.
2. Open this folder in Android Studio and allow it to sync using the installed Gradle, or generate the official wrapper from this project root:

   ```bash
   gradle wrapper --gradle-version 9.9.2 --distribution-type bin
   ```

3. Run the project gates:

   ```bash
   ./gradlew assembleDebug testDebugUnitTest lintDebug
   ./gradlew :core:data:connectedDebugAndroidTest
   ```

4. Connect an Android device/emulator for instrumentation tests. The Room instrumentation tests need a running API 26+ device/emulator.

The requested release gate is `assembleDebug`, `testDebugUnitTest`, and `lintDebug`. It has **not** been completed in the preparation environment; see `TEST_REPORT.md`.

## Project map

```text
app/                 Compose UI, navigation, activity, settings ViewModel
core/domain/         Pure Kotlin money parsing/formatting/allocation
core/data/           Room v1, DataStore, dependency injection
config/              Detekt configuration
scripts/              Local pure-domain verification
ARCHITECTURE.md       Architecture, schema and dependency rationale
DECISIONS.md          Assumptions and unresolved choices
TEST_REPORT.md        Exact verification status
```

## Package identifier

The initial `applicationId` / namespace is `com.zero.amaraty`. Replace it with a reverse-domain identifier controlled by the publisher before any Play Store release.

## Privacy and safety boundary

- The manifest merger is instructed to remove any `INTERNET` permission, including one contributed by an indirect dependency; the final package check is included as an instrumentation test and has not yet run.
- Android backup is disabled for the app until an explicit encrypted export/restore flow is implemented and reviewed.
- The app must never publish residents' debt lists, encourage service cut-offs, store national ID/card/bank-account numbers, or expose personal contact details in group reports.
- Configurable meeting thresholds and allocation rules are tracking settings, not legal advice. Users must check the building's bylaws and obtain professional advice when needed.
- Elevator, gas, electrical and structural safety decisions require the appropriate qualified professional.
