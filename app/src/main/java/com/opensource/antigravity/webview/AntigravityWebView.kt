package com.opensource.antigravity.webview

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.opensource.antigravity.data.AppSettings

data class WebViewCallbacks(
    val onWebViewReady: (WebView) -> Unit,
    val onProgressChanged: (Int) -> Unit,
    val onNavigationStateChanged: (url: String, canGoBack: Boolean) -> Unit,
    val onPageFirstPaint: () -> Unit,
    val onMainFrameError: (description: String) -> Unit,
    val onFileChooser: (ValueCallback<Array<Uri>>) -> Unit,
)

@Composable
fun AntigravityWebView(
    settings: AppSettings,
    jsBridge: AntigravityJsBridge,
    callbacks: WebViewCallbacks,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val initialUrl = remember {
        settings.lastVisitedUrl.takeIf { AntigravityUrlPolicy.isAntigravityWorkspaceUrl(it) }
            ?: AntigravityUrlPolicy.REMOTE_HOME_URL
    }

    val webView = remember {
        createConfiguredWebView(
            context = context,
            initialSettings = settings,
            jsBridge = jsBridge,
            callbacks = callbacks,
            initialUrl = initialUrl,
        )
    }

    DisposableEffect(webView) {
        callbacks.onWebViewReady(webView)
        onDispose {
            SessionManager.flush()
            webView.destroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { webView },
        update = { view ->
            jsBridge.setNotificationsEnabled(settings.notificationsEnabled)
            applyDynamicSettings(view, settings)
        },
    )
}

private fun createConfiguredWebView(
    context: android.content.Context,
    initialSettings: AppSettings,
    jsBridge: AntigravityJsBridge,
    callbacks: WebViewCallbacks,
    initialUrl: String,
): WebView {
    return WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        setBackgroundColor(Color.BLACK)
        overScrollMode = WebView.OVER_SCROLL_NEVER

        SessionManager.configurePersistentCookies(this)

        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(initialSettings.desktopMode)
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            userAgentString = AntigravityUrlPolicy.sanitizeUserAgent(
                defaultUserAgent = userAgentString,
                desktopMode = initialSettings.desktopMode,
            )
        }

        addJavascriptInterface(jsBridge, AntigravityJsBridge.BRIDGE_NAME)

        webViewClient = buildWebViewClient(initialSettings, callbacks)
        webChromeClient = buildWebChromeClient(context, callbacks)

        loadUrl(initialUrl)
    }
}

private fun applyDynamicSettings(webView: WebView, appSettings: AppSettings) {
    val targetUa = AntigravityUrlPolicy.sanitizeUserAgent(
        defaultUserAgent = WebSettings.getDefaultUserAgent(webView.context),
        desktopMode = appSettings.desktopMode,
    )
    if (webView.settings.userAgentString != targetUa) {
        webView.settings.userAgentString = targetUa
        webView.settings.setSupportZoom(appSettings.desktopMode)
        webView.reload()
    }
    val script = MobileBridgeScript.build(
        mobileOptimization = appSettings.mobileOptimization && !appSettings.desktopMode,
        notificationsEnabled = appSettings.notificationsEnabled,
    )
    webView.evaluateJavascript(script, null)
}

private fun buildWebViewClient(
    settingsSnapshot: AppSettings,
    callbacks: WebViewCallbacks,
): WebViewClient = object : WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest,
    ): Boolean {
        val url = request.url.toString()
        if (AntigravityUrlPolicy.shouldLoadInApp(url)) {
            return false
        }
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, request.url).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            view.context.startActivity(intent)
        }
        return true
    }

    override fun onPageCommitVisible(view: WebView, url: String) {
        super.onPageCommitVisible(view, url)
        callbacks.onPageFirstPaint()
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        SessionManager.flush()
        callbacks.onPageFirstPaint()
        callbacks.onNavigationStateChanged(url, view.canGoBack())
        if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) {
            val script = MobileBridgeScript.build(
                mobileOptimization = settingsSnapshot.mobileOptimization && !settingsSnapshot.desktopMode,
                notificationsEnabled = settingsSnapshot.notificationsEnabled,
            )
            view.evaluateJavascript(script, null)
        }
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError,
    ) {
        super.onReceivedError(view, request, error)
        if (request.isForMainFrame) {
            val desc = error.description?.toString()?.takeIf { it.isNotBlank() }
                ?: "No se pudo conectar con Antigravity Remote."
            callbacks.onMainFrameError(desc)
        }
    }
}

private fun buildWebChromeClient(
    context: android.content.Context,
    callbacks: WebViewCallbacks,
): WebChromeClient = object : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        callbacks.onProgressChanged(newProgress)
        if (newProgress >= 85) {
            callbacks.onPageFirstPaint()
        }
    }

    override fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?,
    ): Boolean {
        val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
        val popupProxy = WebView(context).apply {
            SessionManager.configurePersistentCookies(this)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    popupView: WebView,
                    request: WebResourceRequest,
                ): Boolean {
                    val targetUrl = request.url.toString()
                    if (AntigravityUrlPolicy.shouldLoadInApp(targetUrl)) {
                        view.loadUrl(targetUrl)
                    } else {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, request.url)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            )
                        }
                    }
                    popupView.destroy()
                    return true
                }
            }
        }
        transport.webView = popupProxy
        resultMsg.sendToTarget()
        return true
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?,
    ): Boolean {
        val cb = filePathCallback ?: return false
        callbacks.onFileChooser(cb)
        return true
    }
}
