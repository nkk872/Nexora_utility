# Nexora Utility — implementation and verification status

Last updated: 2026-11-09.

## Repository and product
- Repository: `nkk872/Nexora_utility`, branch `main`.
- Native Kotlin + Jetpack Compose Android app; dark black / emerald / white UI.
- Free product; no premium tier, subscription, credits or paid unlocks.
- Minimum Android API 26; compile/target SDK 35; JDK 17; Gradle 8.9 in CI.

## Implemented features
- Home, Clean, Storage, Vault, VPN and Tools sections.
- Safe estimate and explicit cleanup of Nexora's own temporary cache only.
- Internal data-partition storage figures.
- User-selected SAF folder scan; large files >=20 MB; 5,000-entry scan limit and up to 100 displayed matches.
- Explicit file selection and confirmation before deletion.
- Exact SHA-256 duplicate grouping for currently displayed large files only; not similar-image detection or a complete device-wide media index.
- Encrypted vault files using AES-256-GCM and an Android Keystore non-exportable key. Import, export and delete actions are available; originals are left unchanged.
- Vault screen now gates viewing and actions behind Android strong biometric or device screen-lock credential using AndroidX BiometricPrompt. This is an in-app vault gate, not a system-wide app lock. Physical-device authentication QA is still required.
- Device diagnostics: manufacturer/model, Android version/API, available RAM, battery percentage and thermal status when supported.
- Shortcuts to Android App info/app management, manufacturer game settings when available, network, display, battery and sound settings.
- Embedded WireGuard client for user-provided WireGuard profiles.
- VPN Gate live volunteer relay directory; selected OpenVPN profiles are stored encrypted and can be handed to the separately installed OpenVPN for Android companion app through its documented remote AIDL API.
- VPN Gate availability, speed, uptime and privacy are not guaranteed; Nexora does not operate its own VPN server fleet.

## Android platform boundaries
- Ordinary apps cannot silently clear another app's cache or force-stop it.
- Ordinary apps cannot hide arbitrary installed apps or reliably lock other apps system-wide.
- Android does not grant third-party apps a general-purpose CPU/GPU game booster. Nexora must not claim fake RAM, battery, network or FPS improvements.
- File review uses user-selected folders through the system picker rather than requesting unrestricted storage access.

## Monetization
- Product remains free; no paid tiers or IAP.
- Only permitted placement: one anchored adaptive banner at the bottom.
- No interstitial, rewarded, app-open, native/in-feed, top, pop-up, notification or full-screen ads.
- Live ads remain disabled. Owner must supply a real AdMob App ID and banner unit ID; consent, privacy policy/data-safety disclosures and ad QA must be completed before enabling production ads.

## Build verification
- GitHub Actions run `42588203067` for commit `b15b669f7adc5ccb486db8c9b3bc71c8cbfee3f5` completed successfully.
- Kotlin compilation, Android lint, unit tests, debug APK assembly, unsigned release APK assembly and artifact upload all passed for that commit.
- Artifact: `nexora-utility-apks` (debug APK and unsigned release APK), artifact ID `13659673676`; expires 2026-11-23.
- Vault biometric changes and subsequent documentation updates were committed after that successful run. A fresh CI run must pass before the newer source is considered build-verified.
- Release APK is unsigned. A Play-distributable signed AAB/APK requires the owner's signing key and secure release configuration.
- No physical-device tests have been run from this environment. VPN handshake, route/DNS leak protection, reconnect, foreground service behavior, biometric UX and different Android OEM behavior remain unverified.

## Next external inputs / release gates
1. Owner's AdMob App ID and anchored adaptive banner ad unit ID.
2. Owner-controlled upload/release signing key and secure password configuration.
3. Privacy policy URL and finalized data-safety disclosures.
4. Physical Android device for vault biometric and VPN tunnel QA.
5. Separate OpenVPN for Android companion app for VPN Gate OpenVPN connections, or a real reachable WireGuard server/profile for WireGuard.
6. Google Play Console account and policy review before publishing.

Never describe the app as Play Store-ready, fully device-tested, or production VPN-ready until these gates are complete.
