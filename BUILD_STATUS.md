# Nexora Utility — implementation and verification status

Last updated: 2026-11-09.

## Repository
- Repository: `nkk872/Nexora_utility`, branch `main`.
- Existing Kotlin + Jetpack Compose foundation retained; no project restart or duplicate setup.
- Dark black / emerald-green interface; free product, no premium tier or paid unlocks.

## Current implementation
- Home, Clean, Storage, Vault and Tools navigation.
- Nexora-owned cache estimate and explicit clearing of Nexora's own cache only.
- Internal data-partition storage figures.
- User-selected folder scan through Android Storage Access Framework; large files >=20 MB, scan capped at 5,000 entries, display capped at 100.
- Explicit per-file selection and confirmation before deletion.
- Exact SHA-256 duplicate grouping for the large files currently displayed by the folder scan. This is a bounded review, not a full-device media scan; no automatic deletion.
- Encrypted vault screen using AES-256-GCM and a non-exportable Android Keystore key. User can import a file into encrypted app-private storage, export a decrypted copy to a chosen destination, and delete the encrypted vault copy. Original source files are left untouched.
- Vault does not yet have a separate PIN/biometric gate. It must not be described as PIN/biometric protected.
- Tools diagnostics: device manufacturer/model, Android version/API, available RAM, battery percentage and thermal status when supported. Fixed escaped Kotlin string interpolation so these diagnostics render actual device values.
- Tools shortcuts to Android App info, network, display, battery and sound settings.

## Eight development stages
1. Cleaner and storage analyzer — source implementation present; Android build/device verification pending.
2. Duplicate photo/video detection — exact duplicate hashing is implemented for the currently listed large files only; broad media selection and full test coverage remain.
3. Encrypted private vault — encrypted storage/import/export is implemented; separate PIN/biometric access gate and security QA remain.
4. App lock — not implemented. System-wide locking of other apps is restricted and requires careful Android/Play policy review. Do not fake this.
5. App manager / app hiding — only Android App info settings shortcut exists. Arbitrary app hiding is not supported for ordinary apps; supported app-management flow remains.
6. Device diagnostics / game tools — basic live diagnostics and system-settings shortcuts are implemented; game-specific tools remain.
7. VPN — built-in WireGuard client engine added using the official embeddable `com.wireguard.android:tunnel` library. Nexora imports a user-supplied WireGuard `.conf` profile into the existing AES-256-GCM Android Keystore vault, requests Android VPN consent, and can start/stop a `GoBackend` tunnel. This is a client, not a server/provider: a reachable WireGuard server and valid peer profile are required. Handshake, traffic routing, IP/DNS leak protection and physical-device QA remain unverified.
8. Ads, testing and release — no AdMob IDs or UMP consent integration configured; no successful build, lint, test, APK, AAB or device QA confirmed.

## VPN implementation details
- Replaced the Proton companion shortcut with an in-app WireGuard client screen.
- Uses the official embeddable WireGuard tunnel AAR from Maven Central and the userspace backend; root access is not required.
- Imports `.conf` via Android's document picker and encrypts it using the existing Android Keystore AES-256-GCM vault; only the encrypted vault ID is stored in preferences.
- Requests Android VPN consent before connecting and provides connect/disconnect controls.
- No VPN server, relay infrastructure, subscription service or free exit locations are supplied. A valid profile for a reachable WireGuard server is required.
- Build, handshake, route, DNS/IP leak, reconnect, foreground-service and physical-device QA remain release gates.

## Build verification
- Existing `.github/workflows/android-ci.yml` runs on `main` pushes, pull requests, and manual dispatch. It installs JDK 17 / Android SDK 35, provisions Gradle 8.9, generates the wrapper when absent, compiles Kotlin, runs lint and unit tests, and assembles debug plus unsigned release APKs as workflow artifacts.
- Updated the workflow to `gradle/actions/setup-gradle@v6` and fixed the diagnostics string interpolation in `MainActivity.kt`.
- A source commit has been pushed to trigger CI, but no successful workflow result or APK artifact has yet been observed. Built-in WireGuard integration requires a successful CI build and physical-device testing with a valid server profile. Build status remains unverified until GitHub Actions completes.
- No local Android SDK / Gradle build was run in this environment.
- Do not mark any release as build-ready until an actual successful CI build and physical-device QA are observed.

## Product / monetization rules
- Free to use: no subscriptions, premium tier, credits, paid unlocks or in-app purchases.
- At most one anchored adaptive banner at the bottom.
- No interstitial, rewarded, app-open, native/in-feed, top, pop-up, notification or full-screen ads.
- Do not enable live ads without the owner's real AdMob App ID and banner unit ID, consent flow, privacy disclosures and QA.
- Never silently delete personal files or claim fabricated VPN, cache, RAM, battery or performance results.
