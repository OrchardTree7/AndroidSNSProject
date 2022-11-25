package com.example.snsproject.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.snsproject.R
import com.example.snsproject.databinding.ActivityCommentBinding
import com.example.snsproject.navigation.model.AlarmDTO
import com.example.snsproject.navigation.model.ContentDTO
import com.google.api.Billing.BillingDestination
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_deail.*

class CommentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCommentBinding.inflate(layoutInflater) }
    private var contentUid: String? = null
    var destinationUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        val commentSendBtn = binding.commentSendBtn
        val commentEt = binding.commentEt
        val commentRecyclerView = binding.commentRecyclerView

        commentRecyclerView.adapter = CommentRecyclerViewAdapater()
        commentRecyclerView.layoutManager = LinearLayoutManager(this)

        commentSendBtn.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userid = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = commentEt.text.toString()
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!)
                .collection("comments").document().set(comment)
            commentAlarm(destinationUid!!, commentEt.getText().toString())
            commentEt.setText("")
        }
    }

    fun commentAlarm(destinationUid : String, message : String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.kind = 1
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.message = message
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }

    inner class CommentRecyclerViewAdapater : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()

        init {
            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if (querySnapshot == null)
                        return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = holder.itemView
            view.commentItemTv.text = comments[position].comment
            view.commentItemProfileTv.text = comments[position].userid

            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url)
                            .apply(RequestOptions().circleCrop()).into(view.commentItemImageView)
                    }
                }
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }
}