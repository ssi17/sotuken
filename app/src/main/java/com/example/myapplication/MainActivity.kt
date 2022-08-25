package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.room.CoroutinesRoom
import androidx.room.Room
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.Favorite
import com.example.myapplication.json.Article
import com.example.myapplication.model.MainViewModel
import com.example.myapplication.ui.AboutFragment
import com.example.myapplication.ui.FavoriteFragment
import com.example.myapplication.ui.InformationFragment
import com.example.myapplication.ui.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Integer.parseInt
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var navController: NavController
    private val sharedViewModel: MainViewModel by viewModels()
    var bgm: MediaPlayer = MediaPlayer()
    private lateinit var tts: TextToSpeech

    @SuppressLint("RestrictedApi", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ナビゲーションの設定
        // 下部ナビゲーション
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // フラグメント間の遷移
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupWithNavController(bottomNavigation, navController)

        // BGMの設定
        bgm = MediaPlayer.create(this, R.raw.bgm)
        bgm.isLooping = true    // ループ再生をON
        bgm.start()             // BGMを再生

        // TextToSpeechの初期化
        tts = TextToSpeech(this, this)

        // JSONファイルを取得し、ViewModelへ保存
        val assetManager = resources.assets

        // Contents.json
        val contentsFile = assetManager.open("Contents.json")
        var br = BufferedReader(InputStreamReader(contentsFile))
        val contentsArray = JSONArray(br.readText())

        // Articles.json
        val articlesFile = assetManager.open("Articles.json")
        br = BufferedReader(InputStreamReader(articlesFile))
        val articlesArray = JSONArray(br.readText())

        sharedViewModel.contentsArray = contentsArray
        sharedViewModel.articlesArray = articlesArray

        // データベース
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
        sharedViewModel.db = db
//        for(i in 1 until 13) {
//            sharedViewModel.addFlag(i)
//        }
        sharedViewModel.getAllFlag()

        val menuView = bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val menuItem = menuView.getChildAt(2) as BottomNavigationItemView
        // iconのサイズを設定
//        0.until(menuView.childCount).forEach { index ->
//            val icon = menuView.getChildAt(index).findViewById<ImageView>(com.google.android.material.R.id.icon)
//            val displayMetrics = resources.displayMetrics
//            val layoutParams = (icon.layoutParams).apply {
//                width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, displayMetrics).toInt()
//                height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, displayMetrics).toInt()
//            }
//            icon.layoutParams = layoutParams
//        }
        menuItem.setOnClickListener {
            sharedViewModel.changeStartFlag()
            if(sharedViewModel.startFlag) {
                if(sharedViewModel.contents.size != 0) {
                    startSpeech()
                }
                menuItem.setIcon(resources.getDrawable(R.drawable.stop_button))
            } else {
                onPause()
                menuItem.setIcon(resources.getDrawable(R.drawable.play_button))
            }
        }
    }

    // 音声読み上げ機能
    @SuppressLint("NewApi")
    fun startSpeech() {
        if(tts.isSpeaking) {
            tts.stop()
        }

        val articles = sharedViewModel.articles
        val contents = sharedViewModel.contents

        val list: MutableList<Article> = sharedViewModel.displayArticles.value!!
        if(list.size != 0) {
            list.removeAt(0)
        }

        tts.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onDone(id: String) {
                // BGMの音量を戻す
                bgm.setVolume(1.0F, 1.0F)
                // 読み上げが終わったコンテンツのリストを更新
                sharedViewModel.doneContents.add(parseInt(id))
            }

            override fun onError(id: String) {

            }

            override fun onStart(id: String) {
                // BGMの音量を下げる
                bgm.setVolume(0.1F, 0.1F)
                // 読み上げられているコンテンツの記事を記事リストから取得
                for(content in contents) {
                    if(content.id == parseInt(id)) {
                        for(article in articles) {
                            if(content.articleId.contains(article.id)) {
                                list.add(0, article)
                            }
                        }
                        break
                    }
                }
                // 画面に表示する記事のリストを更新
                sharedViewModel.setDisplayArticles(list)
            }
        })

        for(content in sharedViewModel.contents) {
            // 読み上げが終わっているコンテンツなら処理をスキップ
            if(sharedViewModel.doneContents.contains(content.id)) {
                continue
            }
            // 音声データの取得
            val text = content.voice
            // 音声読み上げのキューに追加
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, "${content.id}")
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {
            tts.let { tts ->
                val locale = Locale.JAPAN
                if(tts.isLanguageAvailable(locale) > TextToSpeech.LANG_AVAILABLE) {
                    tts.language = Locale.JAPAN
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        tts.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    fun stopSpeech() {
        onPause()
    }

    @SuppressLint("RestrictedApi", "UseCompatLoadingForDrawables")
    fun setIcon() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val menuView = bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val menuItem = menuView.getChildAt(2) as BottomNavigationItemView

        menuItem.setIcon(resources.getDrawable(R.drawable.stop_button))
    }
}