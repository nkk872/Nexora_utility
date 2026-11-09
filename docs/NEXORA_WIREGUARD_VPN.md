# Nexora VPN architecture

Nexora VPN is a native WireGuard client built into Nexora Utility. It uses the official embeddable Android tunnel library `com.wireguard.android:tunnel` (Apache-2.0) and its userspace backend, so root access is not required.

## Setup

1. Obtain a valid WireGuard client profile (`.conf`) from a WireGuard server you own or are authorized to use.
2. In Nexora VPN, import the profile using Android's file picker.
3. Nexora encrypts the profile in its existing AES-256-GCM Android Keystore vault.
4. Tap Connect and approve Android's VPN permission dialog.
5. Tap Disconnect to stop the tunnel.

The profile contains a private key and must be treated as a secret. Do not commit it to Git, paste it into chats, or share it publicly. The repository ignores `.conf` files.

## Critical distinction: client vs provider

This implementation makes Nexora its own VPN *client app*. It does not provide VPN servers, internet egress, server locations, account provisioning, or free bandwidth. A working tunnel requires a reachable WireGuard server and valid peer configuration. To offer public VPN locations to users, the project owner must provision and maintain servers, IP addresses, routing, DNS, monitoring, abuse controls and operating costs.

## Release gates

The app must not claim a secure/connected service based only on the backend's UP state. Before release, verify a recent peer handshake, transferred traffic, public IP change, IPv4 and IPv6 routing, DNS leak behavior, reconnect after network changes, Android foreground-service behavior, battery/background behavior, and failure/kill-switch behavior on real devices.
