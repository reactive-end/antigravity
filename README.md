# Antigravity (Android Remote Client)

Cliente nativo Android (Kotlin + Jetpack Compose + Material 3) para controlar **Antigravity Remote** (`https://antigravity.google.com/remote`) con estética oscura/cósmica fiel a Antigravity, sesión persistente y notificaciones nativas.

## Características clave

- **Sesión persistente real**: `CookieManager` con cookies de terceros habilitadas y `flush()` automático en `onPageFinished`, `onPause` y `onStop`, más `domStorageEnabled` y User-Agent compatible con Google Sign-In (sin bloqueo `403 disallowed_useragent`).
- **Notificaciones nativas Android**: puente JS (`AntigravityNativeBridge`) que intercepta `window.Notification` y entrega notificaciones nativas limpias en el canal *Antigravity Remote* (silenciables en 1 toque).
- **Ergonomía móvil + Edge-to-Edge**: ajuste automático con teclado (`adjustResize` + `imePadding`), viewport táctil optimizado, soporte de selector de archivos y manejo de popups OAuth dentro de la misma sesión.
- **Control flotante minimalista y ajustes**: barra flotante colapsable (Atrás, Recargar, Inicio `/remote`, Ajustes) con indicador online/offline en vivo, modo escritorio opcional, mantener pantalla encendida, selector de tema (Oscuro/Claro/Sistema), limpiar caché sin cerrar sesión y cierre de sesión explícito.
- **Calidad validada con `anvil v0.3.0`**: grafo Kotlin (`anvil-graph`), Tier 0 AST, compresor de Gradle (`anvil run`) y QA móvil (`anvil qa scan-mobile`).

## Compilar APK

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

El APK queda en `app\build\outputs\apk\debug\app-debug.apk`.
