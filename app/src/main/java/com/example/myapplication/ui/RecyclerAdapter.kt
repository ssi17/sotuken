package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class RecyclerAdapter(private val images: List<Int>?, private val favoriteFlag:List<Boolean>?, private val titles: List<Int>?, private val describes: List<Int>?): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.list.text = list[position]
        holder.images?.setImageResource(images?.get(position) as Int)
        if(favoriteFlag?.get(position) == true) {
            holder.favorites.setImageResource(R.drawable.favorite_button)
        } else {
            holder.favorites.setImageResource(R.drawable.not_favorite_button)
        }
        holder.titles.setText(titles?.get(position) as Int)
        holder.describes.setText(describes?.get(position) as Int)
    }

    override fun getItemCount(): Int {
        return if(images == null) {
            0
        } else {
            images!!.size
        }
    }
}