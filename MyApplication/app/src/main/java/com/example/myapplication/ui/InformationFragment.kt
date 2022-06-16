package com.example.myapplication.ui

import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentInformationBinding
import com.example.myapplication.model.MainViewModel

class InformationFragment: Fragment() {

    private var binding: FragmentInformationBinding? = null
    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView

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

        if(sharedViewModel.startFlag) {
            setRecyclerView()
        } else {
            if(sharedViewModel.images == null) {
                setTopTitle()
            }
            binding!!.startButton.setImageResource(R.drawable.ic_start_48)
        }
        setButton()
    }

    private fun setTopTitle() {
        binding!!.topTitle.setImageResource(R.drawable.top_title)
        binding!!.startText.setText(R.string.start_text)
    }

    fun setButton() {
        binding!!.startButton.setImageResource(
            if(sharedViewModel.startFlag) {
                R.drawable.ic_pause_48
            } else {
                R.drawable.ic_start_48
            })
    }

    fun pushButton() {
        sharedViewModel.changeStartFlag()
        setButton()

        if(sharedViewModel.startFlag) {
            if(sharedViewModel.images == null) {
                binding!!.topTitle.setImageDrawable(null)
                binding!!.startText.text = null
            }
            binding!!.informationTitle.setText(R.string.information_title)
            setRecyclerView()
        }
    }

    private fun setRecyclerView() {
        getInformation()
        recyclerView = binding!!.recyclerView
        recyclerView.adapter = RecyclerAdapter(sharedViewModel.images, sharedViewModel.favoriteFlag, sharedViewModel.titles, sharedViewModel.describes)
        recyclerView.layoutManager = LinearLayoutManager(MainActivity())
    }

    private fun getInformation() {
        sharedViewModel.getImages()
        sharedViewModel.getFavoriteFlag()
        sharedViewModel.getTitles()
        sharedViewModel.getDescribes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}