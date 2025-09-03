package com.example.momapp

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.momapp.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommunityFragment : Fragment() {
    private lateinit var adapter: PostAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewPosts)

        if (!::adapter.isInitialized) {
            val query = db.collection("posts").orderBy("createdAt", Query.Direction.DESCENDING)
            val options = FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post::class.java)
                .setLifecycleOwner(viewLifecycleOwner)
                .build()
            adapter = PostAdapter(options)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null  // 애니메이션 제거

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddPost)
        fab.setOnClickListener { showPostDialog() }

        return view
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun showPostDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_post, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etContent = dialogView.findViewById<EditText>(R.id.etContent)

        AlertDialog.Builder(requireContext())
            .setTitle("게시글 작성")
            .setView(dialogView)
            .setPositiveButton("등록") { _, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()
                val user = FirebaseAuth.getInstance().currentUser
                val authorname = when {
                    !user?.displayName.isNullOrBlank() -> user.displayName!!
                    !user?.email.isNullOrBlank() -> user.email!!
                    else -> "사용자"
                }

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(context, "제목과 내용을 모두 입력하세요", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val post = hashMapOf(
                    "title" to title,
                    "content" to content,
                    "author" to authorname,
                    "uid" to user?.uid,
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )

                db.collection("posts").add(post)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "게시글이 등록되었습니다", Toast.LENGTH_SHORT).show()

                        // 알림 설정 여부 확인
                        val alarmOn = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
                            .getBoolean("alarm_on", true) // 기본값은 알림 켜짐

                        if (alarmOn) {
                            val helper = MyNotificationHelper(requireContext())
                            helper.showNotification("새로운 글이 있어요!", "$title 글이 등록되었습니다.")
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
