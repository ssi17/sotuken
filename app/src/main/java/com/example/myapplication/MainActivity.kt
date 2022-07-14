package com.example.myapplication

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.room.CoroutinesRoom
import androidx.room.Room
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.Favorite
import com.example.myapplication.model.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val sharedViewModel: MainViewModel by viewModels()
    var bgm: MediaPlayer = MediaPlayer()

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
        sharedViewModel.getAllFlag()
    }

    fun setButton(startButton: ImageView, flag: Boolean) {
        startButton.setImageResource(
            if(flag) {
                R.drawable.pause_button
            } else {
                R.drawable.start_button
            })
    }
}