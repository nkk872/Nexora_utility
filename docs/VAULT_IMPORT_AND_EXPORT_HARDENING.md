# Nexora Utility — Vault import/export hardening

Date: 2026-11-08

## Changes in this source pass
- Vault imports preserve the selected document's display name where the document provider exposes it.
- Each import uses a unique temporary cache filename to avoid collisions between overlapping imports.
- Temporary plaintext import copies are deleted in a finally block after encryption attempts.
- Decrypted exports are written to a temporary file and authenticated by AES-GCM before publication.
- If replacing an existing export destination fails, the previous destination is restored where possible.
- Vault deletion removes metadata only after ciphertext deletion succeeds.

## Limitations and release gates
- The original selected file is deliberately preserved; the app does not claim to securely erase arbitrary source-provider files.
- Android storage providers may not expose a display name; a generic fallback is used.
- A crash can leave a cache temporary file; startup cleanup of stale import/export temporary files remains to be implemented.
- Cryptographic recovery, tamper, key-loss, reinstall, backup/restore and large-file tests remain required.
- Source-level changes only. No Gradle build, lint, unit test, emulator test or physical-device test was run in this environment.
