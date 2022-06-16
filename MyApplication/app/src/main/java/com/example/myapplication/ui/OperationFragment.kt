package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentOperationBinding
import com.example.myapplication.model.MainViewModel

class OperationFragment: Fragment() {

    private var binding: FragmentOperationBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentOperationBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.operationFragment = this

        InformationFragment().setButton()
    }

    fun pushButton() {
        sharedViewModel.changeStartFlag()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}