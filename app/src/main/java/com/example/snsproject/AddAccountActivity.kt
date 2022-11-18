package com.example.snsproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snsproject.databinding.ActivityAddAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Pattern

class AddAccountActivity : AppCompatActivity() {

    // 검사 정규식
    private val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private val binding by lazy { ActivityAddAccountBinding.inflate(layoutInflater) }
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                checkEmail()
            }

        })

        val emailSignInBtn = binding.emailSignInBtn

        emailSignInBtn.setOnClickListener {
            if (!checkEmail()) {
                Toast.makeText(this, "이메일을 형식에 맞게 입력하세요", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText.text.toString() == "") {
                Toast.makeText(this, "패스워드 공란 ", Toast.LENGTH_SHORT).show()
            } else {
                signInAsEmail()
            }
        }
    }

    private fun signInAndUp() {
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

    fun checkEmail(): Boolean {
        val email = emailEditText.text.toString().trim() //공백제거
        val p = Pattern.matches(emailValidation, email) // 서로 패턴이 맞닝?
        return if (p) {
            //이메일 형태가 정상일 경우
            emailEditText.setTextColor(Color.BLACK)
            true
        } else {
            emailEditText.setTextColor(Color.RED)
            false
        }
    }
}