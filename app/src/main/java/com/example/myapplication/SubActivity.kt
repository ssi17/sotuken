package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity

class SubActivity: Activity() {

    fun openUrl(view: View, url: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch(e: Exception) {
            Log.d("debug", "$e")
        }
    }
}