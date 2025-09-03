package com.example.momapp.model

import com.google.firebase.Timestamp

data class Post(
    val title: String = "",
    val content: String = "",
    val author: String = "",
    val uid: String = "",
    val createdAt: Timestamp? = null
)
