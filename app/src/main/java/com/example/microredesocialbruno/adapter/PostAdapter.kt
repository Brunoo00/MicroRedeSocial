package com.example.microredesocialbruno.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.microredesocialbruno.databinding.PostItemBinding
import com.example.microredesocialbruno.model.Post

class PostAdapter(
    private val posts: Array<Post>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class ViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.imgPost.setImageBitmap(post.imagem)
            binding.txtDescricao.text = post.descricao
        }
    }
}