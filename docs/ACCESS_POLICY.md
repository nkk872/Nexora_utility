# Nexora Utility — access, privacy and permissions policy

**Product access promise:** Nexora Utility is free for everyone. All core features are available without payment, subscription, premium tier, credits, account registration, or advertising-based unlocks. No feature is intentionally withheld behind a paywall.

## Access rules
- Anyone with a compatible Android device may install and use the app.
- Local features work without signing in.
- Users may decline optional permissions and continue using unrelated features.
- Permissions are requested just in time, with a clear explanation.
- No user files, photos, PINs, vault keys, installed-app lists, or usage history are uploaded, sold, or shared for advertising or analytics.

## Feature permission boundaries
| Feature | Access model | Limitation |
|---|---|---|
| Storage overview | Android storage APIs where available | Report only OS-exposed values; no fabricated savings |
| Media scan / duplicates | MediaStore and user-selected folders via Storage Access Framework | Scan only accessible media; no silent broad filesystem access |
| Cleanup assistant | Review candidates; user confirms; open system settings or document picker | Cannot clear another app's private cache or protected data |
| App manager | Visible packages and Android app-info/uninstall intents | No arbitrary app hiding, force-stop, or silent uninstall |
| Private vault | App-private storage with Android Keystore-backed encryption | Exclude vault data from backup; explain recovery limits |
| App lock | Only after user enables Accessibility service in Android Settings | Never silently enable; do not collect screen text or credentials; provide disable path |
| VPN | Android VpnService consent plus real provider/configuration and working tunnel | Show disconnected until tunnel is established; no shared private keys or fake locations |
| Game/device tools | Public Android APIs and system settings | No guaranteed FPS, RAM boost, battery repair, or overclock claims |

## Data and security
- Keep sensitive data on-device by default.
- Use Android Keystore; never hard-code secrets.
- Exclude vault content and secrets from backup/device transfer.
- PINs must be salted and derived using a suitable password KDF; never store plaintext PINs.
- Provide a privacy screen explaining local data, permissions, and revocation.
- Any future network feature requires clear disclosure and opt-in before transmission.

## Accessibility and compatibility
- Support font scaling, TalkBack labels, sufficient contrast, and graceful fallbacks.
- Explain unavailable APIs rather than simulating success.
- Never block emergency/system UI or trap users in an app-lock overlay.

## Release acceptance tests
1. Fresh install launches without account creation or payment.
2. Optional permissions can be denied; unrelated screens remain usable.
3. No purchase, subscription, credit check, or premium gate exists.
4. Scan/storage figures come from Android APIs and are labelled accurately.
5. Destructive actions require review/confirmation and report the actual OS outcome.
6. Vault encryption, key lifecycle, backup exclusion, and recovery pass security review.
7. App-lock consent, disable flow, and sensitive-content handling pass manual testing.
8. VPN never reports connected unless a real authenticated tunnel routes traffic.
9. Lint, unit tests, debug/release compilation, and device smoke tests pass.
10. Privacy disclosures and store listing match actual behavior.

A feature that cannot meet Android platform or security requirements must be labelled limited/unavailable or omitted, never falsely represented as working.
