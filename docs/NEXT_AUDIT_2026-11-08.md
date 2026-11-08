# Nexora Utility source audit — 2026-11-08 (next pass)

## Changes in this pass
- Added the AppLockScreen state-flow API to AppLockService so the screen's referenced properties and methods exist.
- Persisted app-lock timeout and internal protection preferences in app-private SharedPreferences.
- Kept biometric unlock explicitly unavailable; the UI now disables its switch and says it is not implemented.
- Added a PIN prompt for the in-app Lock Now action; the dialog remains open until PIN verification succeeds.
- Kept failed-attempt and lockout counters reflected in observable state.

## Remaining security limitations
- isSessionUnlocked is currently an app-lock UI/session flag, not yet an authorization gate enforced by every protected screen or repository. Vault/duplicate/settings screens still need a common mandatory gate before they can be called protected.
- External app locking depends on AccessibilityService and overlay behavior and must be reviewed against Google Play policy and tested on real devices. Do not promise compatibility with every app/device.
- BiometricPrompt integration is not implemented and remains disabled.
- App-lock preferences use app-private SharedPreferences, not encrypted preferences. PIN is stored as salted PBKDF2 verifier, not plaintext.
- Vault metadata is encrypted, but full recovery, tamper, key-loss, partial-write, and export tests remain required.
- VPN is not functional without a valid user/provider profile and verified packet routing.

## Build status
No Android SDK/Gradle build was run in this environment. These edits are source-level only; no compile or APK success is claimed.