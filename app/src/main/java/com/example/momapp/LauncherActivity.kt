package com.example.momapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val autoLogin = sharedPref.getBoolean("auto_login", true)

        val loggedIn = MyApplication.checkAuth()

        val nextIntent = if (autoLogin) {
            // 자동로그인 ON
            if (loggedIn) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
        } else {
            // 자동로그인 OFF
            Intent(this, LoginActivity::class.java)
        }

        nextIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(nextIntent)
        finish()
    }
}
