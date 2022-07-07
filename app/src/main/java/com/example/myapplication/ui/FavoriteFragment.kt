package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentFavoriteBinding
import com.example.myapplication.model.MainViewModel

class FavoriteFragment: Fragment() {

    private var binding: FragmentFavoriteBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.favoriteFragment = this

        setButton()
    }

    private fun setButton() {
        requireActivity().let {
            if(it is MainActivity) {
                it.setButton(binding!!.startButton, sharedViewModel.startFlag)
            }
        }
    }

    fun pushButton() {
        sharedViewModel.changeStartFlag()
        setButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}