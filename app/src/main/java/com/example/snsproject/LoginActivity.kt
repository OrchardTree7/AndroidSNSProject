package com.example.snsproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snsproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val emailSignInBtn = binding.emailSignInBtn
        val emailSignUpBtn = binding.emailSignUpBtn

        emailSignInBtn.setOnClickListener {
            signInAsEmail()
        }

        emailSignUpBtn.setOnClickListener {
            val intent = Intent(this, AddAccountActivity::class.java)
            startActivity(intent)
        }
    }

//    override fun onStart() { // 자동 로그인
//        super.onStart()
//        moveToMain(auth.currentUser)
//    }

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