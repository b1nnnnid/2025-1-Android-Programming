package com.example.momapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.momapp.databinding.ItemRecyclerviewBinding
import com.example.momapp.model.Post
import com.example.momapp.ui.theme.PostDetailActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat
import java.util.Locale

//  기본 RecyclerView용
class MyViewHolder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: MutableList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding

        binding.itemRoot.setOnClickListener {
            Toast.makeText(it.context, "${datas[position]}이 선택되었습니다.", Toast.LENGTH_SHORT).show()
            AlertDialog.Builder(it.context).run {
                setTitle("알림")
                setIcon(android.R.drawable.ic_dialog_alert)
                setMessage("${datas[position]}이 선택되었습니다.")
                setPositiveButton("예", null)
                show()
            }
        }
    }
}

//  커뮤니티 게시글용 어댑터
class PostAdapter(options: FirestoreRecyclerOptions<Post>) :
    FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(options) {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvDate: TextView = itemView.findViewById(R.id.tvCreatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.tvTitle.text = model.title
        holder.tvAuthor.text = "작성자: ${model.author}"

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val createdAtStr = model.createdAt?.toDate()?.let { formatter.format(it) } ?: ""
        holder.tvDate.text = createdAtStr

        holder.itemView.setOnClickListener {
            Log.d("PostClick", "게시글 클릭됨: ${model.title}")
            val context = holder.itemView.context
            val intent = Intent(context, PostDetailActivity::class.java).apply {
                putExtra("title", model.title)
                putExtra("content", model.content)
                putExtra("author", model.author)
                putExtra("createdAt", createdAtStr)
            }
            context.startActivity(intent)
        }
    }
}
