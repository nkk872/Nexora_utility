# Nexora Utility — implementation and verification status

Last checked: 2026-11-08.

## Verified repository and CI state
- Latest inspected main commit before this documentation update: `802407381bbf12a51d5664077b25852671e5de3e`.
- Latest Android CI run inspected: run 11, ID `42462859689`, conclusion `failure`.
- The job failed at `Verify native Android project is present`. JDK setup, Android SDK setup, Gradle, Kotlin compilation, lint, tests and APK assembly were all skipped.
- The repository does not contain the Gradle project files or Kotlin source tree yet.
- The native source archive is available in the working environment: 77 ZIP entries, 39 Kotlin files, about 415 KB uncompressed source. It must be added to the repository root as `nexora-android-source.zip`, or the extracted source tree committed directly.

## Work completed
- Existing CI workflow checks for the real native project and runs compile, lint, tests, debug/release APK assembly after import.
- Free-access and privacy policy documentation added.
- Source-level release blockers documented in `docs/RELEASE_BLOCKERS.md`.
- Existing source candidate inspected; it is a Kotlin/Jetpack Compose Android project, not merely the React preview.

## Source-level blockers
- **VPN:** no verified provider-issued profile or real tunnel yet. The main service correctly refuses connection while unconfigured. Do not advertise VPN as working until a real handshake and routed traffic are verified.
- **App Lock:** PIN salt/hash and protected package list are held in static in-memory values; they do not persist across process death/reboot. Replace hard-coded initial package selection with explicit user choices and secure persistence.
- **Vault:** AES-GCM and Android Keystore are used for payloads, but original filename/MIME metadata is reconstructed generically when listing items. Add durable authenticated metadata and test recovery/key-loss behavior.
- **Cleaner:** only clean files the app is allowed to access; Android does not permit silently clearing other apps' private caches.
- **Permissions/Play policy:** review broad storage/overlay/usage access permissions and request only what each user-activated feature genuinely needs.

## Required verification
1. Import the native project into `main`.
2. Run `:app:compileDebugKotlin`, `:app:lintDebug`, `:app:testDebugUnitTest`, `:app:assembleDebug`, and `:app:assembleRelease`.
3. Resolve build, lint and test failures.
4. Complete App Lock and Vault persistence/security work; keep VPN unavailable until real configuration and end-to-end verification.
5. Test permission denial/revocation, destructive-action confirmation, install/upgrade, and Android 8+ plus current Android versions on physical devices.
6. Sign release builds only with a user-controlled signing key. Never commit keystore files or passwords.

## Product promise
Nexora Utility remains free for everyone: no premium tier, subscription, or paid unlock. Never fabricate scan totals, cleanup savings, device-performance improvements, or VPN connectivity.

## Release status
**Not release-ready.** No successful Android compilation, passing test suite, installable APK, or physical-device QA is verified yet.
