# Nexora Utility — implementation audit

Audit date: 2026-11-08

## Scope

Native Kotlin + Jetpack Compose Android app. The web preview is not the Android product.

## Confirmed source modules in the supplied project archive

- Cleaner/storage scan screens and StorageScannerService
- Large-file and duplicate-photo screens/repositories
- App manager and app-lock screens/service
- Private vault repository using Android Keystore and AES-GCM
- VPN screens and multiple competing VPN service implementations
- Game tools, device tools, settings, and dashboard screens

Presence of a screen or class is not proof that the feature is production-ready.

## Critical release blockers found during source inspection

1. The original manifest referenced missing mipmap launcher icons. A drawable launcher icon is now supplied and the manifest references it.
2. App backup was enabled while a private vault exists. The manifest now disables Android backup by default.
3. VPN architecture is inconsistent: the dashboard binds to VpnTunnelService, while the VPN screen uses NexoraVpnService. The former contains a hard-coded template private key and static WARP profile; the latter establishes a TUN interface but does not implement a packet-forwarding WireGuard tunnel. Neither should be advertised as a working VPN in this state.
4. App-lock PIN and locked-package state are held in process memory in the current implementation; durable, protected persistence and restart/reboot tests are required.
5. The Android build has not been verified. The supplied project has no Gradle Wrapper, and a configured Android SDK/JDK build environment is not available in this audit environment.
6. The source must be tested on real Android devices for storage access, app visibility, accessibility disclosures, vault import/export and recovery, foreground-service behavior, and Android-version compatibility.

## Delivery sequence

1. Make the project build reproducibly (Gradle Wrapper, SDK/AGP compatibility, manifest/resources, CI).
2. Consolidate VPN to one WireGuard implementation. Require a genuine user/provider-issued profile; never ship template private keys or claim a tunnel is connected without a successful WireGuard handshake and traffic verification.
3. Persist app-lock settings securely and harden the accessibility flow with clear opt-in disclosure.
4. Complete vault metadata persistence, authenticated unlock, safe import/export, and recovery tests.
5. Validate cleaner scanning/deletion boundaries; never claim to clean other apps' private caches or delete files without user review.
6. Complete app manager, duplicate/large-file flows, device diagnostics, and game tools using supported Android APIs.
7. Add unit/instrumentation tests, CI build, signed APK/AAB preparation, and Play policy review.

## Current status

Repository bootstrap and two manifest/resource hardening changes are committed. Full Android source import, successful Gradle build, APK generation, and device QA are not yet complete.
