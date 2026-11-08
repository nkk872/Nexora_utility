# Nexora Utility build status

The Android CI workflow is now configured to use JDK 17, Gradle 8.9, Android lint, unit tests, and a debug APK artifact.

## Source import status

The full Android Kotlin/Jetpack Compose project is prepared in the build-candidate archive supplied separately. It has **not yet been imported into this GitHub repository**. The repository currently contains setup documentation and selected bootstrap files, so CI cannot produce an APK until the Android project files are added.

## Safety fixes in the candidate source

- Removed the placeholder WireGuard private keys.
- Disabled the misleading VPN "connected/secured" behavior. A TUN interface without a packet-forwarding engine is not a functioning VPN.
- Removed broad all-files storage access from the manifest; the app should use scoped media access and the Storage Access Framework.
- Removed the nonexistent round launcher icon resource reference.
- Preserved the user-controlled app-lock accessibility/overlay flow with explicit Android settings consent.

## Next release gates

1. Import the candidate Android project into this repository.
2. Run the Android CI workflow and fix all compiler/lint/test failures.
3. Install the debug APK on a real Android device and verify permission-denied paths.
4. Complete security review of vault encryption, app-lock persistence, and storage cleanup.
5. Implement and independently verify a real VPN provider/packet-forwarding integration before advertising VPN protection.
6. Only then prepare a signed release APK/AAB.

A successful source archive export is not a compiled APK. Production readiness is not claimed until CI and device tests pass.
