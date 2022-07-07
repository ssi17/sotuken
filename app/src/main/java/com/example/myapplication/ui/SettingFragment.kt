package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentSettingBinding
import com.example.myapplication.model.MainViewModel

class SettingFragment: Fragment() {

    private var binding: FragmentSettingBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSettingBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            settingFragment = this@SettingFragment
            viewModel = sharedViewModel
        }
        setButton()
    }

    // 再生ボタンに画像を設定
    private fun setButton() {
        requireActivity().let {
            if(it is MainActivity) {
                it.setButton(binding!!.startButton, sharedViewModel.startFlag)
            }
        }
    }

    // 再生ボタンが押された時の処理
    fun pushButton() {
        sharedViewModel.changeStartFlag()
        setButton()
    }

    // BGMのON/OFF切り替え
    fun switchBgm() {
        requireActivity().let {
            if(it is MainActivity) {
                if(binding!!.soundSwitch.isChecked) {
                    it.bgm.start()
                } else {
                    it.bgm.pause()
                }
            }
        }
        sharedViewModel.switchBgmFlag()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}