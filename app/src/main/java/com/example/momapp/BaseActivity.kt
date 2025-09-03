package com.example.momapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val fontSizeLevel = sharedPref.getInt("font_size_level", 1)

        // 글자 크기 조절 적용
        val configuration = resources.configuration
        configuration.fontScale = when(fontSizeLevel) {
            0 -> 0.85f
            1 -> 1.0f
            2 -> 1.15f
            else -> 1.0f
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)

        super.onCreate(savedInstanceState)
    }
}
