package com.example.snsproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snsproject.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val auth = FirebaseAuth.getInstance()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val emailSignInBtn = binding.emailSignInBtn
        val googleSignInBtn = binding.googleSignInBtn

        emailSignInBtn.setOnClickListener {
            signInAndUp()
        }
    }

    private fun signInAndUp() {
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText
        auth.createUserWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 유저 계정 생성
                moveToMain(task.result.user)
            } else if (task.exception?.message.isNullOrEmpty()) {
                // 에러 메세지 코드
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            } else {
                // 계정이 이미 존재하면, 로그인
                signInAsEmail()
            }
        }
    }

    private fun signInAsEmail() {
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText
        auth.createUserWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 로그인 성공
                moveToMain(task.result.user)
            } else {
                // 로그인 실패
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveToMain(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


}