# Nexora Utility

Native Android utility app built with Kotlin and Jetpack Compose.

## Current implementation
- Dark black / emerald-green interface with Home, Clean, Storage, Vault and Tools sections.
- Safe cache estimate and explicit clearing of Nexora Utility's own cache only.
- Internal shared data-partition usage and available-space display using Android filesystem statistics.
- User-selected large-file review, explicit delete confirmation and bounded exact SHA-256 duplicate grouping.
- Encrypted private vault using Android Keystore AES-256-GCM with import/export; separate PIN/biometric gate is still pending.
- Basic device diagnostics for Android version, available RAM, battery percentage and thermal status.
- Shortcuts to Android system settings for app, network, display, battery and sound controls.
- Minimal manifest and least-privilege baseline; no broad storage, overlay, accessibility or package-visibility permissions are requested by this foundation.

## Product scope and release gates
Planned next modules: user-selected file analysis, duplicate/large-file review, encrypted private vault, optional app-lock capability subject to Android accessibility and Play policy, app manager using supported system intents, diagnostics, game tools, and VPN integration only with a real provider configuration and verified routing.

Features are not described as active until implemented and tested. Android does not allow an ordinary third-party app to silently clear other apps' caches, hide arbitrary apps, or boost CPU/GPU performance. Nexora will use supported APIs and disclose these limitations.

## Monetization
Free to use. The only permitted advertising placement is one anchored adaptive banner at the bottom. No subscriptions, premium tier, paid unlocks, credits, interstitials, rewarded ads, app-open ads, pop-ups or full-screen ads. Live ads remain disabled until real AdMob identifiers, consent, privacy disclosures and QA are complete. See [ad requirements](docs/ADS_AND_MONETIZATION_REQUIREMENTS.md).

## Build
- Application ID: `com.nexora.utility`
- Minimum Android: API 26
- Compile / target SDK: 35
- JDK: 17
- Gradle: 8.9 (CI generates the wrapper if absent)

GitHub Actions CI is being added to compile Kotlin, run lint and unit tests, and assemble a debug APK. A successful CI run and physical-device QA are required before calling this release-ready.
