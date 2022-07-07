package com.example.myapplication.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentInformationBinding
import com.example.myapplication.model.MainViewModel
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import com.google.android.gms.location.*

class InformationFragment: Fragment() {

    private var binding: FragmentInformationBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView

    // 位置情報関係の変数
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var requestingLocationUpdates: Boolean = false

    // 現在地の市町村名を保持
    private var currentLocation = ""

    //位置情報使用の権限許可を確認
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 使用が許可された
            requestingLocationUpdates = true
        } else {
            // それでも拒否された時の対応
            val toast = Toast.makeText(requireActivity(),
                "これ以上なにもできません", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentInformationBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.informationFragment = this

        // 再生状態かどうかを判断
        if (sharedViewModel.contents.size == 0 && !sharedViewModel.startFlag) {
            setTopTitle()
        } else {
            setInformationTitle()
            getContents(currentLocation)
        }

        // 再生ボタンに画像を設定
        setButton()

        //位置情報の権限許可
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestingLocationUpdates = true
        }

        locationRequest = LocationRequest.create()
        locationRequest.setPriority(
            LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setFastestInterval(30000).interval = 30000

        //位置情報に変更があったら呼び出される
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for(location in locationResult.locations) {
                    val str1 = "Latitude:" + location.latitude
                    val str2 = "Longitude:" + location.longitude
                    Log.d("debug_location", "緯度:$str1, 経度:$str2")

                    //address取得
                    getAddress(location.latitude, location.longitude)
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun setButton() {
        requireActivity().let {
            if (it is MainActivity) {
                it.setButton(binding!!.startButton, sharedViewModel.startFlag)
            }
        }
    }

    private fun setTopTitle() {
        binding!!.topTitle.setImageResource(R.drawable.top_title)
        binding!!.startText.setText(R.string.start_text)
    }

    private fun setInformationTitle() {
        binding!!.informationTitle.setImageResource(R.drawable.information)
    }

    // 再生ボタンが押されたときに呼び出される
    fun pushButton() {
        // 再生・停止を切り替え
        sharedViewModel.changeStartFlag()
        setButton()

        // 再生状態かどうかを判断
        if (sharedViewModel.startFlag) {
            // トップタイトルと再生ボタンの説明文の非表示にする
            if (sharedViewModel.contents.size == 0) {
                binding!!.topTitle.setImageDrawable(null)
                binding!!.startText.text = null
            }

            setInformationTitle()
            getContents("")
            setRecyclerView()
        }
    }

    // リサイクラーを設定
    private fun setRecyclerView() {
        recyclerView = binding!!.recyclerView
        val adapter = RecyclerAdapter(sharedViewModel.articles)
        recyclerView.adapter = adapter
        adapter.favoriteButton = object : RecyclerAdapter.FavoriteButton {
            override fun pushFavoriteButton(holder: ViewHolder) {
                Toast.makeText(
                    requireActivity(),
                    "${holder.titles.text}をお気に入りに登録しました",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    // JSONからコンテンツを取得
    private fun getContents(cityName: String) {
        // asset
        val assetManager = resources.assets

        // Contents.json
        val contentsFile = assetManager.open("Contents.json")
        var br = BufferedReader(InputStreamReader(contentsFile))
        val contentsArray = JSONArray(br.readText())

        // Articles.json
        val articlesFile = assetManager.open("Articles.json")
        br = BufferedReader(InputStreamReader(articlesFile))
        val articlesArray = JSONArray(br.readText())

        sharedViewModel.getContents(contentsArray, articlesArray, cityName)

        setRecyclerView()
    }

    //緯度経度をもとに住所の取得
    private fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(requireActivity())
        val address = geocoder.getFromLocation(lat, lng, 1)

        if(sharedViewModel.startFlag) {
            if(currentLocation != address[0].locality.toString()) {
                currentLocation = address[0].locality.toString()
                getContents(address[0].locality.toString())
            }
        }
        //位置情報の表示
        Log.d("debug_location", "市町村：$currentLocation")
    }

    // -----位置情報-----
    private fun startLocationUpdates() {
        Log.d("debug_location", "startLocationUpdates()")
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("debug_location", "startLocation() return")
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        Log.d("debug_location", "stopLocationUpdates()")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        Log.d("debug_location", "onResume()")
        if(requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("debug_location", "onPause()")
        stopLocationUpdates()
    }
    // -----位置情報-----

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}