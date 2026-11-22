package com.opensource.antigravity.webview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

data class WebViewCallbacks(
    val onProgressChanged: (Int) -> Unit,
    val onNavigationStateChanged: (url: String, canGoBack: Boolean) -> Unit,
    val onPageFirstPaint: () -> Unit,
    val onMainFrameError: (description: String) -> Unit,
    val onFileChooser: (ValueCallback<Array<Uri>>) -> Unit,
)

object AntigravityWebViewFactory {

    fun acquireOrCreate(
        activityContext: Context,
        initialUrl: String,
        jsBridge: AntigravityJsBridge,
        callbacks: WebViewCallbacks,
    ): AlwaysActiveWebView {
        return WebViewHost.acquire(activityContext) { hostContext ->
            createConfiguredWebView(
                context = hostContext,
                initialUrl = initialUrl,
                jsBridge = jsBridge,
                callbacks = callbacks,
            )
        }.also { existing ->
            existing.webViewClient = buildWebViewClient(callbacks)
            existing.webChromeClient = buildWebChromeClient(activityContext, callbacks)
            if (existing.url != null) {
                callbacks.onPageFirstPaint()
            }
        }
    }

    private fun createConfiguredWebView(
        context: Context,
        initialUrl: String,
        jsBridge: AntigravityJsBridge,
        callbacks: WebViewCallbacks,
    ): AlwaysActiveWebView {
        return AlwaysActiveWebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            setBackgroundColor(Color.BLACK)
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            isFocusable = true
            isFocusableInTouchMode = true

            SessionManager.configurePersistentCookies(this)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = false
                displayZoomControls = false
                setSupportZoom(false)
                setGeolocationEnabled(false)
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                userAgentString = AntigravityUrlPolicy.sanitizeUserAgent(userAgentString)
            }

            addJavascriptInterface(jsBridge, AntigravityJsBridge.BRIDGE_NAME)

            webViewClient = buildWebViewClient(callbacks)
            webChromeClient = buildWebChromeClient(context, callbacks)

            loadUrl(initialUrl)
        }
    }

    private fun buildWebViewClient(callbacks: WebViewCallbacks): WebViewClient =
        object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
            ): Boolean {
                val url = request.url.toString()
                if (url.startsWith("intent://", ignoreCase = true)) {
                    val fallback = AntigravityUrlPolicy.extractIntentFallbackUrl(url)
                    if (fallback != null && (AntigravityUrlPolicy.shouldLoadInApp(fallback) || !request.hasGesture())) {
                        view.loadUrl(fallback)
                        return true
                    }
                }
                if (AntigravityUrlPolicy.shouldLoadInApp(url)) {
                    return false
                }
                if (!request.hasGesture() && AntigravityUrlPolicy.isHttpOrHttps(url)) {
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

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) {
                    view.evaluateJavascript(MobileBridgeScript.INJECT_SCRIPT, null)
                }
            }

            override fun onPageCommitVisible(view: WebView, url: String) {
                super.onPageCommitVisible(view, url)
                if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) {
                    view.evaluateJavascript(MobileBridgeScript.INJECT_SCRIPT, null)
                }
                callbacks.onPageFirstPaint()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                SessionManager.flush()
                callbacks.onPageFirstPaint()
                callbacks.onNavigationStateChanged(url, view.canGoBack())
                if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) {
                    view.evaluateJavascript(MobileBridgeScript.INJECT_SCRIPT, null)
                    view.postDelayed({
                        view.evaluateJavascript(MobileBridgeScript.INJECT_SCRIPT, null)
                    }, 800L)
                }
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) {
                    view.evaluateJavascript(MobileBridgeScript.INJECT_SCRIPT, null)
                    view.postDelayed({
                        view.evaluateJavascript(MobileBridgeScript.INJECT_SCRIPT, null)
                    }, 350L)
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
        context: Context,
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
                        if (targetUrl.isBlank() || targetUrl == "about:blank") {
                            return false
                        }
                        val resolved = AntigravityUrlPolicy.extractIntentFallbackUrl(targetUrl) ?: targetUrl
                        if (AntigravityUrlPolicy.shouldLoadInApp(resolved) ||
                            (!request.hasGesture() && AntigravityUrlPolicy.isHttpOrHttps(resolved))
                        ) {
                            view.loadUrl(resolved)
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
}
