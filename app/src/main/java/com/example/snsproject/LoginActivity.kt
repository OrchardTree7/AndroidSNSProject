package com.example.snsproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snsproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    // 검사 정규식
    private val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
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
        val emailSignUpBtn = binding.emailSignUpBtn

        emailSignInBtn.setOnClickListener {
            if (emailEditText.text.toString() == "") {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (!checkEmail()) {
                Toast.makeText(this, "이메일을 형식에 맞게 입력하세요", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText.text.toString() == "") {
                Toast.makeText(this, "패스워드를 입력해주세요 ", Toast.LENGTH_SHORT).show()
            } else {
                signInAsEmail()
            }
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
        val email = emailEditText.text.toString().trim()
        val p = Pattern.matches(emailValidation, email)
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