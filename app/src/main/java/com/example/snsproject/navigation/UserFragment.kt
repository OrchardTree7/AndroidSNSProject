package com.example.snsproject.navigation

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.snsproject.LoginActivity
import com.example.snsproject.MainActivity
import com.example.snsproject.R
import com.example.snsproject.navigation.model.AlarmDTO
import com.example.snsproject.navigation.model.ContentDTO
import com.example.snsproject.navigation.model.FollowDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.view.*


class UserFragment : Fragment() {
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var dUid: String? = null
    var userId : String? = null
    var auth: FirebaseAuth? = null
    var currentUid: String? = null

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        dUid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUid = FirebaseAuth.getInstance().uid
        userId = arguments?.getString("userId")

        var mainactivity = (activity as MainActivity)

        if (currentUid == dUid) {
            // 나의 유저 페이지
            mainactivity.toolbar_title_image?.visibility = View.VISIBLE
            mainactivity.toolbar_username?.visibility = View.INVISIBLE
            mainactivity.toolbar_btn_back?.visibility = View.INVISIBLE

            fragmentView?.account_btn_follow_signout?.text = activity?.getText(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                auth?.signOut()
                activity?.finish()
                startActivity(Intent(activity, LoginActivity::class.java))

            }
        } else {
            // 상대방 유저 페이지
            mainactivity.toolbar_title_image?.visibility = View.INVISIBLE
            mainactivity.toolbar_username?.visibility = View.VISIBLE
            mainactivity.toolbar_btn_back?.visibility = View.VISIBLE

            mainactivity.toolbar_username?.text = userId
            mainactivity.toolbar_btn_back?.setOnClickListener {
                mainactivity.bottom_navigation.selectedItemId = R.id.action_home
            }
            fragmentView?.account_btn_follow_signout?.text = activity?.getText(R.string.follow)

            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                requestFollow()
            }
        }
        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(requireActivity(), 3)

        fragmentView?.account_iv_profile?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }
        getProfile()
        getFollowerAndFollwing()
        return fragmentView
    }

    fun getFollowerAndFollwing() {
        firestore?.collection("users")?.document(dUid!!)?.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            var followDTO = value.toObject(FollowDTO::class.java)
            if (followDTO?.followingCount != null) {
                fragmentView?.account_tv_following_count?.text = followDTO.followingCount.toString()
            }

            if (followDTO?.followerCount != null) {
                fragmentView?.account_tv_follower_count?.text = followDTO.followerCount.toString()

                if(currentUid == dUid)
                    return@addSnapshotListener

                if(followDTO.followers.containsKey(currentUid))
                    fragmentView?.account_btn_follow_signout?.text = activity?.getText(R.string.follow_cancel)
                else
                    fragmentView?.account_btn_follow_signout?.text = activity?.getText(R.string.follow)
            }

        }
    }

    fun requestFollow() {
        // 내 계정에서 상대가 누구를 팔로우하는지
        var tsDocFollowing = firestore?.collection("users")?.document(currentUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[dUid!!] = true
                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }
            else if (followDTO.followings.containsKey(dUid)) {
                // 팔로워 한 상태. 팔로잉 취소
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followings.remove(dUid)
            } else {
                // 팔로워 하지 않은 상태. 팔로잉
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followings[dUid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        var tsDocFollower = firestore?.collection("users")?.document(dUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUid!!] = true
                followerAlarm(dUid!!)

                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            else if (followDTO!!.followers.containsKey(currentUid)) {
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUid!!)
            } else {
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers?.set(currentUid!!, true)
                followerAlarm(dUid!!)
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }

    fun followerAlarm(destinationUid: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = auth?.currentUser?.email
        alarmDTO.uid = auth?.currentUser?.uid
        alarmDTO.kind = 2
        alarmDTO.timestamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }

    fun getProfile() {
        firestore?.collection("profileImages")?.document(dUid!!)
            ?.addSnapshotListener { value, error ->
                if (value == null) return@addSnapshotListener

                if (value.data != null) {
                    var url = value.data!!["image"]
                    Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop())
                        .into(fragmentView?.account_iv_profile!!)
                }
            }
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.whereEqualTo("uid", dUid)
                ?.addSnapshotListener { querySnapshot, _ ->
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var imageview = (p0 as CustomViewHolder).imageview
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageview)
        }
    }
}
