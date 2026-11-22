# Antigravity (Android Remote Client)

Cliente nativo Android (Kotlin + Jetpack Compose + Material 3) en modo pantalla completa oscura para controlar **Antigravity Remote** (`https://antigravity.google.com/remote`) sin barras de ajustes ni configuración manual.

## Características clave

- **Estética oscura fiel a Antigravity**: lienzo `#000000`, superficies `#121318`, bordes `#232632` y acento `#4C8BF5`, sin barras flotantes ni selector de temas.
- **Sesión persistente real**: `CookieManager` con cookies de terceros habilitadas y `flush()` automático en `onPageFinished`, `onPause` y `onStop`, `domStorageEnabled`, restauración de la última ruta visitada en `DataStore` y User-Agent compatible con Google Sign-In (sin bloqueo `403 disallowed_useragent`).
- **Optimización móvil automática**: viewport táctil, ajuste automático con teclado (`adjustResize` + `imePadding`), scroll 100% nativo sin simulación, prevención de zoom involuntario al escribir, soporte de selector de archivos y popups OAuth dentro de la misma sesión.
- **Notificaciones nativas Android**: puente JS (`AntigravityNativeBridge`) que intercepta `window.Notification` y entrega notificaciones nativas limpias en el canal *Antigravity Remote*.
- **Reconexión automática**: detección de conectividad con `NetworkMonitor` y recarga automática al volver la conexión.

## Compilar APK

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

El APK queda en `app\build\outputs\apk\debug\app-debug.apk`.
