# Nexora Utility — auto-lock lifecycle patch

Date: 2026-11-08

## Source changes prepared
- AppLockService now records when the app enters background and enforces the selected auto-lock timeout when the app returns.
- A timeout of zero locks immediately on background.
- MainActivity invokes the lifecycle hooks.
- Privacy screen no longer displays a fabricated numeric privacy score or invented permission counts.
- Privacy copy now states that biometric unlock is not implemented and directs users to Android app-permission settings.

## Verification
- ZIP integrity check passed.
- Source-level inspection only. No Gradle/Android SDK build, unit test, emulator run, or physical-device test was performed.
- The ZIP is not automatically uploaded into the repository by this documentation commit.

## Remaining release gates
- Import the full native project into the repository.
- Run compile, lint, and unit tests in CI.
- Add lifecycle and PIN verification tests.
- Enforce authorization consistently across all protected routes and data operations.
- Verify AccessibilityService behavior and Play policy eligibility.
- Configure and test a real WireGuard provider profile and routed traffic, or keep VPN marked unavailable.
- Test Vault tampering, key loss, reinstall, restore, partial writes, and recovery on supported Android versions.