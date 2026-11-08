# Nexora Utility — release blockers (2026-11-08)

This is an evidence-based status note. Do not mark the app release-ready until the checks below pass.

## Verified repository state
- Latest main commit inspected: 802407381bbf12a51d5664077b25852671e5de3e.
- Latest Android Actions run inspected: run 11, conclusion `failure`.
- Job log confirms failure at `Verify native Android project is present`, before JDK, Android SDK, Gradle, Kotlin compilation, lint, tests, or APK packaging.
- The current main branch does not contain `settings.gradle.kts`; the native source archive is not present in the repository.
- A 77-entry source ZIP is available outside the repository; it contains 39 Kotlin files and Gradle project files. It must be committed at repository root as `nexora-android-source.zip` (or the extracted project committed directly) to let CI proceed.

## Source-level blockers found during archive inspection
1. **VPN:** the main VPN service intentionally reports “not configured” and refuses to connect without a provider-issued profile. This is honest behavior, not a functioning VPN offering. The separate WireGuard manager contains WARP endpoint constants and comments referring to a development template key; do not expose a connected state or ship a shared/test private key. A real user-provided or properly provisioned provider configuration flow and end-to-end tunnel tests are required.
2. **App Lock:** PIN salt/hash, failed-attempt state, and locked package collection are static in-memory values. They do not survive process death/reboot. The current package list also starts with hard-coded protected apps. Persist settings securely and let users explicitly select packages before advertising App Lock as durable.
3. **Vault:** encrypted payloads use AES-GCM with an Android Keystore key, but `listVaultItems()` reconstructs generic filename/MIME metadata from ciphertext filenames. A durable authenticated metadata index is needed; verify restore, rename, delete, and key-loss behavior.
4. **Cleaner/storage:** Android cannot silently clear another app's private cache. Keep cleanup limited to files the app can access and use explicit user confirmation for deletion.
5. **Release validation:** no successful Kotlin compilation, lint, unit tests, APK artifact, install test, or physical-device smoke test is verified yet.

## Required next steps
- Import the native source archive into `main`.
- Run `:app:compileDebugKotlin`, `:app:lintDebug`, `:app:testDebugUnitTest`, `:app:assembleDebug`, and `:app:assembleRelease`.
- Fix all build/lint/test failures, then test Android 8+ behavior and current Android versions.
- Complete App Lock and Vault persistence/security work.
- Keep VPN explicitly unavailable until a real configuration and handshake are verified.
- Generate a signed release only with a user-controlled signing key; never commit keystore material or passwords.
- Perform installation and permission-revocation tests on real devices before calling the app ready.

## Product promise
Nexora Utility remains free: no premium tier, subscription, or paid unlock. Never fake scan totals, cleanup savings, device-performance improvements, or VPN connectivity.
