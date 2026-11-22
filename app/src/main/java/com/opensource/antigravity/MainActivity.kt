package com.opensource.antigravity

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.opensource.antigravity.data.AppPreferencesRepository
import com.opensource.antigravity.data.AppSettings
import com.opensource.antigravity.data.NetworkMonitor
import com.opensource.antigravity.notifications.NotificationHelper
import com.opensource.antigravity.ui.components.CosmicProgressBar
import com.opensource.antigravity.ui.components.ErrorStateView
import com.opensource.antigravity.ui.components.FloatingControlDock
import com.opensource.antigravity.ui.components.SettingsSheet
import com.opensource.antigravity.ui.components.SettingsSheetActions
import com.opensource.antigravity.ui.components.SplashOverlay
import com.opensource.antigravity.ui.theme.AntigravityTheme
import com.opensource.antigravity.ui.theme.LocalAntigravityPalette
import com.opensource.antigravity.webview.AntigravityJsBridge
import com.opensource.antigravity.webview.AntigravityUrlPolicy
import com.opensource.antigravity.webview.AntigravityWebView
import com.opensource.antigravity.webview.SessionManager
import com.opensource.antigravity.webview.WebViewCallbacks
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferencesRepository
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var jsBridge: AntigravityJsBridge

    private var pendingFileCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        pendingFileCallback?.onReceiveValue(uris.toTypedArray())
        pendingFileCallback = null
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* Permission state is queried dynamically on notify */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        preferences = AppPreferencesRepository(applicationContext)
        networkMonitor = NetworkMonitor(applicationContext)
        notificationHelper = NotificationHelper(applicationContext).apply { ensureChannel() }
        jsBridge = AntigravityJsBridge(notificationHelper)

        requestNotificationPermissionIfNeeded()

        setContent {
            val settings by preferences.settings.collectAsState(initial = AppSettings())
            val isOnline by networkMonitor.isOnline.collectAsState(
                initial = networkMonitor.isCurrentlyOnline(),
            )

            LaunchedEffect(settings.keepScreenOn) {
                if (settings.keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            AntigravityTheme(themeMode = settings.themeMode) {
                AntigravityRemoteRoot(
                    settings = settings,
                    isOnline = isOnline,
                    preferences = preferences,
                    jsBridge = jsBridge,
                    onRequestNotificationPermission = ::requestNotificationPermissionIfNeeded,
                    onLaunchFileChooser = { callback ->
                        pendingFileCallback?.onReceiveValue(emptyArray())
                        pendingFileCallback = callback
                        fileChooserLauncher.launch(arrayOf("*/*"))
                    },
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        SessionManager.flush()
    }

    override fun onStop() {
        super.onStop()
        SessionManager.flush()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationHelper.hasNotificationPermission()
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun AntigravityRemoteRoot(
    settings: AppSettings,
    isOnline: Boolean,
    preferences: AppPreferencesRepository,
    jsBridge: AntigravityJsBridge,
    onRequestNotificationPermission: () -> Unit,
    onLaunchFileChooser: (ValueCallback<Array<Uri>>) -> Unit,
) {
    val palette = LocalAntigravityPalette.current
    val scope = rememberCoroutineScope()

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }
    var showSplash by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2_600L)
        showSplash = false
    }

    LaunchedEffect(isOnline) {
        if (isOnline && errorMessage != null) {
            errorMessage = null
            webViewRef?.reload()
        }
    }

    BackHandler(enabled = canGoBack) {
        webViewRef?.goBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        AntigravityWebView(
            settings = settings,
            jsBridge = jsBridge,
            callbacks = WebViewCallbacks(
                onWebViewReady = { webViewRef = it },
                onProgressChanged = { progress = it },
                onNavigationStateChanged = { url, back ->
                    canGoBack = back
                    if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) {
                        scope.launch { preferences.setLastVisitedUrl(url) }
                    }
                },
                onPageFirstPaint = {
                    errorMessage = null
                    showSplash = false
                },
                onMainFrameError = { desc ->
                    showSplash = false
                    errorMessage = desc
                },
                onFileChooser = onLaunchFileChooser,
            ),
            modifier = Modifier.fillMaxSize(),
        )

        CosmicProgressBar(
            progress = progress,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        errorMessage?.let { msg ->
            ErrorStateView(
                message = msg,
                onRetry = {
                    errorMessage = null
                    webViewRef?.reload()
                },
            )
        }

        FloatingControlDock(
            isOnline = isOnline,
            canGoBack = canGoBack,
            onBack = { webViewRef?.goBack() },
            onReload = {
                errorMessage = null
                webViewRef?.reload()
            },
            onHome = {
                errorMessage = null
                webViewRef?.loadUrl(AntigravityUrlPolicy.REMOTE_HOME_URL)
            },
            onOpenSettings = { showSettings = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 10.dp),
        )

        SplashOverlay(visible = showSplash)
    }

    if (showSettings) {
        SettingsSheet(
            settings = settings,
            isOnline = isOnline,
            actions = SettingsSheetActions(
                onDismiss = { showSettings = false },
                onToggleNotifications = { enabled ->
                    if (enabled) onRequestNotificationPermission()
                    scope.launch { preferences.setNotificationsEnabled(enabled) }
                },
                onToggleMobileOptimization = { enabled ->
                    scope.launch { preferences.setMobileOptimization(enabled) }
                },
                onToggleDesktopMode = { enabled ->
                    scope.launch { preferences.setDesktopMode(enabled) }
                },
                onToggleKeepScreenOn = { enabled ->
                    scope.launch { preferences.setKeepScreenOn(enabled) }
                },
                onSelectTheme = { mode ->
                    scope.launch { preferences.setThemeMode(mode) }
                },
                onClearCacheOnly = {
                    webViewRef?.let(SessionManager::clearCachePreservingSession)
                    showSettings = false
                },
                onSignOut = {
                    webViewRef?.let { view ->
                        SessionManager.signOut(view) {
                            scope.launch {
                                preferences.setLastVisitedUrl(AntigravityUrlPolicy.REMOTE_HOME_URL)
                            }
                        }
                    }
                    showSettings = false
                },
            ),
        )
    }
}
