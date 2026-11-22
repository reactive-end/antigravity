package com.opensource.antigravity

import android.Manifest
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.opensource.antigravity.data.NetworkMonitor
import com.opensource.antigravity.data.SessionStore
import com.opensource.antigravity.notifications.NotificationHelper
import com.opensource.antigravity.webview.AlwaysActiveWebView
import com.opensource.antigravity.webview.AntigravityJsBridge
import com.opensource.antigravity.webview.AntigravityWebViewFactory
import com.opensource.antigravity.webview.SessionManager
import com.opensource.antigravity.webview.WebViewCallbacks
import com.opensource.antigravity.webview.WebViewHost
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var sessionStore: SessionStore
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var jsBridge: AntigravityJsBridge

    private var webView: AlwaysActiveWebView? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var splashContainer: View
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorTextView: TextView

    private var pendingFileCallback: ValueCallback<Array<Uri>>? = null
    private var hasError = false

    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val view = webView
            if (view != null && view.canGoBack()) {
                view.goBack()
            }
        }
    }

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        pendingFileCallback?.onReceiveValue(uris.toTypedArray())
        pendingFileCallback = null
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* Checked dynamically when posting notifications */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        sessionStore = SessionStore(applicationContext)
        networkMonitor = NetworkMonitor(applicationContext)
        notificationHelper = NotificationHelper(applicationContext).apply { ensureChannels() }
        jsBridge = AntigravityJsBridge(notificationHelper)

        onBackPressedDispatcher.addCallback(this, backCallback)
        requestNotificationPermissionIfNeeded()

        val root = buildNativeHierarchy()
        setContentView(root)

        observeNetworkRecovery()
    }

    override fun onStart() {
        super.onStart()
        WebViewHost.setBackgroundState(false)
    }

    override fun onPause() {
        super.onPause()
        SessionManager.flush()
    }

    override fun onStop() {
        super.onStop()
        SessionManager.flush()
        WebViewHost.setBackgroundState(true)
    }

    override fun onDestroy() {
        WebViewHost.detachFromActivity(applicationContext)
        webView = null
        super.onDestroy()
    }

    private fun buildNativeHierarchy(): FrameLayout {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime(),
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(2),
                Gravity.TOP,
            )
        }

        splashContainer = buildSplashOverlay()
        errorContainer = buildErrorOverlay()

        val acquiredWebView = AntigravityWebViewFactory.acquireOrCreate(
            activityContext = this,
            initialUrl = sessionStore.getLastVisitedUrl(),
            jsBridge = jsBridge,
            callbacks = WebViewCallbacks(
                onProgressChanged = { p ->
                    if (p in 1..99) {
                        progressBar.visibility = View.VISIBLE
                        progressBar.progress = p
                    } else {
                        progressBar.visibility = View.GONE
                    }
                },
                onNavigationStateChanged = { url, canGoBack ->
                    backCallback.isEnabled = canGoBack
                    sessionStore.saveLastVisitedUrl(url)
                },
                onPageFirstPaint = {
                    hasError = false
                    errorContainer.visibility = View.GONE
                    hideSplashFast()
                },
                onMainFrameError = { desc ->
                    hasError = true
                    hideSplashFast()
                    errorTextView.text = desc
                    errorContainer.visibility = View.VISIBLE
                },
                onFileChooser = { callback ->
                    pendingFileCallback?.onReceiveValue(emptyArray())
                    pendingFileCallback = callback
                    fileChooserLauncher.launch(arrayOf("*/*"))
                },
            ),
        )
        webView = acquiredWebView

        root.addView(acquiredWebView)
        root.addView(progressBar)
        root.addView(errorContainer)
        root.addView(splashContainer)

        root.postDelayed({ hideSplashFast() }, 1_800L)
        return root
    }

    private fun buildSplashOverlay(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.BLACK)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )

            val logo = ImageView(context).apply {
                setImageResource(R.drawable.antigravity_logo_official)
                layoutParams = LinearLayout.LayoutParams(dp(80), dp(80))
            }
            val title = TextView(context).apply {
                text = "Antigravity"
                setTextColor(Color.parseColor("#F3F4F6"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                setPadding(0, dp(16), 0, 0)
            }
            addView(logo)
            addView(title)
        }
    }

    private fun buildErrorOverlay(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE
            setBackgroundColor(Color.BLACK)
            setPadding(dp(32), dp(32), dp(32), dp(32))
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )

            val title = TextView(context).apply {
                text = "Conexión interrumpida"
                setTextColor(Color.parseColor("#F3F4F6"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                gravity = Gravity.CENTER
            }
            errorTextView = TextView(context).apply {
                text = "No se pudo conectar con Antigravity Remote."
                setTextColor(Color.parseColor("#9CA3AF"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                gravity = Gravity.CENTER
                setPadding(0, dp(8), 0, dp(24))
            }
            val retryBtn = Button(context).apply {
                text = "Reintentar"
                isAllCaps = false
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#4C8BF5"))
                    cornerRadius = dp(20).toFloat()
                }
                setPadding(dp(28), dp(10), dp(28), dp(10))
                setOnClickListener {
                    hasError = false
                    visibility = View.GONE
                    webView?.reload()
                }
            }
            addView(title)
            addView(errorTextView)
            addView(retryBtn)
        }
    }

    private fun hideSplashFast() {
        if (splashContainer.visibility == View.GONE) return
        splashContainer.animate()
            .alpha(0f)
            .setDuration(180L)
            .withEndAction { splashContainer.visibility = View.GONE }
            .start()
    }

    private fun observeNetworkRecovery() {
        lifecycleScope.launch {
            networkMonitor.isOnline.collectLatest { online ->
                if (online && hasError) {
                    hasError = false
                    errorContainer.visibility = View.GONE
                    webView?.reload()
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationHelper.hasNotificationPermission()
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
