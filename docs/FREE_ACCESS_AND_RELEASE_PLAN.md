# Nexora Utility — free-access product and release plan

## Product promise
Nexora Utility is intended to be free for everyone. There are no paid tiers, subscriptions, premium unlocks, credit limits, or paywalls. Optional donations may be considered later, but must never gate functionality.

## User-facing capabilities
- **Storage overview:** show Android-reported total, used, and available storage.
- **Media review:** find large media and likely duplicate photos only within folders/media the user has granted access to.
- **Cleanup assistant:** explain safe cleanup choices and open the relevant Android settings or file picker. Never claim that Android lets an ordinary app clear another app's private cache.
- **Installed-app manager:** show apps Android makes visible and offer supported system uninstall/app-info actions. Do not promise arbitrary app hiding.
- **Private vault:** store user-selected files in app-private storage with encryption; exclude vault data from Android backup; provide explicit export/recovery warnings.
- **App lock:** only enable after clear disclosure and the user manually enables the Accessibility service in Android settings. Never enable it silently or collect screen contents.
- **Device tools:** show available system information without claiming that a RAM cleaner or game booster can increase hardware performance.
- **VPN:** show disconnected/unconfigured until a real, user-authorized VPN provider or valid WireGuard configuration is supplied. Never ship shared private keys, fake server locations, or a simulated connected state.

## Permissions and privacy
Request permissions just in time, explain why they are needed, and offer a usable limited mode when declined. Prefer Android Storage Access Framework and MediaStore over broad all-files access. Do not request overlay, accessibility, usage access, or all-files access for unrelated features. No account is required for local utility features. Do not transmit files, photos, PINs, vault keys, or usage data to a server.

## Release gates (must all pass before calling the app production-ready)
1. Import and review the complete Android source in this repository.
2. Build debug and release variants with a pinned Gradle wrapper and JDK 17.
3. Run unit tests, Android lint, and a device/emulator smoke test on Android 8+.
4. Verify permission-denied and permission-revoked flows.
5. Security review vault encryption, key handling, backup exclusions, app-lock persistence, and VPN implementation.
6. Verify all visible controls perform the advertised action and all displayed numbers come from Android APIs.
7. Produce a signed APK/AAB only after the build and security checks pass.

## Current status
The GitHub repository currently contains project setup/audit documentation and a launcher vector/manifest work, not the complete Android source tree. A supplied source archive contains the native Compose screens and services, but has not yet been imported and its build has not been verified. Therefore no installable APK/AAB is claimed at this stage.
