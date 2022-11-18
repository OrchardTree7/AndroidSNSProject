package com.example.snsproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snsproject.databinding.ActivityAddAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AddAccountActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddAccountBinding.inflate(layoutInflater) }
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val emailSignInBtn = binding.emailSignInBtn

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
        auth.signInWithEmailAndPassword(
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
            finish()
        }
    }
}