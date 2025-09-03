package com.example.momapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.momapp.model.Post

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(post: Post) {
        itemView.findViewById<TextView>(R.id.tvTitle).text = post.title
        itemView.findViewById<TextView>(R.id.tvAuthor).text = "작성자: ${post.author}"
    }
}
