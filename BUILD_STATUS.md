# Nexora Utility — implementation and verification status

Last updated: 2026-11-09.

## Repository changes
- Repository: `nkk872/Nexora_utility`, branch `main`.
- Added Gradle settings, root build configuration, version catalog and app module configuration.
- Added Kotlin + Jetpack Compose app entry point and dark emerald interface.
- Replaced the old manifest, which referenced missing resources and services, with a minimal launcher manifest.
- Added AndroidX DocumentFile and a user-selected folder scan through Android's Storage Access Framework.

## Implemented in source (not yet build-verified)
- Home dashboard and navigation: Home, Clean, Storage and Tools.
- Nexora-owned cache estimate, refresh and explicit cache clearing.
- Internal data-partition total/used/available storage figures.
- Folder picker to scan only the directory explicitly selected by the user.
- Large-file review (files at least 20 MB; scan capped at 5,000 entries and display capped at 100).
- Per-file selection and a second confirmation before deletion.
- Launchers for Android app, network, display, battery and sound settings.
- Least-privilege manifest: no broad file access, overlay, accessibility, VPN or package visibility permissions are enabled.

## Not yet implemented
- Duplicate-photo detection and a full category-based media browser.
- Encrypted private vault and biometric/PIN access.
- App lock / app hiding; Android and Google Play restrictions must be respected.
- Game tools and device diagnostics beyond system settings shortcuts.
- VPN client. No VPN connection may be claimed until a provider profile is configured and a real handshake and routed traffic are verified.
- AdMob and UMP. No ad IDs or live advertising have been configured.

## Verification
- No successful Android build, lint result, test result, APK artifact, or physical-device QA result has yet been confirmed after these commits.
- GitHub Actions is configured to compile Kotlin, run lint/tests and assemble debug and release APKs on project changes. The latest result must be checked before any APK is called build-ready.

## Product and monetization rules
- Free app; no subscriptions, premium tier, credits, paid unlocks or in-app purchases.
- At most one anchored adaptive banner at the bottom of the app.
- No interstitial, rewarded, app-open, native/in-feed, top, pop-up, notification or full-screen ads.
- Never delete personal files without explicit selection and confirmation.
- Never claim fabricated cache savings, RAM gains, battery health, VPN status or performance boosts.
