# Import the Nexora Android source (one-file upload)

The complete Android project has been packaged as `nexora-android-source.zip`. Its archive root contains `app/`, `gradle/`, `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, and `.gitignore`.

1. Download the generated `nexora-android-source.zip` bundle.
2. Open https://github.com/nkk872/Nexora_utility on GitHub.
3. Choose **Add file → Upload files** and upload the ZIP to the repository root with the exact filename `nexora-android-source.zip`.
4. Commit the upload to `main`.

Do not unzip it manually in the GitHub editor. The Android CI workflow now detects this archive, extracts it into the checkout, checks the required Gradle/Kotlin files, and then runs compile, lint, tests, and APK assembly.

The archive intentionally excludes the existing `.github/` workflow and `docs/` policies so they remain intact. After a successful CI run, download the debug APK from the workflow's `nexora-utility-apks` artifact. A release APK without signing secrets is unsigned and is not Play Store-ready.

This import step does not itself mean the app is compiled or tested; those claims depend on the subsequent CI result and physical-device smoke tests.
