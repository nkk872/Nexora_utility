# Nexora Utility

Native Android utility app built with Kotlin and Jetpack Compose.

## Product scope
- Smart cleaner and storage analyzer
- Large-file and duplicate-photo manager
- App manager and user-controlled app-lock tools
- Encrypted private vault
- WireGuard VPN client (requires user-supplied/approved server configuration)
- Game tools and device diagnostics
- Privacy, permissions, and settings

## Engineering principles
- Never fabricate scan sizes, battery health, RAM savings, or VPN status.
- Never delete user files without explicit review and confirmation.
- Use Android-supported APIs and explain limitations of app hiding, background cleaning, and performance controls.
- Keep vault content out of device backups; protect secrets with Android Keystore-backed encryption.
- Request sensitive permissions only when the user activates the feature, with clear disclosure.

## Build status
The native source package is being audited and repaired. An Android Gradle build and real-device QA have not yet been verified. Do not treat the current feature screens as release-ready until tests pass.

## Project identity
- Application ID: `com.nexora.utility`
- Main branch: `main`
- Primary implementation: `app/` (Kotlin + Jetpack Compose)
- `src/` is a companion web preview, not a substitute for the Android app.
