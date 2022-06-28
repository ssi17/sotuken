package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentInformationBinding
import com.example.myapplication.model.MainViewModel
import com.example.myapplication.network.Content
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

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

        if (sharedViewModel.contents.size == 0) {
            setTopTitle()
        } else {
            setInformationTitle()
            setRecyclerView()
        }
        setButton()
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

    fun pushButton() {
        sharedViewModel.changeStartFlag()
        setButton()

        if (sharedViewModel.startFlag) {
            if (sharedViewModel.contents.size == 0) {
                binding!!.topTitle.setImageDrawable(null)
                binding!!.startText.text = null
            }
            setInformationTitle()
            setRecyclerView()
            getContents()
        }
    }

    private fun setRecyclerView() {
        recyclerView = binding!!.recyclerView
        recyclerView.adapter =
            RecyclerAdapter(sharedViewModel.contents)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun getContents() {
        val assetManager = resources.assets
        val file = assetManager.open("Contents.json")
        val br = BufferedReader(InputStreamReader(file))
        val jsonArray = JSONArray(br.readText())
        sharedViewModel.getContents(jsonArray, "那覇市")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}