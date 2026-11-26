# Architecture and first schema

## Modules

| Module | Responsibility | Boundary |
|---|---|---|
| `:app` | Compose screens, navigation, activity lifecycle, app lock, user settings UI | No direct SQL or business allocation logic |
| `:core:domain` | Money, parsing, formatting and allocation | Pure Kotlin/JVM; no Android imports |
| `:core:data` | Room, DataStore, database DI | Maps durable records to domain APIs as modules grow |

The app uses MVVM with immutable UI state, StateFlow and coroutine-based operations. Navigation routes are Kotlin-serializable typed routes. Repository interfaces will sit between future domain use-cases and persistence adapters before cloud sync is considered; cloud sync is explicitly out of v1.

## Dependency rationale

The version catalog is `gradle/libs.versions.toml`. Versions were selected against the official release/documentation pages during preparation; a real Gradle sync is still required before treating them as a resolved dependency graph.

| Dependency | Reason |
|---|---|
| Android Gradle Plugin 9.5.3 / Gradle 9.9.2 | Android build and dependency task engine |
| Kotlin 2.4.21 / KSP 2.3.14 | Kotlin compiler and compile-time code generation |
| Compose BOM 2026.11.01 + Material 3 | Consistent Compose UI versions and Material components |
| Navigation Compose 2.10.2 + Kotlin serialization | Typed route navigation |
| Room 2.8.5 + KSP | Local relational persistence, schema export, generated DAO code |
| Hilt 2.60.1 + AndroidX Hilt lifecycle Compose 1.4.0 | Dependency injection and Compose ViewModel access |
| Lifecycle 2.11.1 + Coroutines 1.11.0 | Lifecycle-aware state and asynchronous work |
| DataStore 1.3.0 | Local user settings |
| WorkManager 2.12.0 | Reserved dependency for the reminder milestone; workers are not implemented yet |
| Biometric 1.1.0 + Fragment 1.9.1 | BiometricPrompt hosted by FragmentActivity |
| LeakCanary 2.14 | Debug-only memory leak inspection |
| Detekt 2.0.0-alpha.6 + Spotless 8.12.0 / ktlint 1.8.0 | Static analysis and Kotlin formatting. Detekt 2 alpha.6 is the current line tested against Kotlin 2.4.10 / AGP 9.3.1; the exact Kotlin 2.4.21 / AGP 9.5.3 combination still needs Gradle validation |
| JUnit 4.13.2 / AndroidX Test 1.3.0 and 1.7.0 | JVM and device-side tests |

AGP 9's built-in Kotlin integration is disabled in `gradle.properties` so the project can pin the standalone Kotlin plugin consistently with the Compose compiler plugin and KSP. This compatibility switch must be confirmed by the first Gradle sync; it has not been verified in this environment.

## Room schema v1

| Entity | Fields / invariants |
|---|---|
| `PropertyEntity` | ID, display name, property type, governorate/city/address, floors, elevator/garage/shops flags, created/updated timestamps |
| `UnitEntity` | ID, property FK, number, floor, unit type, optional area in integer hundredths of m², positive allocation weight, occupancy status, factual notes, timestamps |
| `AuditLogEntity` | ID, entity type/ID, action, event time, non-sensitive summary; DAO exposes insert and read only |

Constraints:

- A unit must reference an existing property (`ON DELETE RESTRICT`).
- `(propertyId, unitNumber)` is unique; `propertyId` is indexed.
- The database schema is exported to `core/data/schemas` when Room's KSP task runs.
- Database version is 1; no migration exists yet. Every future schema bump must add an explicit Migration and a test. Destructive fallback is not configured.

This is only the first slice of the domain model, not the full database promised by the product brief. The next schema additions should be introduced with each corresponding milestone rather than being represented as empty tables.

## Money and allocation invariants

- The durable unit is one Egyptian piastre, represented by `Long`.
- Parsing uses exact decimal-to-integer arithmetic. No binary floating-point type is used in the money or allocation packages.
- Inputs with ambiguous punctuation are rejected rather than guessed. Arabic-Indic and Persian digits are normalized; Arabic decimal/grouping separators are interpreted explicitly.
- Charge allocation uses largest remainder. Intermediate numerator/denominator operations use `BigInteger`; ties resolve by `sortOrder`, then `unitId`, and the returned shares sum exactly to the requested total.
- Ledger records, reversal rules, receipts, partial-payment application and fund reconciliation are not yet implemented. When added, financial entries must be append-only and corrections must be represented by reversal plus replacement entries.

## Android privacy configuration

- Manifest requests the merger to remove any `INTERNET` permission from the final package, even if a transitive dependency declares it; an instrumentation test asserts the installed package has none.
- `allowBackup=false` and `fullBackupContent=false` remain set until reviewed encrypted export/restore exists.
- No account, analytics, advertising, OCR, automatic messaging, or payment-gateway integration is present.
- `FLAG_SECURE` is optional and user controlled.
- Android 11+ uses strong biometric or device credential. Android 8–10 use the supported strong-biometric authenticator path only; the app does not advertise a device-credential fallback on those versions.
