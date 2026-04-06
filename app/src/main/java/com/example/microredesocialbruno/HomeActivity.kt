package com.example.microredesocialbruno

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.microredesocialbruno.adapter.PostAdapter
import com.example.microredesocialbruno.databinding.ActivityHomeBinding
import com.example.microredesocialbruno.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var posts: ArrayList<Post>
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnCarregarFeed.setOnClickListener {
            carregarFeed()
        }
    }

    private fun carregarFeed() {
        val db = Firebase.firestore
        db.collection("posts").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    posts = ArrayList()
                    for (document in task.result.documents) {
                        val imageString = document.data!!["imageString"].toString()
                        val bitmap = Base64Converter.stringToBitmap(imageString)
                        val descricao = document.data!!["descricao"].toString()
                        posts.add(Post(descricao, bitmap))
                    }
                    adapter = PostAdapter(posts.toTypedArray())
                    binding.recyclerView.apply {
                        layoutManager = LinearLayoutManager(this@HomeActivity)
                        adapter = this@HomeActivity.adapter
                    }
                }
            }
    }

    private fun logout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}