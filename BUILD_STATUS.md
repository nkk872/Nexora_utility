# Nexora Utility — implementation and verification status

Last checked: 2026-11-08.

## Verified repository and CI state
- Latest checked CI run: run 11, ID `42462859689`, failed at `Verify native Android project is present`.
- That run did not reach JDK/SDK setup, Gradle, Kotlin compilation, lint, tests, or APK assembly.
- The complete native Kotlin project is prepared as a local source archive, but the repository still does not contain the full project tree. The current root has only a minimal `app/` placeholder structure; do not interpret that as a successful native import.
- Latest local source archive: 89 entries, 39 Kotlin source files; ZIP integrity check passed.
- The current archive contains the Gradle project, app source/resources, CI workflow, import instructions, README, and audit notes. It must be uploaded/extracted into the repository root before CI can compile it.

## Source work completed
- App Lock: user-selected package list and salted PBKDF2 PIN verifier persisted in app-private preferences; failed-attempt lockout persisted; app restart requires fresh PIN when enabled; background/foreground timeout handling added. Biometric unlock remains intentionally disabled.
- Vault: AES-GCM encrypted payload and encrypted metadata sidecars; PIN authorization gate before protected listing; unique temporary import files; selected document display name preserved where available; authenticated temporary export before publishing; deletion preserves metadata if ciphertext deletion fails.
- VPN: refuses to claim protection while no genuine provider profile and verified packet routing exist. No private key is embedded.
- Privacy UI: fabricated privacy score and permission totals removed.
- Cleaner and App Manager remain constrained by Android's scoped storage and package-management APIs.

## Required verification and outstanding work
1. Import the full native project into repository root.
2. Run `:app:compileDebugKotlin`, `:app:lintDebug`, `:app:testDebugUnitTest`, `:app:assembleDebug`, and `:app:assembleRelease`.
3. Resolve every build/lint/test failure from actual CI logs.
4. Enforce App Lock authorization consistently for every route and data operation; verify accessibility and overlay behavior on supported devices and review Play policy eligibility.
5. Complete Vault tamper, key-loss, reinstall, backup/restore, partial-write, large-file, and recovery tests.
6. Configure a genuine WireGuard provider profile and verify handshake plus actual routed traffic, or keep VPN unavailable.
7. Review permissions/foreground service declarations; test permission denial/revocation, cleanup confirmations, upgrades, and Android 8+ plus current Android devices.
8. Produce a signed release only with a user-controlled signing key stored outside Git.

## Environment limitations
- No Android SDK is configured in the current working environment (`ANDROID_HOME` is unset); no Gradle executable or wrapper is available locally.
- No Android compilation, lint, unit test, emulator test, physical-device test, or APK build has been run here.
- ZIP integrity is not equivalent to a successful Android build.

## Product promise
Nexora Utility remains free: no premium tier, subscription, credits, or paid unlocks. Do not fabricate scan totals, cleanup savings, device-performance improvements, or VPN connectivity.

## Release status
**Not release-ready.** Awaiting complete source import, successful CI, security tests, and physical-device QA.
