package com.example.momapp

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.postDelayed
import com.example.momapp.MainActivity
import android.view.animation.AnimationUtils

import android.os.Handler


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImage = findViewById<ImageView>(R.id.logoImage)
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        logoImage.startAnimation(scaleAnimation)

        // 애니메이션이 끝날 때까지 기다리거나 딜레이 후 다음 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LauncherActivity::class.java))
            finish()
        }, 1500)
    }
}
