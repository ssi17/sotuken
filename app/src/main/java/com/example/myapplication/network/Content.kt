package com.example.myapplication.network

data class Content (
    val id: Int,
    val city: String,
    val title: String,
    val describe: String,
    val img: String,
    val url: String,
    val category: String,
    var flag: Boolean
        )