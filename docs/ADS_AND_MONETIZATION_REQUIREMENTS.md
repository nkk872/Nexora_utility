# Nexora Utility — advertising and monetization requirements

Last updated: 2026-11-08.

## Product decision

Nexora Utility is free to download and use. The developer earns revenue from advertising only.

- No premium tier, subscription, paid unlock, credits, paywall, or in-app purchase.
- The only permitted ad format is one anchored adaptive banner fixed at the bottom edge of the app.
- No interstitial, rewarded, app-open, native/in-feed, top banner, pop-up, notification, or full-screen ads.
- The banner must never cover navigation, buttons, system gesture areas, or content. Reserve its measured height in the layout.
- If an ad fails to load, the app remains fully usable and the reserved area may collapse cleanly.
- Never encourage users to click ads or place controls close enough to cause accidental clicks.
- Consider suppressing the banner on sensitive Vault/PIN/accessibility-permission screens.

## Implementation target

Use Google Mobile Ads SDK for Android with an anchored adaptive banner in a single reusable Jetpack Compose component. Keep the app ID and production ad unit ID in build configuration / manifest placeholders, not hardcoded in source control. Use Google's official test banner ad unit during development and QA only:
`ca-app-pub-3940256099942544/9214589741`.

Do not enable live ads until the publisher has supplied the real AdMob App ID and banner Ad Unit ID, app listing details are correct, privacy disclosures are published, and consent handling is tested. Never use a production ad unit for development tests.

## Privacy and consent

- Integrate the current Google User Messaging Platform (UMP) consent flow as required for the app's users and regions.
- Provide an accessible privacy policy describing data collection, advertising SDKs, device identifiers, and consent choices.
- Complete Google Play Data safety and app content declarations accurately.
- Do not initialize or request personalized ads before the applicable consent/privacy state is resolved. Respect limited/non-personalized ad choices and platform requirements.
- Do not collect extra data merely to increase ad targeting.

## Release QA for ads

- Verify exactly one banner slot exists and it is at the bottom on all supported screen sizes/orientations.
- Verify banner never overlaps the app's bottom navigation or Android system navigation/gesture area.
- Verify no other ad formats or placements exist.
- Test with Google's test ad unit; test no-fill, offline, timeout, rotate, background/foreground, and process recreation.
- Verify all app features remain usable when ad SDK initialization or loading fails.
- Inspect ad clicks only through the SDK's normal reporting; never generate test clicks on live ads.
- Replace the test unit with the owner's real unit only for release after the owner's explicit configuration.

## Current implementation state

**Test integration added; production monetization not enabled.** The app now requests UMP consent before initializing Google Mobile Ads and displays only one Google-provided anchored adaptive test banner below the navigation bar (hidden on the Private Vault screen). Debug builds use Google's sample test App ID and banner unit ID. Release builds show no banner unless both owner production IDs are supplied as GitHub Actions secrets `NEXORA_ADMOB_APP_ID` and `NEXORA_ADMOB_BANNER_ID`; this prevents test ads from accidentally shipping in production. The owner must create the required privacy messages in AdMob, supply the real App ID and banner unit ID through secure build configuration, publish the privacy policy, complete Data safety declarations and test the consent/banner flow before release.
