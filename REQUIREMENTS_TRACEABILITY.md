# Requirements traceability

Status legend: **M1 implemented** = present in this foundation source; **Partial** = only the described foundation piece exists; **Not started** = explicitly out of the current delivery. A row marked M1 implemented still requires the Android Gradle gate and device tests before release.

| Requirement | Planned area / screen | Test or evidence | Status |
|---|---|---|---|
| Arabic-first app and RTL | `:app`, all Compose screens | Arabic default strings, RTL provider, resource XML parse | Partial: no bundled OFL Arabic font yet |
| English locale fallback | `values-en` resources | Resource file exists and parses | M1 implemented |
| Multiple property switcher | Home / property context | Property schema only | Not started |
| Property record | `PropertyEntity` / future property form | Room property test source | Partial: persistence entity/DAO only, no editor screen |
| Unit list and property ownership link | `UnitEntity` | Foreign-key and unique-index instrumentation test source | Partial: entity/DAO only |
| Party roles (owner/tenant/proxy/contact) | Future parties module | Not implemented | Not started |
| Do not store national ID, card or bank account numbers | Data model / vault | Data model review and privacy tests in later milestones | Partial: not collected by M1; full schema review pending |
| Exact money storage | `core:domain` `Money` | Parser tests + standalone smoke | M1 implemented in domain; Gradle/JUnit gate pending |
| Parse Arabic and Western digits | Money input | Seven valid + six invalid smoke cases | M1 implemented in domain |
| Reject ambiguous separator input | Money input | `MoneyParserTest` and smoke | M1 implemented in domain |
| Never use Float/Double in money/allocation | `core:domain` build check | `verifyNoFloatingPointMoney` Gradle task + static scan | M1 implemented; task awaits Gradle execution |
| Largest-remainder allocation | `core:domain` allocator | Three golden vectors + 4,008 generated cases | M1 implemented in domain |
| Store the allocation rule snapshot for historic charge runs | Union charge module | Future database schema/tests | Not started |
| Append-only financial ledger with reversal entries | Union ledger | Future DAO and domain tests | Not started; current append-only API is audit log only |
| Sequential receipts and voided receipt numbering | Union receipts | Future transaction tests | Not started |
| Apply partial payments to oldest due first | Union collection | Future domain tests | Not started |
| Funds, transfers, approvals and reconciliation | Union finance | Future transaction/reconciliation tests | Not started |
| Household bills and recurring templates | Resident / bills | Future DAO, scheduler and UI tests | Not started |
| Meter reading validation | Resident / meters | Future domain tests | Not started |
| Contracts, warranties and expiry reminders | Resident / contracts | Future DAO and WorkManager tests | Not started |
| Light finishing project, retention and snag list | Resident / finishing | Future milestone/ledger tests | Not started |
| Dashboard priority ordering | Home / attention list | Future prioritization test | Not started |
| Meetings, invitations, attendance, proxies and resolutions | Union / meetings | Future rule tests | Not started |
| Editable quorum and majority rules with bylaws warning | Meetings settings | Future configurable rule tests | Not started |
| Elevator maintenance / safety log | Maintenance / elevator | Future safety workflow tests | Not started |
| Emergency action screen / hotlines | Safety screen | Current numbers must be reverified from official sources before release | Not started |
| Maintenance tickets | Maintenance | Future DAO, UI and reminder tests | Not started |
| Private follow-up and message preparation only | Union follow-up | Future privacy tests; no auto-send integration | Not started |
| No public arrears/debt lists or utility-cutoff punishment | Reports / collection UI | Future export/privacy tests | Policy boundary documented; UI tests pending |
| Documents encrypted at rest | Document vault | Key-management and encryption tests | Not started |
| Encrypted backup and atomic restore | Backup/restore | Corruption, wrong-password and restore transaction tests | Not started; Android backup currently disabled |
| Offline-only; no account/cloud or Internet permission | App/manifest | Manifest merger privacy instrumentation test source | Partial: source has removal directive; final merged manifest test not run |
| No automatic bill fetch / payment gateway / OCR / automatic messages | Scope control | Dependency and permission review | M1 implemented: these features are absent |
| Biometric/device credential app lock | Activity security | API-level device tests required | Partial: implemented in source; not device-tested |
| Secure screen flag | Settings | Manual screenshot/recents test required | Partial: implemented in source; not device-tested |
| WorkManager reminders | Data layer | Future worker tests | Not started; dependency reserved |
| Explicit database migrations, no destructive migration | Room | Migration test on every future schema bump | Partial: v1 only, no migration yet |
| Debug LeakCanary and StrictMode | Application debug config | Build and debug-device tests | Partial: configured, not executed |
| detekt, ktlint and static money guard | Build | Gradle lint/check gates | Partial: configured, not run |
| PDF/CSV export and aggregation-only group reports | Reports | Future format/RTL/privacy tests | Not started |
