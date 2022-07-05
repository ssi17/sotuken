package com.example.myapplication.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.network.ContentsApi
import kotlinx.coroutines.launch
import android.content.res.AssetManager
import com.example.myapplication.json.Article
import com.example.myapplication.json.Content
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

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
    // アーティクルのリスト
    var articles: MutableList<Article> = mutableListOf()

    // 設定画面のスイッチの初期値
    init {
        _bgmFlag.value = true
        _touristSightFlag.value = false
        _restaurantFlag.value = false
        _historyFlag.value = true
        _triviaFlag.value = true
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

    fun getContents(contentsArray: JSONArray, articlesArray: JSONArray, city: String) {
        // JSONを扱うためのクラス
        val moshi1 = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        // JSONのデータを格納するクラスを指定
        val adapter1 = moshi1.adapter(Content::class.java)

        contents = mutableListOf()
        articles = mutableListOf()

        val articleIds: MutableSet<Int> = mutableSetOf()

        for(i in 0 until contentsArray.length()) {
            // 市町村名を取得
            val cityName = contentsArray.getJSONObject(i).getString("city").toString()
            if(cityName == city) {
                if(when(contentsArray.getJSONObject(i).getString("category").toString()) {
                        "観光スポット" -> _touristSightFlag.value!!
                        "飲食店" -> _restaurantFlag.value!!
                        "歴史" -> _historyFlag.value!!
                        else -> _triviaFlag.value!!
                }) {
                    // Contentインスタンスを作成し、リストに保存
                    val obj = adapter1.fromJson(contentsArray.getJSONObject(i).toString()) as Content
                    contents.add(obj)

                    // アーティクルIDを取得
                    for(id in obj.articleId) {
                        articleIds.add(id)
                    }
                }
            }
        }

        val moshi2 = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter2 = moshi2.adapter(Article::class.java)
        for(i in 0 until articlesArray.length()) {
            val id = articlesArray.getJSONObject(i).getInt("id")
            if(articleIds.contains(id)) {
                articles.add(adapter2.fromJson(articlesArray.getJSONObject(i).toString()) as Article)
            }
        }
        Log.d("debug_json", "articles.size:${articles.size}")
    }
}