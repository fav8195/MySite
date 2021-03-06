package by.fav8195.mysiteubel

import android.annotation.TargetApi
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //private lateinit var appBarConfiguration: AppBarConfiguration//?
    //-fav
    var networkAvailable = false
    lateinit var mWebView : WebView
    lateinit var drawerLayout: DrawerLayout
    //fav-

    //При создании
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //fav
        var url = getString(R.string.website_url)
        //адрес страницы ОС
        val urlFeedback = getString(R.string.website_urlFeedBack)

        mWebView = findViewById(R.id.webView)
        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(false)

        loadWebSite(mWebView , url, applicationContext)

        //делаем цветной свайп
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRed, R.color.colorBlue, R.color.colorGreen )
        swipeRefreshLayout.apply {
            setOnRefreshListener {
                if (mWebView.url != null) url = mWebView.url
                loadWebSite(mWebView, url, applicationContext)
            }

            setOnChildScrollUpCallback { parent, child ->  mWebView.getScrollY() > 0}
        }
/*
        //простое открытие УРЛа в вебВью
        val url = getString(R.string.website_url)
        webView.webViewClient= WebViewClient()
        webView.loadUrl(url)
*/
        //fav-


        val fab: FloatingActionButton = findViewById(R.id.fab)

        //-fav
        //Слушатель кнопки обратной связи
        fab.setOnClickListener {
            loadWebSite(mWebView,urlFeedback,applicationContext)
        }

        //-fav


/*
        //Исходный слушатель кнопки ОС, открывающий снек панель снизу
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Типа улетело письмо... ;)", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
*/

        //-fav
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)//переобъявлена выше
        drawerLayout = findViewById(R.id.drawer_layout)//привязка файла макета
        val navView: NavigationView = findViewById(R.id.nav_view)

        //гамбургер
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) //создание переключателя
        //связка переключателя с панелью навигации
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        //fav-

    }

/*
    public fun onClickButtSett() {
        val urlSett = getString(R.string.website_urlFeedBack)
        loadWebSite(mWebView, urlSett,applicationContext )
    }
*/

    //Загрузка страницы
    private fun loadWebSite(mWebView: WebView, url: String, context: Context) {

        progressBar.visibility = View.VISIBLE
        networkAvailable = isNetworkAvailable(context)
        mWebView.clearCache(true)
        if (networkAvailable) {
            wvVisible(mWebView)
            mWebView.loadUrl(url)
            mWebView.webViewClient = MyWebViewClient()
        } else {
            wvGone(mWebView)
        }
    }

    //-fav
    //Видим вебВью (когда есть сеть)
    private fun wvVisible (mWebView : WebView){
        mWebView.visibility = View.VISIBLE
        tvCheckConnector.visibility = View.GONE
    }

    //Скрываем вебВью (когда нет сети)
    private fun wvGone (mWebView : WebView){
        mWebView.visibility = View.GONE
        tvCheckConnector.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    //Остановка и скрытие прогресс бара
    private fun onLoadComplete() {
        swipeRefreshLayout.isRefreshing = false
        progressBar.visibility = View.GONE
    }

    //Проверка наличия соединения
    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context) : Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT > 22) {
                val an = cm.activeNetwork ?: return false
                val capabilities = cm.getNetworkCapabilities(an) ?: return false
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val a = cm.activeNetworkInfo ?: return false
                a.isConnected && (a.type == ConnectivityManager.TYPE_WIFI || a.type == ConnectivityManager.TYPE_MOBILE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false

    }

    //внутренний класс
    private inner class MyWebViewClient : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.N)
        //Перехват стандартной загрузки УРЛа
        override fun shouldOverrideUrlLoading(view: WebView?,request: WebResourceRequest?): Boolean {

            val url = request?.url.toString()
            return urlOverride(url)
        }

        //Перехват стандартной загрузки УРЛа
        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
            return urlOverride(url)
        }

        //Переопределяем, чтобы в приложении загружались тока наши УРЛы - остальные - отправляем на стандартные
        private fun urlOverride(url: String): Boolean {
            progressBar.visibility = View.VISIBLE
            networkAvailable = isNetworkAvailable(applicationContext)

            if (networkAvailable) {
                if ((Uri.parse(url).host == getString(R.string.website_domain)) || (Uri.parse(url).host == getString(R.string.website_domain_personalArea))) return false
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                onLoadComplete()
                return true
            } else {
                wvGone(webView)
                return false
            }
        }

        //Переропределяем обработчик ошибок соединения с сервером
        @Suppress("DEPRECATION")
        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?,failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            //Если код ошибки =0 - нет ссоединения, другие ошибки (другие коды) не переропределяем
            if (errorCode == 0) {
                view?.visibility = View.GONE
                tvCheckConnector.visibility = View.VISIBLE
                onLoadComplete()
            }
        }

        //Перевызов той же функции (выше) из старой версии
        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            onReceivedError(view, error!!.errorCode, error.description.toString(), request!!.url.toString())
        }

        //Переопределяем функцию окончания загрузки
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onLoadComplete()
        }
    }
    //При создании меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> {
                val url = getString(R.string.website_url)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_cat -> {
                val url = getString(R.string.website_cat)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_partner -> {
                val url = getString(R.string.website_menu_partner)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_personalArea -> {
                val url = getString(R.string.website_personalArea)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_info -> {
                val url = getString(R.string.website_info)
                loadWebSite(mWebView, url, applicationContext)
            }
            R.id.nav_about -> {
                val url = getString(R.string.website_about)
                loadWebSite(mWebView, url, applicationContext)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    //fav-


/*
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
*/
}
