package com.example.myapplication.model

import androidx.lifecycle.ViewModel
import com.example.myapplication.R

class MainViewModel: ViewModel() {

    var startFlag = false
    var images: List<Int>? = null
    var titles: List<Int>? = null
    var describes: List<Int>? = null
    var favoriteFlag: List<Boolean>? = null

    fun changeStartFlag() {
        startFlag = !startFlag
    }

    fun getImages() {
        images = listOf(R.drawable.syurizyo, R.drawable.tyuraumi)
    }

    fun getTitles() {
        titles = listOf(R.string.title1, R.string.title2)
    }

    fun getDescribes() {
        describes = listOf(R.string.describe1, R.string.describe2)
    }

    fun getFavoriteFlag() {
        favoriteFlag = listOf(false, false)
    }
}