package com.example.myapplication.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.network.Content
import com.example.myapplication.network.ContentsApi
import kotlinx.coroutines.launch
import android.content.res.AssetManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray

class MainViewModel : ViewModel() {

    var startFlag = false

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

    var contents: MutableList<Content> = mutableListOf()

    init {
        _bgmFlag.value = true
        _touristSightFlag.value = false
        _restaurantFlag.value = false
        _historyFlag.value = true
        _triviaFlag.value = true
    }

    fun getContents(jsonArray: JSONArray, city: String) {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(Content::class.java)
        for(i in 0 until jsonArray.length()) {
            val cityName = jsonArray.getJSONObject(i).getString("city").toString()
            Log.d("debug_json", "cityName = $cityName")
            if (cityName == city) {
                Log.d("debug_json", "contents.add()")
                if(when(jsonArray.getJSONObject(i).getString("category").toString()) {
                    "観光スポット" -> _touristSightFlag.value!!
                    "飲食店" -> _restaurantFlag.value!!
                    "歴史" -> _historyFlag.value!!
                    else -> _triviaFlag.value!!
                }) {
                    contents.add(adapter.fromJson(jsonArray.getJSONObject(i).toString()) as Content)
                }
            }
        }
    }

    fun changeStartFlag() {
        startFlag = !startFlag
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