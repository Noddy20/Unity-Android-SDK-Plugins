package com.arupakaman.pluginwebview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.webkit.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout

class WebViewActivity : AppCompatActivity() {

    companion object{

        private const val KEY_EXTRA_TITLE = "titleExtra"
        private const val KEY_EXTRA_URL = "urlExtra"
        private const val KEY_EXTRA_IS_TOOLBAR_ENABLED = "toolbarEnabled"
        private const val KEY_EXTRA_SCREEN_ORIENTATION = "screenOrientation"
        private const val KEY_EXTRA_IS_REFRESH_ENABLED = "urlIsRefreshEnabled"
        private const val KEY_EXTRA_IS_JAVA_SCRIPT_ENABLED = "jsEnabled"

        @Keep
        @JvmStatic
        @JvmOverloads
        fun startWebViewActivity(
            mContext: Context,
            title: String,                                      // Toolbar Title
            url: String,
            isToolbarEnabled: Boolean = true,                   // Show/Hide Toolbar
            screenOrientation: Int = -1,                        // WebView Orientation : 0 = LANDSCAPE , 1 = PORTRAIT
            isRefreshEnabled: Boolean = true,                   // Show/Hide Refresh button in toolbar
            isJsEnabled: Boolean = true,                        // Enables JavaScript in WebView
        ) {
            Log.d("WebViewAct", "startWebViewActivity launch $title $url $isToolbarEnabled $screenOrientation $isRefreshEnabled $isJsEnabled")
            with(mContext) {
                startActivity(
                    Intent(this, WebViewActivity::class.java).apply {
                        putExtra(KEY_EXTRA_TITLE, title)
                        putExtra(KEY_EXTRA_URL, url)
                        putExtra(KEY_EXTRA_IS_TOOLBAR_ENABLED, isToolbarEnabled)
                        putExtra(KEY_EXTRA_SCREEN_ORIENTATION, screenOrientation)
                        putExtra(KEY_EXTRA_IS_REFRESH_ENABLED, isRefreshEnabled)
                        putExtra(KEY_EXTRA_IS_JAVA_SCRIPT_ENABLED, isJsEnabled)
                    }
                )
            }
        }
    }

    private var toolbarTitle: String? = ""
    private var webUrl: String? = ""
    private var isToolbarEnabled = true
    private var isRefreshEnabled = true
    private var isJsEnabled = true

    private var refreshMenuItem: MenuItem? = null
    private lateinit var webView: WebView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvErrorMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webView = findViewById(R.id.webView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        progressBar = findViewById(R.id.progressBar)
        tvErrorMsg = findViewById(R.id.tvErrorMsg)

        intent?.let {
            toolbarTitle = it.getStringExtra(KEY_EXTRA_TITLE)
            webUrl = it.getStringExtra(KEY_EXTRA_URL)
            isToolbarEnabled = it.getBooleanExtra(KEY_EXTRA_IS_TOOLBAR_ENABLED, true)
            isRefreshEnabled = it.getBooleanExtra(KEY_EXTRA_IS_REFRESH_ENABLED, true)
            isJsEnabled = it.getBooleanExtra(KEY_EXTRA_IS_JAVA_SCRIPT_ENABLED, true)

            if (webUrl.isNullOrBlank() && intent.data != null){
                //Handle deep link here
                isToolbarEnabled = false       // hide toolbar because we don't have a title
                webUrl = intent.data?.toString()
            }

            val screenOr = it.getIntExtra(KEY_EXTRA_SCREEN_ORIENTATION, 1)
            if (screenOr == 0 || screenOr == 1) {             // 0 = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE , 1 = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                @SuppressLint("SourceLockedOrientationActivity")
                requestedOrientation = screenOr
            }
        }
        Log.d("WebViewAct", "startWebViewActivity onCreate $title $webUrl $isToolbarEnabled $isRefreshEnabled $isJsEnabled")

        if (webUrl?.trim().isNullOrEmpty()){
            finish()
        }else {
            init(savedInstanceState)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        //Save WebView State
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        //Go back on webpage
        if (webView.canGoBack()){
            webView.goBack()
        }else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_web_view, menu)

        refreshMenuItem = menu?.findItem(R.id.menuItemRefresh)
        refreshMenuItem?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            isVisible = isRefreshEnabled
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //close webView
            finish()
        }
        if (item.itemId == R.id.menuItemRefresh) {
            webView.reload()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init(savedInstanceState: Bundle?){
        if (isToolbarEnabled) {
            setSupportActionBar(findViewById(R.id.toolbar))
            supportActionBar?.apply {
                title = toolbarTitle ?: ""
                //setHomeAsUpIndicator(R.drawable.ic_close)
                setDisplayHomeAsUpEnabled(true)
            }
        }else{
            findViewById<AppBarLayout>(R.id.appBar).isVisible = false
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            webView.reload()
        }

        val onScrollChangedListener = ViewTreeObserver.OnScrollChangedListener{
            swipeRefreshLayout.isEnabled = (webView.scrollY == 0 && isRefreshEnabled)
        }
        swipeRefreshLayout.viewTreeObserver.addOnScrollChangedListener(onScrollChangedListener)

        webView.webViewLoad(savedInstanceState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.webViewLoad(savedInstanceState: Bundle?){
        if (webUrl?.endsWith(".pdf", true) == true){
            webUrl = "https://docs.google.com/gview?embedded=true&url=$webUrl"
        }
        requestFocus()
        settings.apply {
            setSupportZoom(true)
            javaScriptEnabled = isJsEnabled

            //builtInZoomControls = true
            //loadWithOverviewMode = true
            //useWideViewPort = true

            databaseEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            setGeolocationEnabled(true)
            displayZoomControls = false

            @Suppress("DEPRECATION")
            run{
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
            }

            displayZoomControls = false
            javaScriptCanOpenWindowsAutomatically = false
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = 2
            }
        }

        scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        isScrollbarFadingEnabled = true

        webViewClient = object : WebViewClient(){

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.isVisible = true
                tvErrorMsg.isVisible = false
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.isVisible = false
                tvErrorMsg.isVisible = false
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                progressBar.isVisible = false
                tvErrorMsg.isVisible = true
                super.onReceivedError(view, request, error)
            }

        }

        webChromeClient = WebChromeClient()

        if (savedInstanceState == null)
            loadUrl(webUrl ?: "")
        else
            restoreState(savedInstanceState)       //Restore saved WebView State
    }

}