package com.opensource.antigravity

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.opensource.antigravity.data.NetworkMonitor
import com.opensource.antigravity.data.SessionStore
import com.opensource.antigravity.notifications.AntigravityKeepAliveService
import com.opensource.antigravity.notifications.NotificationHelper
import com.opensource.antigravity.ui.components.CosmicProgressBar
import com.opensource.antigravity.ui.components.ErrorStateView
import com.opensource.antigravity.ui.components.SplashOverlay
import com.opensource.antigravity.ui.theme.AntigravityColors
import com.opensource.antigravity.ui.theme.AntigravityTheme
import com.opensource.antigravity.webview.AntigravityJsBridge
import com.opensource.antigravity.webview.AntigravityUrlPolicy
import com.opensource.antigravity.webview.AntigravityWebView
import com.opensource.antigravity.webview.SessionManager
import com.opensource.antigravity.webview.WebViewCallbacks
import com.opensource.antigravity.webview.WebViewHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var sessionStore: SessionStore
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
    ) { /* Permission state checked dynamically when posting notifications */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        sessionStore = SessionStore(applicationContext)
        networkMonitor = NetworkMonitor(applicationContext)
        notificationHelper = NotificationHelper(applicationContext).apply { ensureChannels() }
        jsBridge = AntigravityJsBridge(notificationHelper)

        requestNotificationPermissionIfNeeded()
        AntigravityKeepAliveService.start(applicationContext)

        setContent {
            val isOnline by networkMonitor.isOnline.collectAsState(
                initial = networkMonitor.isCurrentlyOnline(),
            )
            var initialUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                initialUrl = sessionStore.getLastVisitedUrl()
            }

            AntigravityTheme {
                AntigravityRemoteRoot(
                    initialUrl = initialUrl,
                    isOnline = isOnline,
                    sessionStore = sessionStore,
                    jsBridge = jsBridge,
                    onLaunchFileChooser = { callback ->
                        pendingFileCallback?.onReceiveValue(emptyArray())
                        pendingFileCallback = callback
                        fileChooserLauncher.launch(arrayOf("*/*"))
                    },
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        WebViewHost.isInBackground = false
        WebViewHost.keepAliveTick()
    }

    override fun onPause() {
        super.onPause()
        SessionManager.flush()
    }

    override fun onStop() {
        super.onStop()
        WebViewHost.isInBackground = true
        SessionManager.flush()
        WebViewHost.keepAliveTick()
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
    initialUrl: String?,
    isOnline: Boolean,
    sessionStore: SessionStore,
    jsBridge: AntigravityJsBridge,
    onLaunchFileChooser: (ValueCallback<Array<Uri>>) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }
    var showSplash by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(2_400L)
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
            .background(AntigravityColors.Canvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        if (initialUrl != null) {
            AntigravityWebView(
                initialUrl = initialUrl,
                jsBridge = jsBridge,
                callbacks = WebViewCallbacks(
                    onWebViewReady = { webViewRef = it },
                    onProgressChanged = { progress = it },
                    onNavigationStateChanged = { url, back ->
                        canGoBack = back
                        if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) {
                            scope.launch { sessionStore.saveLastVisitedUrl(url) }
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
        }

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

        SplashOverlay(visible = showSplash)
    }
}
