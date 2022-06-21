package com.example.myapplication.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.SubActivity

class RecyclerAdapter(
    private val images: List<Int>?,
    private val favoriteFlag:List<Boolean>?,
    private val titles: List<Int>?,
    private val describes: List<Int>?,
    private val pages: List<String>?
    ): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // サムネイル画像を設定
        holder.images.setImageResource(images?.get(position) as Int)
        // お気に入り登録ボタンの設定
        if(favoriteFlag?.get(position) == true) {
            holder.favorites.setImageResource(R.drawable.favorite_button)
        } else {
            holder.favorites.setImageResource(R.drawable.not_favorite_button)
        }
        // タイトルを設定
        holder.titles.setText(titles?.get(position) as Int)
        // 説明を設定
        holder.describes.setText(describes?.get(position) as Int)

        holder.pages.setOnClickListener {
            SubActivity().openUrl(holder.pages, pages?.get(position))
        }
    }

    override fun getItemCount(): Int {
        return images?.size ?: 0
    }
}