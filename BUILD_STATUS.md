# Nexora Utility — implementation and verification status

Last checked: 2026-11-08.

## Verified repository and CI state
- Repository: `nkk872/Nexora_utility`, branch `main`.
- Latest checked CI run: run 11, ID `42462859689`, failed at `Verify native Android project is present`.
- The job restored no `nexora-android-source.zip`; it then failed because `settings.gradle.kts`, root `build.gradle.kts`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, and `app/src/main/java/com/nexora/utility/MainActivity.kt` are absent.
- CI did not reach JDK/SDK setup, Gradle, Kotlin compilation, lint, unit tests, or APK assembly.
- Current tracked tree contains a manifest and launcher drawable under `app/src/main`, plus docs/workflow. It does not contain the complete Kotlin/Gradle project.
- The current manifest requests broad permissions including `MANAGE_EXTERNAL_STORAGE` and `SYSTEM_ALERT_WINDOW`; these require feature-by-feature justification and Play policy review before release.

## Product and monetization decision
- Nexora Utility is free to download and use; revenue comes from ads only.
- No premium tier, subscription, credits, paid unlocks, or in-app purchases.
- Only one anchored adaptive banner fixed at the bottom of the app is permitted.
- No interstitial, rewarded, app-open, native/in-feed, top, pop-up, notification, or full-screen ads.
- Ads must not cover controls or block features; app operation must remain complete when ads fail to load.
- Google Mobile Ads/UMP implementation, real AdMob IDs, privacy disclosures and consent are **not yet integrated or verified**.
- Requirements are recorded in `docs/ADS_AND_MONETIZATION_REQUIREMENTS.md`.

## Source and feature status
- Complete native Kotlin/Compose source is not present in the current GitHub tree, so previous local source-patch descriptions cannot be treated as merged repository code.
- App Lock, Vault, Cleaner, App Manager, diagnostics and game tools are not build-verified from the current repository.
- VPN must remain unavailable / clearly marked unconfigured until a real provider profile, successful WireGuard handshake, and routed traffic tests are verified.
- No release APK has been built or tested.

## Required release gates
1. Import the complete native project at repository root, including Gradle settings/build files, wrapper/version catalog, Kotlin source, resources and tests.
2. Run Kotlin compilation, Android lint, unit tests, debug APK and release APK tasks; fix all actual failures.
3. Review manifest permissions and remove broad permissions that are not essential; document remaining policy justifications.
4. Verify App Lock authorization across all protected routes and data operations; test permission revocation, reboot, process death and timeout.
5. Verify Vault encryption, authenticated metadata, tamper rejection, key loss/reinstall behavior, backup exclusion, large files and partial writes.
6. Verify Cleaner never deletes without user review/confirmation; validate scoped-storage behavior.
7. Verify App Manager uses only supported Android flows; do not claim silent uninstall, arbitrary hiding or force-stop.
8. Keep VPN unavailable unless a real profile can establish a handshake and route traffic; never display a fake connected state.
9. Integrate a single bottom anchored adaptive banner using test ads; verify no overlap and no other placements. Add UMP consent, privacy policy and accurate Play Data safety declarations before live ads.
10. Run emulator/device smoke tests on Android 8+ and current Android, including denied/revoked permissions, offline state, rotation and app upgrades.
11. Create a signed release using a developer-controlled signing key stored outside Git; verify APK/AAB and Play Console declarations.

## Environment and evidence limitations
- No Android SDK, Gradle wrapper or complete native source is available in the current execution environment.
- No Android compilation, lint, unit test, emulator test, physical-device test, or APK build has been run.
- Documentation updates do not close implementation or QA gates.

## Release status
**NOT RELEASE-READY.** The immediate blocker is importing the complete Kotlin/Gradle project into this repository. Do not claim build success, working ads, fully secure App Lock/Vault, functional VPN, or a release APK until verified by CI and device testing.
