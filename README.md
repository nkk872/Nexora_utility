# Nexora Utility

Native Android utility app planned with Kotlin and Jetpack Compose.

## Product scope
- Smart cleaner and storage analyzer
- Large-file and duplicate-photo manager
- Android-supported app manager and user-controlled app-lock tools
- Encrypted private vault
- WireGuard VPN client (only when a real provider profile and verified routing are configured)
- Game tools and device diagnostics
- Privacy, permissions, and settings

## Monetization
Nexora Utility is free to use and earns revenue through advertising only. The only permitted placement is one anchored adaptive banner at the bottom of the app. No subscriptions, premium tier, paid unlocks, credits, interstitials, rewarded videos, app-open ads, pop-ups, or full-screen ads. Features must remain usable when the banner fails to load.

See [bottom-banner advertising requirements](docs/ADS_AND_MONETIZATION_REQUIREMENTS.md).

## Engineering principles
- Never fabricate scan sizes, battery health, RAM savings, device-performance improvements, or VPN status.
- Never delete user files without explicit review and confirmation.
- Use Android-supported APIs and explain limitations of app hiding, background cleaning, and performance controls.
- Keep vault content out of device backups; protect secrets with Android Keystore-backed encryption.
- Request sensitive permissions only when the user activates the feature, with clear disclosure.
- Use Google's test ad unit during development; add UMP consent and accurate privacy disclosures before enabling live ads.

## Build status
The repository does not yet contain the complete Kotlin/Gradle project. The latest GitHub Actions run failed at the native project presence check and did not compile Kotlin or build an APK. An Android Gradle build and real-device QA have not been verified. See [BUILD_STATUS.md](BUILD_STATUS.md) for current evidence and release gates.

## Project identity
- Application ID: `com.nexora.utility`
- Main branch: `main`
- Planned implementation: `app/` (Kotlin + Jetpack Compose)
