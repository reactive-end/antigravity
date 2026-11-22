# Antigravity (Unofficial Android Remote Client)

A lightweight native Android client (Kotlin + `WebView`, **~358 KB** release APK) built to run **Google Antigravity Remote** (`https://antigravity.google.com/remote`) in fullscreen dark mode with mobile-friendly touch targets.

> [!NOTE]
> **Trademark & Affiliation Disclaimer**
>
> This is an **unofficial** personal project with **no affiliation, sponsorship, or endorsement by Google LLC**.
> The **Google Antigravity** and **Antigravity** names, the official logo, and `antigravity.google.com/remote` are the property and trademarks of **Google LLC**. This wrapper source code is shared strictly for personal and educational use.

## Features

- **Lightweight & Fast**: Plain Android Views without heavy UI frameworks, compiled in release mode with R8 minification and resource shrinking (~358 KB).
- **Fullscreen Dark UI**: Edge-to-edge `#000000` theme paired with dark Onyx CSS overrides, mobile touch-target sizing (`38–44px` controls), truncated model selector pill, and disabled `backdrop-filter` for smoother WebView scrolling.
- **Persistent Session & Google 2FA**: Automatic cookie flushing (`CookieManager`) plus URL routing (`AntigravityUrlPolicy`) that keeps Google sign-in, 2-step verification, regional `google.*` domains, and `intent://` fallbacks inside the app.
- **Mobile Ergonomics**: Prevents accidental input zoom (`16px` form controls), supports native multi-file picking (`OpenMultipleDocuments`), injects styles across open Shadow DOM roots and SPA route changes, and reloads automatically when connectivity returns (`NetworkMonitor`).
- **Notification Bridge**: Supports Android 13+ (`POST_NOTIFICATIONS`) permission checks with a settings fallback and bridges Web notifications while active.

## Download APK

Go to **[Releases](../../releases/latest)** and download **`Antigravity.apk`** (requires Android 8.0 / API 26 or higher; targets API 35).

## Build from Source

Requires **JDK 17** and the **Android SDK** (`compileSdk = 37`):

```powershell
.\gradlew.bat testDebugUnitTest assembleRelease
```

The release APK is generated at `app\build\outputs\apk\release\app-release.apk`.
