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

    // 設定画面のスイッチの状態を保持
    // BGM
    private val _bgmFlag = MutableLiveData<Boolean>()
    val bgmFlag: LiveData<Boolean> = _bgmFlag
    // 観光スポット
    private val _touristSightFlag = MutableLiveData<Boolean>()
    val touristSightFlag: LiveData<Boolean> = _touristSightFlag
    // 飲食店
    private val _restaurantFlag = MutableLiveData<Boolean>()
    val restaurantFlag: LiveData<Boolean> = _restaurantFlag
    // 歴史
    private val _historyFlag = MutableLiveData<Boolean>()
    val historyFlag: LiveData<Boolean> = _historyFlag
    // 雑学
    private val _triviaFlag = MutableLiveData<Boolean>()
    val triviaFlag: LiveData<Boolean> = _triviaFlag

    // コンテンツのリスト
    var contents: MutableList<Content> = mutableListOf()

    // 設定画面のスイッチの初期値
    init {
        _bgmFlag.value = true
        _touristSightFlag.value = false
        _restaurantFlag.value = false
        _historyFlag.value = true
        _triviaFlag.value = true
    }

    // JSONファイルを読み取り、contentsへ格納する
    fun getContents(jsonArray: JSONArray, city: String) {

        // JSONを扱うためのクラス
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        // JSONのデータを格納するクラスを指定
        val adapter = moshi.adapter(Content::class.java)

        // コンテンツリストの初期化
        contents = mutableListOf()

        for(i in 0 until jsonArray.length()) {
            // JSONファイルの要素から市町村を取得
            val cityName = jsonArray.getJSONObject(i).getString("city").toString()
            // 引数で受け取った市町村と一致するか判断
            if (cityName == city) {
                // 設定画面のスイッチの状態によりコンテンツリストに追加するかを判断
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

    // 情報発信のON/OFF
    fun changeStartFlag() {
        startFlag = !startFlag
    }

    // BGMのON/OFF
    fun switchBgmFlag() {
        _bgmFlag.value = !_bgmFlag.value!!
    }

    // 観光スポットのON/OFF
    fun switchTouristSightFlag() {
        _touristSightFlag.value = !_touristSightFlag.value!!
    }

    // 飲食店のON/OFF
    fun switchRestaurantFlag() {
        _restaurantFlag.value = !_restaurantFlag.value!!
    }

    // 歴史のON/OFF
    fun switchHistoryFlag() {
        _historyFlag.value = !_historyFlag.value!!
    }

    // 雑学のON/OFF
    fun switchTriviaFlag() {
        _triviaFlag.value = !_triviaFlag.value!!
    }
}