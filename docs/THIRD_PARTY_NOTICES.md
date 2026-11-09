# Third-party notices

## OpenVPN for Android remote control API

Nexora includes the AIDL interface definitions and the small `APIVpnProfile` parcelable used by the documented remote API of [OpenVPN for Android](https://github.com/schwabe/ics-openvpn).

The upstream project's `remoteExample` is licensed under the Apache License, Version 2.0. The upstream README explicitly states that controlling OpenVPN for Android through its AIDL API from an external app is not subject to the engine's GPL license. The `APIVpnProfile` API class is explicitly marked by its author as exempt from GPLv2.

- Copyright: Arne Schwabe
- License: Apache-2.0 for the remoteExample API
- License text: https://www.apache.org/licenses/LICENSE-2.0
- Upstream API example and source: https://github.com/schwabe/ics-openvpn/tree/master/remoteExample
- Upstream API documentation: https://github.com/schwabe/ics-openvpn/blob/master/doc/README.txt

Nexora does **not** bundle or redistribute the OpenVPN for Android tunnel engine. Users must separately install the OpenVPN for Android app (package `de.blinkt.openvpn`). That companion app is GPLv2 and remains a separate application. Nexora communicates with it through the published remote API.
