# Nexora Utility — security audit update

Date: 2026-11-08

## Changes in this source pass
- App Lock no longer restores an unlocked session after process death/restart when a PIN is configured and protection is enabled.
- Master App Lock cannot be enabled before a PIN exists.
- Vault authorization respects the user's Protect Vault setting.
- Vault metadata is not enumerated/decrypted until the Vault authorization gate is passed; the in-memory list is cleared while locked.
- UI wording says Android Keystore rather than claiming hardware-backed storage on every device.

## Remaining limits
- App Lock session state is not yet a universal authorization service. Duplicate-photo and settings routes still need mandatory gates before being described as protected.
- Auto-lock timeout is persisted but not yet enforced from app background/foreground lifecycle events.
- Accessibility app locking and overlays require explicit opt-in, policy review, and real-device testing.
- Biometric unlock remains disabled.
- Android Keystore hardware backing varies by device.
- VPN remains unavailable until a valid WireGuard profile, handshake, and real packet routing are verified.
- Storage access, package visibility, uninstall intents, and foreground-service declarations need final Android/Play policy review.

## Build verification
No Android SDK/Gradle build or physical-device test was run in this environment. These are source-level changes only; this document is not proof of a successful build or release readiness.