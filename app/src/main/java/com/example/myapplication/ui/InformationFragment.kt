package com.example.myapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentInformationBinding
import com.example.myapplication.json.Article
import com.example.myapplication.model.MainViewModel
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.w3c.dom.Text
import java.util.*

class InformationFragment: Fragment() {

    private var binding: FragmentInformationBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView

    // 位置情報関係の変数
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var requestingLocationUpdates: Boolean = false

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
        binding?.lifecycleOwner = viewLifecycleOwner

        // 再生状態なら現在地をもとにコンテンツを取得
        // 再生状態でなければTopTitleを表示
        if (sharedViewModel.displayArticles.value!!.size == 0 && !sharedViewModel.startFlag) {
            binding!!.topTitle.setImageResource(R.drawable.top_title)
            binding!!.startText.setText(R.string.start_text)
        } else {
            setInformationTitle()
        }

        if(sharedViewModel.startFlag) {
            requireActivity().let {
                if(it is MainActivity) {
                    it.setIcon()
                }
            }
        }

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
                    //address取得
                    getAddress(location.latitude, location.longitude)
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        sharedViewModel.displayArticles.observe(viewLifecycleOwner) { it ->
            setRecyclerView(it)
            if(it.size != 0) {
                binding!!.topTitle.setImageDrawable(null)
                binding!!.startText.text = null
                setInformationTitle()
            }
        }
    }

    private fun setInformationTitle() {
        binding!!.informationTitle.setImageResource(R.drawable.header_title_information)
    }

    // 再生ボタンが押されたときに呼び出される
    fun pushButton() {
        // 再生・停止を切り替え
        sharedViewModel.changeStartFlag()

        // 再生状態かどうかを判断
        if (sharedViewModel.startFlag) {
            // トップタイトルと再生ボタンの説明文の非表示にする


            setInformationTitle()

            requireActivity().let {
                if(it is MainActivity) {
                    it.startSpeech()
                }
            }
        } else {
            requireActivity().let {
                if(it is MainActivity) {
                    it.stopSpeech()
                }
            }
        }
    }

    // リサイクラーを設定
    private fun setRecyclerView(articles: List<Article>) {
        recyclerView = binding!!.recyclerView
        val adapter = RecyclerAdapter(articles, sharedViewModel.flagList)
        recyclerView.adapter = adapter
        // お気に入り登録ボタンが押された時の処理
        adapter.favoriteButton = object : RecyclerAdapter.FavoriteButton {
            override fun pushFavoriteButton(id: Int) {
                sharedViewModel.changeFavoriteFlag(id)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    //緯度経度をもとに住所の取得
    private fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(requireActivity())
        val address = geocoder.getFromLocation(lat, lng, 1)

        val city = address[0].locality.toString()

        if(sharedViewModel.city != city) {
            sharedViewModel.city = city
            sharedViewModel.getContents()
            if(sharedViewModel.startFlag) {
                requireActivity().let {
                    if(it is MainActivity) {
                        it.startSpeech()
                    }
                }
            }
        }
    }

    // -----位置情報-----
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if(requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
    // -----位置情報-----

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}