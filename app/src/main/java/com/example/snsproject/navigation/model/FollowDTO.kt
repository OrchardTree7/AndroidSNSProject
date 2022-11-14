package com.example.snsproject.navigation.model

data class FollowDTO(
    var followerCount : Int = 0,
    // 중복 방지 위한 map
    var followers : MutableMap<String,Boolean> = HashMap(),

    var followingCount : Int = 0,
    var followings : MutableMap<String,Boolean> = HashMap()
)
