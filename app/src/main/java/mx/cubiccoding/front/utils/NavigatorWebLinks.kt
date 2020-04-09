package mx.cubiccoding.front.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_navigator_weblinks.*
import mx.cubiccoding.R

class NavigatorWebLinks : AppCompatActivity() {

    companion object {
        const val PARAM_WEB_URL = "param.web.url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigator_weblinks)
        setupToolbar()
        setupWeb()
    }

    private fun setupToolbar() {
        toolbar?.title = ""
        setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.title = "Web"
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWeb() {
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progress?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress?.visibility = View.GONE
            }
        }

        val url = intent.getStringExtra(PARAM_WEB_URL)
        webView?.loadUrl(url)
    }
}