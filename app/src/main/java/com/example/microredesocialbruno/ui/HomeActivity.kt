package com.example.microredesocialbruno.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.microredesocialbruno.adapter.PostAdapter
import com.example.microredesocialbruno.dao.PostDAO
import com.example.microredesocialbruno.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var adapter: PostAdapter
    private val postDAO = PostDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        configurarRecyclerView()

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnPerfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnCarregarFeed.setOnClickListener {
            carregarFeed()
        }
    }

    private fun configurarRecyclerView() {
        adapter = PostAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = this@HomeActivity.adapter
        }
    }

    private fun carregarFeed() {
        postDAO.carregarPosts(
            onSucesso = { posts ->
                if (posts.isEmpty()) {
                    Toast.makeText(this, "Nenhum post encontrado", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.adicionarPosts(posts)
                }
            },
            onErro = { erro ->
                Log.e("HomeActivity", "Erro ao carregar posts: ${erro.message}")
                Toast.makeText(this, "Erro ao carregar posts", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun logout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}