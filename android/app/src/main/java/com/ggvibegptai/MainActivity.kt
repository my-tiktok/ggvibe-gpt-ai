package com.ggvibegptai

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var errorView: LinearLayout

    private val localWelcome = "file:///android_asset/index.html"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progress = findViewById(R.id.progress)
        errorView = findViewById(R.id.errorView)
        val btnRetry: Button = findViewById(R.id.btnRetry)

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            loadsImagesAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            mediaPlaybackRequiresUserGesture = false
        }

        webView.webViewClient = object : WebViewClient() {

            // Block “SQL-like” domains and file types
            private fun isSqlLike(u: Uri?): Boolean {
                if (u == null) return false
                val scheme = (u.scheme ?: "").lowercase()
                if (scheme !in listOf("http","https","file")) return true  // block odd schemes

                val host = (u.host ?: "").lowercase()
                val path = (u.path ?: "").lowercase()

                // hosts that *look* SQL-related
                val hostBadFragments = listOf(
                    "sql", "mysql", "mssql", "postgres", "postgresql", "oracle", "sqlite"
                )
                if (hostBadFragments.any { host.contains(it) }) return true

                // file extensions that look like DB/SQL dumps
                val badExt = listOf(".sql", ".sqlite", ".sqlite3", ".db", ".db3", ".dump", ".bak")
                if (badExt.any { path.endsWith(it) }) return true

                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url
                return if (isSqlLike(url)) {
                    showLoading(false); showError(true)
                    true // block
                } else {
                    false // allow
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showLoading(true); showError(false)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                showLoading(false)
            }
            override fun onReceivedError(view: WebView?, req: WebResourceRequest?, err: WebResourceError?) {
                showLoading(false); showError(true)
            }
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                showLoading(false); showError(true); handler?.cancel()
            }
        }

        btnRetry.setOnClickListener { reload() }
        reload()
    }

    private fun startUrl(): String {
        // Use the URL from the launch intent if present; else show local welcome page
        return intent?.dataString?.takeIf { it.isNotBlank() } ?: localWelcome
    }

    private fun reload() {
        val url = startUrl()
        if (isOnline() || url.startsWith("file://")) {
            showError(false)
            webView.loadUrl(url)
        } else {
            showLoading(false); showError(true)
        }
    }

    private fun showLoading(show: Boolean) {
        progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean) {
        errorView.visibility = if (show) View.VISIBLE else View.GONE
        webView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(ConnectivityManager::class.java)
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override fun onBackPressed() {
        if (this::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
