# Nexora Utility Privacy Policy

**Effective date: 9 November 2026**

Nexora Utility is a free Android utility application. This policy describes the current app implementation. It must be reviewed and updated before any new analytics, advertising SDK, account system or other data collection is enabled.

## Information the app accesses

- **Files you select:** The Storage screen accesses only the folder you choose through Android's system folder picker. It reads file names, sizes and content for local large-file review and exact duplicate hashing. You choose whether to delete selected files and must confirm.
- **Private Vault:** Files you explicitly import are encrypted on your device using AES-256-GCM and a key held in Android Keystore. The app does not upload vault contents to Nexora servers. Export creates a decrypted copy at the destination you choose.
- **VPN profiles:** WireGuard and VPN Gate/OpenVPN profiles you import or select are stored encrypted in the app's private vault. A profile is shared with the relevant VPN engine only when you ask to connect.
- **Device diagnostics:** Device model, Android version, available memory, battery percentage and thermal status are read locally to display device information. The current implementation does not send these diagnostic values to Nexora.
- **Network access:** Internet access is used to retrieve the public VPN Gate server directory. The app does not operate its own VPN server network.

## VPN and third-party services

If you connect to a VPN Gate relay, your network traffic is routed through a volunteer-operated third-party server. That server operator may observe connection metadata and traffic that is not protected by end-to-end encryption such as HTTPS. VPN Gate relays have their own operators, privacy practices, availability and risks. Do not use public volunteer relays for banking or sensitive activity.

WireGuard connections use the server and peer configured in the profile you provide. OpenVPN connections are handled by the separately installed OpenVPN for Android companion app, which has its own privacy policy and data handling. Nexora does not control third-party VPN operators or the companion app.

## Advertising, analytics and accounts

The app includes the Google Mobile Ads SDK and Google's User Messaging Platform (UMP) consent SDK in test mode. It currently uses Google's public sample App ID and test banner unit ID only; no production AdMob account identifiers or revenue-generating ad units are configured. UMP consent information is requested at app launch, and the ad SDK is initialized only when UMP indicates that ad requests are permitted. The app displays at most one anchored adaptive test banner below navigation, and hides it on the Private Vault screen. Google may process device and ad-request information as described in Google's [Mobile Ads data disclosure](https://developers.google.com/admob/android/privacy/play-data-disclosure) and [privacy policy](https://policies.google.com/privacy). The owner must configure privacy messages, replace sample IDs with real identifiers and update Play Data safety disclosures before production ads are enabled. The approved product plan allows only one anchored adaptive banner; it does not authorize other ad formats.

## Storage, security and retention

Selected files and encrypted vault items remain in app-controlled storage until you delete them or uninstall the app. Android Keystore protects the vault encryption key. Android device security, backups, malware, a compromised operating system and files you deliberately export can affect confidentiality. Keep your device updated and use a secure screen lock.

The app does not silently delete personal files or other apps' data. You remain responsible for reviewing files before confirming deletion and for protecting exported copies.

## Children

Nexora Utility is a general-purpose utility app and is not designed specifically for children. The app does not knowingly collect personal information from children through an account or analytics system in the current implementation.

## Changes to this policy

This policy will be updated when app functionality, third-party services or data practices change. The effective date above will be revised when the policy changes.

## Contact

For privacy questions or requests, contact the developer through the Nexora Utility repository: https://github.com/nkk872/Nexora_utility/issues
