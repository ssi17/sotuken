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

        //
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