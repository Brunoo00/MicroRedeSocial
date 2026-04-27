package com.example.microredesocialbruno.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
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

        binding.btnLogout.setOnClickListener { logout() }

        binding.btnPerfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnNovoPost.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        binding.btnCarregarFeed.setOnClickListener {
            limparFeed()
            carregarFeed()
        }

        binding.btnBuscar.setOnClickListener {
            buscarPorCidade()
        }

        binding.edtBuscarCidade.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarPorCidade()
                true
            } else {
                false
            }
        }
    }

    private fun configurarRecyclerView() {
        adapter = PostAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = this@HomeActivity.adapter
        }
    }

    private fun limparFeed() {
        postDAO.resetar()
        adapter = PostAdapter()
        binding.recyclerView.adapter = adapter
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

    private fun buscarPorCidade() {
        val cidade = binding.edtBuscarCidade.text.toString()

        if (cidade.isEmpty()) {
            Toast.makeText(this, "Digite o nome de uma cidade", Toast.LENGTH_SHORT).show()
            return
        }

        limparFeed()

        postDAO.buscarPostsPorCidade(
            cidade = cidade,
            onSucesso = { posts ->
                if (posts.isEmpty()) {
                    Toast.makeText(this, "Nenhum post encontrado para \"$cidade\"", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.adicionarPosts(posts)
                }
            },
            onErro = { erro ->
                Log.e("HomeActivity", "Erro na busca: ${erro.message}")
                Toast.makeText(this, "Erro ao buscar posts", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun logout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}