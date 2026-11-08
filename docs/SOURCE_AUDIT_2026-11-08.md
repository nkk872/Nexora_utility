# Nexora Utility source audit — 2026-11-08

## Archive recovered
The saved `Nexora-Utility-build-candidate.zip` archive is available and contains 75 files, including 39 Kotlin source files, a Kotlin/Compose Android app module, Gradle build scripts, a version catalog, resources, and a React preview. The React preview is not the Android app. The archive does not include a Gradle wrapper.

## Current GitHub state
The current `main` tree still lacks the native source and Gradle project files. The latest CI run (42462401755) fails at the required-file gate before SDK setup or compilation. No Kotlin compile or APK task has run.

## Static source findings (not device-tested)
1. **Cleaner scope:** `StorageScannerService` measures real device partition usage but scans only Nexora's own internal/external cache directories. It cannot clean other apps' private caches. UI copy must not imply a device-wide junk cleanup.
2. **VPN:** The code correctly fails closed when there is no provider profile or packet-forwarding implementation. There are two competing service/UI paths (`VpnTunnelService` and `NexoraVpnService`) plus a separate WireGuard manager. Consolidate to one path before release. The WARP profile comments and manager constants are misleading: no registered client profile exists, and a template private key must never be shipped. Do not show connected/protected until a real WireGuard handshake and routed traffic are verified.
3. **App Lock:** The default protected-package set includes Settings, Photos, Play Store, and YouTube without user selection. PIN salt/hash, selected packages, master enable state, and lockout counters are held in process memory, so they do not survive process death/reboot. Make the initial list empty, persist only necessary state using encrypted app-private storage, and provide clear accessibility/overlay consent and disable controls. Accessibility must not collect screen contents.
4. **Vault:** AES-GCM file encryption with an Android Keystore key is present. However, the vault list reconstructs generic filenames from encrypted file names; original names/MIME metadata are not persisted, so metadata and recovery UX need work. Verify partial-file cleanup on failed encryption/decryption and recovery behavior after app data/key loss.
5. **Duplicates:** Exact duplicates are discovered through MediaStore and SHA-256. Keep deletion opt-in, show individual items and sizes, and require explicit confirmation; verify scoped media permission behavior on Android 13+.
6. **Permissions/release:** Review the manifest against current Android and Google Play policies. Request media access only when the user opens the relevant feature; remove unused broad permissions. Verify foreground service types/notifications and accessibility disclosure. Test minSdk 26 through supported recent Android versions.

## Next engineering sequence
- Import the recovered native source tree into the repository without overwriting the existing CI and policy documentation.
- Run compile, lint, unit tests, and APK assembly; fix actual build output rather than guessing.
- Fix App Lock persistence/default selection and Vault metadata/recovery.
- Consolidate VPN architecture and keep it explicitly unavailable until real tunnel forwarding is implemented and verified.
- Test destructive actions, permission denial/revocation, install/upgrade, and privacy disclosures on physical devices.

## Release truth
This is a static source audit only. No successful Android build, APK installation, VPN protection, or physical-device test is claimed.
