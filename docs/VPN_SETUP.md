# VPN setup in Nexora Utility

## Current implementation

Nexora includes a provider-neutral VPN setup screen. It:
- Opens Android's native VPN settings.
- Detects whether the active network currently exposes Android's VPN transport.
- Clearly reports that no VPN provider is selected or bundled.
- Does not collect, store, or transmit provider passwords, private keys, or configuration profiles.

The app requests only `ACCESS_NETWORK_STATE` to inspect the active network transport. It does not implement a VPN tunnel and does not claim to encrypt or route traffic itself.

## How to configure a real provider

1. Choose a reputable VPN provider that supports Android.
2. Install its official Android app from the provider's official distribution channel, or obtain its supported Android profile instructions.
3. Open Nexora > VPN > Open Android VPN settings.
4. Follow the provider's setup instructions. If the provider uses its own app, complete setup in that app. If it supplies a profile supported by Android's built-in VPN client, add it in Android settings.
5. Accept Android's VPN consent prompt when shown.
6. Return to Nexora and tap Refresh VPN status.

Do not enter credentials into Nexora. Enter them only in the provider's official app or Android's system VPN screen.

## What status means

An active VPN transport means Android reports that the current active network is a VPN. It does not prove which provider is connected, whether the tunnel is correctly encrypted, whether all traffic or DNS is routed through it, whether a kill switch works, or whether there are IP/DNS leaks.

A future provider-specific integration must be selected and implemented with its supported SDK/profile format, then verified with handshake/session status, route inspection, DNS/IP leak tests, reconnect behavior, and Android device QA. Nexora must not display "secure" or "protected" solely because a VPN transport is present.
