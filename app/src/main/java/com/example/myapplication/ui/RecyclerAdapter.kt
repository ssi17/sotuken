package com.example.myapplication.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.json.Article


class RecyclerAdapter(
    private val articles: List<Article>
    ): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.item.context

        // サムネイル画像を設定
        val resId = context.resources.getIdentifier(articles[position].img, "drawable", context.packageName)
        holder.images.setImageResource(resId)

        // お気に入り登録ボタンの設定
        holder.favorites.setImageResource(R.drawable.not_favorite_button)
//        holder.favorites.setOnClickListener {
//            articles[position].flag = !articles[position].flag
//        }
//        if(articles[position].flag) {
//            holder.favorites.setImageResource(R.drawable.favorite_button)
//        } else {
//            holder.favorites.setImageResource(R.drawable.not_favorite_button)
//        }

        // タイトルを設定
        holder.titles.text = articles[position].name

        // 説明を設定
        holder.describes.text = articles[position].describe

        // ページへ
        holder.pages.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articles[position].url))
            context.startActivity(intent)
        }

        // マップを開く
        holder.maps.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articles[position].map))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}