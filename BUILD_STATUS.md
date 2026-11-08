# Nexora Utility — implementation and verification status

## Completed in GitHub
- Free-for-everyone access policy: docs/ACCESS_POLICY.md.
- Android CI workflow: .github/workflows/android-ci.yml.
- CI configured for JDK 17, Android SDK 35, Gradle 8.9, Kotlin compilation, Android lint, unit tests, debug APK, release APK, and artifact upload.
- CI checks required native project files before compilation.

## Pending source integration
The full source candidate is currently a ZIP in the working session. The repository must contain extracted project files at its root, including settings.gradle.kts, build.gradle.kts, gradle/libs.versions.toml, app/build.gradle.kts, and app/src/main/**. The workflow deliberately fails early if these are missing.

## Pending verification
- Import/reconcile the complete source tree with existing GitHub commits.
- Run CI and fix compiler, dependency, lint, and test failures.
- Test permission denial/revocation, scoped media access, vault encryption/recovery, app-lock consent/disable, and destructive-action confirmation on devices.
- Verify VPN tunnel establishment and routed traffic using a real provider configuration; otherwise keep VPN unavailable/disconnected.
- Verify release signing, APK installation/upgrade, and Play disclosures.

## Truthful status
No successful Android compilation or installable APK is claimed until a GitHub Actions run succeeds and a device smoke test is recorded. An artifact built without signing secrets is unsigned and not ready for Play Store distribution.
