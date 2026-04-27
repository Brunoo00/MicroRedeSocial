package com.example.microredesocialbruno.dao

import android.graphics.Bitmap
import com.example.microredesocialbruno.helper.Base64Converter
import com.example.microredesocialbruno.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostDAO {
    private val db = Firebase.firestore
    private var ultimoTimestamp: Timestamp? = null

    fun resetar() {
        ultimoTimestamp = null
    }

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
                    val posts = documentos.mapNotNull { document ->
                        documentToPost(document)
                    }
                    onSucesso(posts)
                } else {
                    onSucesso(emptyList())
                }
            }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun buscarPostsPorCidade(
        cidade: String,
        onSucesso: (List<Post>) -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("posts")
            .whereEqualTo("cidade", cidade)
            .get()
            .addOnSuccessListener { documentos ->
                if (!documentos.isEmpty) {
                    val posts = documentos.mapNotNull { document ->
                        documentToPost(document)
                    }
                    onSucesso(posts)
                } else {
                    onSucesso(emptyList())
                }
            }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun salvarPost(
        imageString: String,
        descricao: String,
        autorEmail: String,
        cidade: String,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        val post = hashMapOf(
            "imageString" to imageString,
            "descricao" to descricao,
            "autor" to autorEmail,
            "cidade" to cidade,
            "data" to Timestamp.now()
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    private fun documentToPost(document: DocumentSnapshot): Post? {
        return try {
            val imageString = document.getString("imageString") ?: ""
            val bitmap: Bitmap = if (imageString.isNotEmpty()) {
                Base64Converter.stringToBitmap(imageString)
            } else {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            }
            val descricao = document.getString("descricao") ?: ""
            val cidade = document.getString("cidade") ?: ""
            val autor = document.getString("autor") ?: ""
            val data = document.getTimestamp("data") ?: Timestamp.now()
            Post(descricao, bitmap, cidade, autor, data)
        } catch (e: Exception) {
            null
        }
    }
}