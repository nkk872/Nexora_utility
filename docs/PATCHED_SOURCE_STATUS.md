# Nexora Utility — patched source candidate

Date: 2026-11-08

A patched source archive has been prepared in the working session as `nexora-android-source-patched.zip`. It is not yet committed to this repository and has not been compiled.

## Changes in the patched archive
- App Lock now loads and persists the user's selected protected package list, salted PBKDF2 PIN hash/salt, master enabled state, failed attempt count, and lockout expiry using app-private SharedPreferences.
- The previous hard-coded initial protected-app list is removed; users must select packages.
- MainActivity initializes App Lock persistence before UI interactions.
- Dashboard text now says “No VPN profile configured” instead of implying an in-memory VPN profile is ready.
- The source comment explicitly states no client private key is embedded.

## Still not release-ready
- The native Android project is not present on GitHub main, so CI has not compiled it.
- Android compilation, lint, tests, APK packaging and physical-device QA remain unverified.
- SharedPreferences persistence is not a substitute for complete device-level security testing. Verify PIN reset, lockout behavior, app updates, accessibility service disable/re-enable, and package selection on real devices.
- Vault filename/MIME metadata persistence still needs implementation and tamper/recovery testing.
- VPN remains unavailable until a real provider-issued/user-generated profile is connected through the WireGuard backend and handshake plus routed traffic are verified. The separate VPN service path that explicitly lacks packet forwarding must not be used to claim protection.
- Review manifest permissions and Play policy disclosures.

## Import instruction
Upload the patched ZIP to the repository root with the name `nexora-android-source.zip` and commit it to `main`, or commit its extracted project tree directly. Then inspect the new Actions run and fix actual build/test errors.

No successful build or installable APK is claimed.
