package com.example.momapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.momapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import android.view.View
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash", "keyHash: $keyHash")

        // 구글 로그인 콜백 등록
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                MyApplication.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { loginTask ->
                        if (loginTask.isSuccessful) {
                            MyApplication.email = account.email
                            Log.d("login", "구글 로그인 성공")
                            onLoginSuccess()
                        } else {
                            Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(this, "구글 로그인 오류", Toast.LENGTH_SHORT).show()
            }
        }

        // 이메일 로그인
        binding.loginBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            if(email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            MyApplication.email = email
                            Log.d("login", "이메일 로그인 성공")
                            onLoginSuccess()
                        } else {
                            Toast.makeText(this, "이메일 인증을 완료해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.e("Login", "실패 이유: ${task.exception}")
                    }
                }
        }

        // 회원가입
        binding.signBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            if(email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        MyApplication.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { sendTask ->
                            if (sendTask.isSuccessful) {
                                Toast.makeText(this, "가입 성공! 이메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
                                changeVisibility("login")
                            } else {
                                Toast.makeText(this, "이메일 전송 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        Log.e("Signin", "실패 이유: ${task.exception}")
                    }
                }
        }

        // 소셜 로그인 (Google)
        binding.googleLoginBtn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            requestLauncher.launch(signInIntent)
        }

        // 카카오 계정 로그인만 사용 (카카오톡 앱 없이도 동작)
        binding.btnKakaoLogin.setOnClickListener {
            UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                if (error != null) {
                    Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
                    Log.e("KakaoLogin", "카카오 계정 로그인 실패", error)
                } else if (token != null) {
                    Log.i("KakaoLogin", "카카오 계정 로그인 성공: ${token.accessToken}")
                    onLoginSuccess()
                }
            }
        }



        // 회원가입 모드로 전환
        binding.goSignInBtn.setOnClickListener {
            changeVisibility("signin")
        }

        // 기본은 로그인 모드
        changeVisibility("login")
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                Log.e("KakaoLogin", "카카오 계정 로그인 실패", error)
                Toast.makeText(this, "카카오 계정 로그인 실패: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                Log.d("KakaoLogin", "카카오 계정 로그인 성공. 토큰: ${token.accessToken}")
                onKakaoLoginSuccess()
            }
        }
    }

    private fun onKakaoLoginSuccess() {
        // 로그인 성공 후 사용자 정보 요청
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
                Toast.makeText(this, "사용자 정보 요청 실패: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
            } else if (user != null) {
                Log.d("KakaoLogin", "사용자 정보: ${user.id}, ${user.kakaoAccount?.email}, ${user.kakaoAccount?.profile?.nickname}")
                // 예) Firebase 인증 연동이 필요하면 여기서 처리 가능
                // 지금은 그냥 로그인 성공으로 간주하고 메인 화면으로 이동
                onLoginSuccess()
            }
        }
    }

    private fun onLoginSuccess() {
        // 로그인 성공 시 호출 (이메일, 구글, 카카오 공통)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun changeVisibility(mode: String) {
        binding.run {
            when (mode) {
                "signin" -> {
                    signBtn.visibility = View.VISIBLE
                    loginBtn.visibility = View.GONE
                    goSignInBtn.visibility = View.GONE
                }
                "login" -> {
                    signBtn.visibility = View.GONE
                    loginBtn.visibility = View.VISIBLE
                    goSignInBtn.visibility = View.VISIBLE
                }
            }
        }
    }
}
