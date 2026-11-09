# Nexora Utility

Native Android utility app built with Kotlin and Jetpack Compose.

## Current implementation
- Dark black / emerald-green interface with Home, Clean, Storage, Vault, VPN and Tools sections.
- Safe cache estimate and explicit clearing of Nexora Utility's own cache only.
- Internal shared data-partition usage and available-space display using Android filesystem statistics.
- User-selected photo/video and large-file review (up to 500 displayed matches), explicit delete confirmation and bounded exact SHA-256 duplicate grouping.
- Encrypted private vault using Android Keystore AES-256-GCM with import/export; vault access now requires Android strong biometric or device screen-lock authentication.
- Basic device diagnostics for Android version, available RAM, battery percentage and thermal status.
- Built-in WireGuard client for user-provided WireGuard server profiles.
- Live VPN Gate country/server directory with encrypted OpenVPN profile storage. Nexora can request connect/disconnect and display status through the documented AIDL API of the separately installed OpenVPN for Android app; that companion app supplies the OpenVPN tunnel engine. VPN Gate relays are volunteer-operated and availability changes.
- Shortcuts to Android system settings for app management, manufacturer game settings when available, network, display, battery and sound controls.
- Minimal manifest and least-privilege baseline; no broad storage, overlay or accessibility permissions are requested. Network access is used for the VPN Gate directory and VPN client integrations.

## Product scope and release gates
Remaining release gates: device testing of biometric vault unlock and VPN tunnel routing; broader opt-in media review; supported app-management shortcuts; game-focused device diagnostics; AdMob configuration and consent; signing and Play Console review. WireGuard requires a valid user profile and reachable WireGuard server. VPN Gate OpenVPN connections require the separate OpenVPN for Android companion app; Nexora does not operate its own VPN server fleet. VPN Gate server availability, throughput and privacy are not guaranteed.

Features are not described as active until implemented and tested. Android does not allow an ordinary third-party app to silently clear other apps' caches, hide arbitrary apps, or boost CPU/GPU performance. Nexora will use supported APIs and disclose these limitations.

## Monetization
Free to use. The only permitted advertising placement is one anchored adaptive banner at the bottom. No subscriptions, premium tier, paid unlocks, credits, interstitials, rewarded ads, app-open ads, pop-ups or full-screen ads. Live ads remain disabled until the owner supplies real AdMob App ID and banner unit ID, consent/privacy disclosures are finalized, and QA is complete. See [ad requirements](docs/ADS_AND_MONETIZATION_REQUIREMENTS.md) and the [current privacy policy](docs/PRIVACY_POLICY.md). The policy is committed in the repository but still needs a public hosted URL before Play submission.

## Build
- Application ID: `com.nexora.utility`
- Minimum Android: API 26
- Compile / target SDK: 35
- JDK: 17
- Gradle: 8.9 (installed directly by CI; no wrapper is committed)

The `.github/workflows/android-build.yml` GitHub Actions workflow runs Android lint and unit-test tasks, then assembles debug and release APK artifacts. Release signing and production AdMob IDs are supplied only through the documented GitHub Actions secrets; without a signing keystore, the release APK is unsigned. The repository currently has no Gradle wrapper, so CI installs Gradle 8.9 directly. A successful CI run, physical-device QA, a signed release build and Play Console review are required before calling this release-ready.
