package com.example.microredesocialbruno.model

import android.graphics.Bitmap
import com.google.firebase.Timestamp

data class Post(
    val descricao: String,
    val imagem: Bitmap,
    val cidade: String = "",
    val autor: String = "",
    val data: Timestamp = Timestamp.now()
)