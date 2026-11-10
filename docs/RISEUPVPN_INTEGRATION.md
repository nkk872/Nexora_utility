# RiseupVPN companion integration

## Decision

Nexora Utility integrates RiseupVPN as an external companion app first. The official RiseupVPN Android client is a custom-branded Bitmask Android client and uses Riseup's own provider configuration and tunnel lifecycle.

## What Nexora does

- Detects whether the RiseupVPN Android package (`se.leap.riseupvpn`) is installed.
- Opens RiseupVPN when installed.
- Opens Riseup's official Android installation guide when it is not installed.
- Provides an F-Droid listing link.
- Reports Android's generic VPN transport and internet-validation state.

## What Nexora deliberately does not claim

- Generic Android VPN transport does not identify RiseupVPN as the active provider.
- Nexora cannot control RiseupVPN's connect/disconnect state through a documented public companion API.
- Nexora does not claim leak protection, endpoint identity, or a successful Riseup tunnel based only on Android network state.

## Why this approach

The LEAP Bitmask Android project is a full VPN client with native and submodule build requirements and GPL-3.0 licensing. Embedding or forking it would be a separate engineering and licensing decision, not a small SDK integration. The companion approach keeps the existing Nexora app and its current tunnel code intact while directing users to the official provider client.

## Device verification still required

1. Install RiseupVPN from the official provider instructions or F-Droid.
2. Open it from Nexora.
3. Approve Android's VPN permission in RiseupVPN.
4. Connect inside RiseupVPN and confirm its own UI reports connected.
5. Return to Nexora and verify Android reports a VPN transport.
6. Disconnect inside RiseupVPN and verify Android no longer reports a VPN transport.
7. Repeat on Wi-Fi and mobile data. Do not describe generic transport detection as proof of Riseup identity or leak protection.
