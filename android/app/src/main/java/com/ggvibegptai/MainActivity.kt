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
        wv.webViewClient = WebViewClient() // keep navigation in-app
        wv.loadUrl("https://YOUR-SQUARESPACE-DOMAIN.com/") // <-- put your URL here
    }
}
