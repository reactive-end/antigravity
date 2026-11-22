# Antigravity (Unofficial Android Remote Client)

Cliente ligero para Android (Kotlin + WebView nativo, **~350 KB** en release) pensado para usar **Google Antigravity Remote** (`https://antigravity.google.com/remote`) a pantalla completa oscura y con mejor respuesta táctil que el navegador móvil.

> [!NOTE]
> **Aviso legal y de marca / Trademark Disclaimer**
>
> Este es un proyecto personal **no oficial** y **sin afiliación, patrocinio ni respaldo de Google LLC**.
> El nombre **Google Antigravity**, **Antigravity**, su logotipo oficial y la plataforma `antigravity.google.com/remote` son propiedad y marcas registradas de **Google LLC**. El código fuente de este envoltorio cliente se publica únicamente con fines educativos y de uso personal.

## Características

- **Ligero y rápido**: jerarquía nativa Android Views sin frameworks pesados y compilación release con R8/shrink (~350 KB), pensado incluso para teléfonos con hardware modesto.
- **Pantalla completa oscura**: estética oscura (`#000000`) alineada a Antigravity Desktop, sin barras de ajustes ni menús flotantes.
- **Sesión persistente + Google 2FA**: cookies persistentes con `flush()` automático y política de URLs que mantiene los redireccionamientos de inicio de sesión y verificación en 2 pasos (incluidos dominios regionales `google.*` e `intent://`) dentro de la app.
- **Ajuste móvil automático**: viewport táctil, prevención de zoom involuntario al escribir, soporte para selector de archivos, recarga automática al recuperar internet (`NetworkMonitor`) y desactivación de `backdrop-filter` pesados en GPU móviles.
- **Puente de notificaciones**: integra permisos de Android 13+ (`POST_NOTIFICATIONS`) e intercepta avisos mientras la app está en primer plano.

## Descargar APK

Ve a la sección **[Releases](../../releases/latest)** y descarga **`Antigravity.apk`** (Android 8.0 / API 26 o superior).

## Compilar desde código fuente

Requiere JDK 17 y Android SDK (compileSdk 37):

```powershell
.\gradlew.bat testDebugUnitTest assembleRelease
```

El APK firmado queda en `app\build\outputs\apk\release\app-release.apk`.
