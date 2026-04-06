package com.example.microredesocialbruno.dao

import android.graphics.Bitmap
import com.example.microredesocialbruno.Base64Converter
import com.example.microredesocialbruno.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostDAO {
    private val db = Firebase.firestore
    private var ultimoTimestamp: Timestamp? = null

    fun carregarPosts(onSucesso: (List<Post>) -> Unit, onErro: (Exception) -> Unit) {
        var query = db.collection("posts")
            .orderBy("data", Query.Direction.DESCENDING)
            .limit(5)

        if (ultimoTimestamp != null) {
            query = query.startAfter(ultimoTimestamp!!)
        }

        query.get()
            .addOnSuccessListener { documentos ->
                if (!documentos.isEmpty) {
                    ultimoTimestamp = documentos.documents.last().getTimestamp("data")
                    val posts = documentos.map { document ->
                        val imageString = document.getString("imageString") ?: ""
                        val bitmap: Bitmap = Base64Converter.stringToBitmap(imageString)
                        val descricao = document.getString("descricao") ?: ""
                        val data = document.getTimestamp("data") ?: Timestamp.now()
                        Post(descricao, bitmap, data)
                    }
                    onSucesso(posts)
                }
            }
            .addOnFailureListener { erro ->
                onErro(erro)
            }
    }

    fun salvarPost(
        imageString: String,
        descricao: String,
        autorEmail: String,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        val post = hashMapOf(
            "imageString" to imageString,
            "descricao" to descricao,
            "autor" to autorEmail,
            "data" to Timestamp.now()
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }
}