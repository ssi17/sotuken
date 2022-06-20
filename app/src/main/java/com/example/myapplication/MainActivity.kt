package com.example.myapplication

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    var bgm: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

//        setupActionBarWithNavController(navController)
        setupWithNavController(bottomNavigation, navController)

        bgm = MediaPlayer.create(this, R.raw.bgm)
        bgm.isLooping = true
        bgm.start()
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