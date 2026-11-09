# Nexora Utility

Native Android utility app built with Kotlin and Jetpack Compose.

## Current implementation
- Dark black / emerald-green interface with Home, Clean, Storage, Vault, VPN and Tools sections.
- Safe cache estimate and explicit clearing of Nexora Utility's own cache only.
- Internal shared data-partition usage and available-space display using Android filesystem statistics.
- User-selected large-file review, explicit delete confirmation and bounded exact SHA-256 duplicate grouping.
- Encrypted private vault using Android Keystore AES-256-GCM with import/export; separate PIN/biometric gate is still pending.
- Basic device diagnostics for Android version, available RAM, battery percentage and thermal status.
- Built-in WireGuard client for user-provided WireGuard server profiles.
- Live VPN Gate country/server directory with encrypted OpenVPN profile storage. Nexora can request connect/disconnect and display status through the documented AIDL API of the separately installed OpenVPN for Android app; that companion app supplies the OpenVPN tunnel engine. VPN Gate relays are volunteer-operated and availability changes.
- Shortcuts to Android system settings for app, network, display, battery and sound controls.
- Minimal manifest and least-privilege baseline; no broad storage, overlay or accessibility permissions are requested. Network access is used for the VPN Gate directory and VPN client integrations.

## Product scope and release gates
Planned next modules: broader media analysis, separate vault PIN/biometric gate, compliant app-management tools, game diagnostics and release QA. WireGuard requires a valid user profile and reachable WireGuard server. VPN Gate OpenVPN connections require the separate OpenVPN for Android companion app; Nexora does not operate its own VPN server fleet. VPN Gate server availability, throughput and privacy are not guaranteed.

Features are not described as active until implemented and tested. Android does not allow an ordinary third-party app to silently clear other apps' caches, hide arbitrary apps, or boost CPU/GPU performance. Nexora will use supported APIs and disclose these limitations.

## Monetization
Free to use. The only permitted advertising placement is one anchored adaptive banner at the bottom. No subscriptions, premium tier, paid unlocks, credits, interstitials, rewarded ads, app-open ads, pop-ups or full-screen ads. Live ads remain disabled until real AdMob identifiers, consent, privacy disclosures and QA are complete. See [ad requirements](docs/ADS_AND_MONETIZATION_REQUIREMENTS.md).

## Build
- Application ID: `com.nexora.utility`
- Minimum Android: API 26
- Compile / target SDK: 35
- JDK: 17
- Gradle: 8.9 (CI generates the wrapper if absent)

The `.github/workflows/android-ci.yml` GitHub Actions workflow compiles Kotlin, runs Android lint and unit tests, and assembles debug and unsigned release APK artifacts. A successful CI run and physical-device QA are required before calling this release-ready.
