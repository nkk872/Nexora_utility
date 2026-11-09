# Nexora platform capability and release gates

## App management
A normal Android app cannot silently hide arbitrary installed apps or lock other apps. Package visibility is restricted and accessibility-based app locking has significant Play policy and disclosure requirements. Nexora must not claim system-wide app lock or app hiding unless a compliant, user-enabled implementation is reviewed and tested. The current safe implementation links to Android App info settings.

## Game and device tools
Nexora may show device facts and launch supported system settings. It cannot promise CPU/GPU overclocking, RAM cleaning, thermal bypass, or force-stop behavior on ordinary devices. Any future game panel must clearly distinguish diagnostics/shortcuts from actual performance controls.

## VPN
A VPN requires a configured provider endpoint/profile, credentials or keys, and user consent through Android's VPN permission flow. A VPN screen without a provider cannot establish a secure tunnel. Do not mark this stage complete until a real provider is selected, the profile is configured, a handshake is observed, and routed traffic/DNS-leak behavior is tested.

## Advertising
Only one anchored adaptive banner is permitted. Do not enable live ads until the owner supplies the production AdMob App ID and banner unit ID and privacy/consent disclosures are ready. Google's official test banner unit may be used only for development QA. Never invent production identifiers.
