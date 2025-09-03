package com.example.momapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.momapp.LoginActivity
import com.example.momapp.MyApplication
import com.example.momapp.MyNotificationHelper
import com.example.momapp.R
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var switchDarkMode: Switch
    private lateinit var switchAlarm: Switch
    private lateinit var switchAutoLogin: Switch
    private lateinit var seekFontSize: SeekBar
    private lateinit var btnLogout: Button

    private val sharedPrefName = "user_settings"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 연결
        tvUserName = view.findViewById(R.id.tvUserName)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchAlarm = view.findViewById(R.id.switchAlarm)
        switchAutoLogin = view.findViewById(R.id.switchAutoLogin)
        seekFontSize = view.findViewById(R.id.seekFontSize)
        btnLogout = view.findViewById(R.id.btnLogout)

        // SharedPreferences
        val sharedPref = requireContext().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        // 1. 사용자 이름 표시
        val currentUser = FirebaseAuth.getInstance().currentUser
        val displayNameOrEmail = when {
            !currentUser?.displayName.isNullOrBlank() -> currentUser!!.displayName!!
            !currentUser?.email.isNullOrBlank() -> currentUser!!.email!!
            else -> "사용자"
        }
        tvUserName.text = "$displayNameOrEmail 님, 환영합니다."

        // 2. 스위치 초기값 세팅
        switchDarkMode.isChecked = sharedPref.getBoolean("dark_mode", false)
        switchAlarm.isChecked = sharedPref.getBoolean("alarm_on", true)
        switchAutoLogin.isChecked = sharedPref.getBoolean("auto_login", true)
        seekFontSize.progress = sharedPref.getInt("font_size_level", 1)

        // 3. 다크모드 설정
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            requireActivity().recreate()
        }

        // 4. 알림 스위치 리스너
        switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("alarm_on", isChecked).apply()
            if (isChecked) {
                Toast.makeText(requireContext(), "알림이 켜졌습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "알림이 꺼졌습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. 자동 로그인 스위치
        switchAutoLogin.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("auto_login", isChecked).apply()
        }

        // 6. 글자 크기 SeekBar
        seekFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sharedPref.edit().putInt("font_size_level", progress).apply()
                requireActivity().recreate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 7. 로그아웃 버튼
        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            MyApplication.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
