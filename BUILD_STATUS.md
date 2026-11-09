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
- Tools diagnostics: device manufacturer/model, Android version/API, available RAM, battery percentage and thermal status when supported.
- Tools shortcuts to Android App info, network, display, battery and sound settings.

## Eight development stages
1. Cleaner and storage analyzer — source implementation present; Android build/device verification pending.
2. Duplicate photo/video detection — exact duplicate hashing is implemented for the currently listed large files only; broad media selection and full test coverage remain.
3. Encrypted private vault — encrypted storage/import/export is implemented; separate PIN/biometric access gate and security QA remain.
4. App lock — not implemented. System-wide locking of other apps is restricted and requires careful Android/Play policy review. Do not fake this.
5. App manager / app hiding — only Android App info settings shortcut exists. Arbitrary app hiding is not supported for ordinary apps; supported app-management flow remains.
6. Device diagnostics / game tools — basic live diagnostics and system-settings shortcuts are implemented; game-specific tools remain.
7. VPN — not implemented. Requires a selected provider, profile/credentials and Android VPN consent; connection must be verified by handshake and routed traffic before reporting connected.
8. Ads, testing and release — no AdMob IDs or UMP consent integration configured; no successful build, lint, test, APK, AAB or device QA confirmed.

## Build verification
- CI workflow is configured to compile Kotlin, run lint/unit tests, and assemble debug/release APKs.
- GitHub connector currently exposes no workflow run for the latest direct pushes; combined commit status is empty. Therefore CI success is not confirmed.
- Local build could not be run in this environment because GitHub/Gradle dependencies cannot be fetched.
- Do not mark any release as build-ready until an actual successful CI build and device QA are observed.

## Product / monetization rules
- Free to use: no subscriptions, premium tier, credits, paid unlocks or in-app purchases.
- At most one anchored adaptive banner at the bottom.
- No interstitial, rewarded, app-open, native/in-feed, top, pop-up, notification or full-screen ads.
- Do not enable live ads without the owner's real AdMob App ID and banner unit ID, consent flow, privacy disclosures and QA.
- Never silently delete personal files or claim fabricated VPN, cache, RAM, battery or performance results.
