package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1234
    }

    private lateinit var navController: NavController
    var bgm: MediaPlayer = MediaPlayer()
    var permissionFlag = false

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

        // 位置情報の利用許可
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionFlag = true
        } else {
            // 許可を求めるダイアログを表示
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }

        // BGMの設定
        bgm = MediaPlayer.create(this, R.raw.bgm)
        bgm.isLooping = true    // ループ再生をON
        bgm.start()             // BGMを再生
    }

    fun setButton(startButton: ImageView, flag: Boolean) {
        startButton.setImageResource(
            if(flag) {
                R.drawable.pause_button
            } else {
                R.drawable.start_button
            })
    }

    // 位置情報の利用許可の結果を確認
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_CODE) {
            permissionFlag = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }
}