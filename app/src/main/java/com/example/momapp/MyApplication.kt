package com.example.momapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import com.kakao.sdk.common.KakaoSdk


class MyApplication : Application() {
    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var db : FirebaseFirestore
        var email: String? = null

        fun checkAuth(): Boolean {
            val currentUser = auth.currentUser

            return currentUser?.let {
                email = currentUser.email
                if (currentUser.isEmailVerified) {
                    true
                }
                else {
                    false
                }
            } ?: let {
                false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        val sharedPref = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val darkModeOn = sharedPref.getBoolean("dark_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        auth = Firebase.auth
        db=FirebaseFirestore.getInstance()
    }
}