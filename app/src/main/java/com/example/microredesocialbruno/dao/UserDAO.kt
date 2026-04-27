package com.example.microredesocialbruno.dao

import com.example.microredesocialbruno.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserDAO {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun salvarUsuario(
        user: User,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .document(user.email)
            .set(user)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun buscarUsuario(
        email: String,
        onSucesso: (User) -> Unit,
        onErro: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) onSucesso(user)
                }
            }
            .addOnFailureListener { erro -> onErro(erro) }
    }

    fun alterarSenha(
        senhaAtual: String,
        novaSenha: String,
        onSucesso: () -> Unit,
        onErro: (Exception) -> Unit
    ) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return

        val credencial = EmailAuthProvider.getCredential(email, senhaAtual)
        user.reauthenticate(credencial)
            .addOnSuccessListener {
                user.updatePassword(novaSenha)
                    .addOnSuccessListener { onSucesso() }
                    .addOnFailureListener { erro -> onErro(erro) }
            }
            .addOnFailureListener { erro -> onErro(erro) }
    }
}