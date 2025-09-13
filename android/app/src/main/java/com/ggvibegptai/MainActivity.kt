package com.ggvibegptai

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wv = findViewById<WebView>(R.id.webview)
        wv.settings.javaScriptEnabled = true
        wv.webViewClient = WebViewClient() // keep navigation inside app
        wv.loadUrl("https://ggvibe-gpt-all-1-ai.org") // <--- change if needed
    }
}
