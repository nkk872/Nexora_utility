# Nexora Utility — implementation and verification status

Last updated: 2026-11-09.

## Repository changes
- Repository: `nkk872/Nexora_utility`, branch `main`.
- Added Gradle settings, root build configuration, version catalog and app module configuration.
- Added a Kotlin + Jetpack Compose application entry point.
- Replaced the previous manifest, which referenced missing resources and service classes, with a minimal launcher manifest.
- The Android CI workflow is configured to compile Kotlin, run Android lint/tests and assemble a debug APK when the project files change.

## Implemented in source (not yet build-verified)
- Home dashboard and black/emerald-green styling.
- Nexora-owned cache size estimate, refresh and explicit cache clearing.
- Internal data-partition total/used/available storage figures.
- Launchers for Android app, network, display, battery and sound settings.
- Least-privilege manifest: no broad file access, overlay, accessibility, VPN or package visibility permissions are enabled by this starter implementation.

## Not yet implemented
- User-selected shared-storage file scan and deletion review.
- Large-file and duplicate-photo detection.
- Encrypted private vault and biometric/PIN access.
- App lock / app hiding; Android and Google Play restrictions must be respected.
- Game tools and device diagnostics beyond system settings shortcuts.
- VPN client. No VPN connection may be claimed until a provider profile is configured and a real handshake and routed traffic are verified.
- AdMob and UMP. No ad IDs or live advertising have been configured.

## Verification
- No successful Android build, lint result, test result, APK artifact, or physical-device QA result has yet been confirmed after these commits.
- CI should run automatically on the main-branch file changes. Check the latest GitHub Actions run before proceeding to release packaging.

## Product and monetization rules
- Free app; no subscriptions, premium tier, credits, paid unlocks or in-app purchases.
- At most one anchored adaptive banner at the bottom of the app.
- No interstitial, rewarded, app-open, native/in-feed, top, pop-up, notification or full-screen ads.
- Never delete personal files without explicit selection and confirmation.
- Never claim fabricated cache savings, RAM gains, battery health, VPN status or performance boosts.
