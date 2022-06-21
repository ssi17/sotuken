package com.example.myapplication.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.R

class MainViewModel: ViewModel() {

    var startFlag = false
    var images: List<Int>? = null
    var titles: List<Int>? = null
    var describes: List<Int>? = null
    var favoriteFlag: List<Boolean>? = null

    private val _bgmFlag = MutableLiveData<Boolean>()
    val bgmFlag: LiveData<Boolean> = _bgmFlag
    private val _touristSightFlag = MutableLiveData<Boolean>()
    val touristSightFlag: LiveData<Boolean> = _touristSightFlag
    private val _restaurantFlag = MutableLiveData<Boolean>()
    val restaurantFlag: LiveData<Boolean> = _restaurantFlag
    private val _historyFlag = MutableLiveData<Boolean>()
    val historyFlag: LiveData<Boolean> = _historyFlag
    private val _triviaFlag = MutableLiveData<Boolean>()
    val triviaFlag: LiveData<Boolean> = _triviaFlag

    init {
        _bgmFlag.value = true
        _touristSightFlag.value = false
        _restaurantFlag.value = false
        _historyFlag.value = true
        _triviaFlag.value = true
    }

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

    fun switchBgmFlag() {
        _bgmFlag.value = !_bgmFlag.value!!
    }

    fun switchTouristSightFlag() {
        _touristSightFlag.value = !_touristSightFlag.value!!
    }

    fun switchRestaurantFlag() {
        _restaurantFlag.value = !_restaurantFlag.value!!
    }

    fun switchHistoryFlag() {
        _historyFlag.value = !_historyFlag.value!!
    }

    fun switchTriviaFlag() {
        _triviaFlag.value = !_triviaFlag.value!!
    }
}