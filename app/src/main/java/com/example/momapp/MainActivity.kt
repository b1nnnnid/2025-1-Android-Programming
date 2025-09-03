package com.example.momapp

import android.R.attr.data
import android.R.id.shareText
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.momapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.momapp.MyApplication.Companion.email
import com.google.android.material.navigation.NavigationView
import com.google.common.collect.Multimaps.index
import kotlin.jvm.java
import com.kakao.sdk.common.util.Utility


class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: PageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash", keyHash)

        // 툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "MomCare+"


        // 햄버거 토글 생성 및 연결
        val toggle = ActionBarDrawerToggle(
            this,
            binding.main,        // DrawerLayout의 ID
            binding.toolbar,
            R.string.drawer_open,  // 열기/닫기 설명은 접근성용 텍스트
            R.string.drawer_close
        )
        binding.drawer.setNavigationItemSelectedListener(this)

        binding.main.addDrawerListener(toggle)
        toggle.syncState()

        // ViewPager2 + Adapter 연결
        pagerAdapter = PageAdapter(this)
        binding.viewpager.adapter = pagerAdapter

        // TabLayout + ViewPager2 연결
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> "홈"
                1 -> "아이돌봄"
                2 -> "한부모가족"
                3 -> "커뮤니티"
                4 -> "설정"
                else -> "탭 ${position + 1}"
            }
        }.attach()

        //  기본 페이지 설정
        binding.viewpager.currentItem = 0


        // binding.viewpager.isUserInputEnabled = false

        // 권한 요청
        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it.all { permission -> permission.value != true }) {
                    Toast.makeText(this, "permission DENIED", Toast.LENGTH_SHORT).show()
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.POST_NOTIFICATIONS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(arrayOf("android.permission.POST_NOTIFICATIONS"))
            }
        }
    }

    // 프래그먼트에서 호출할 함수
    fun moveToPage(position: Int) {
        binding.viewpager.currentItem = position
        Log.d("MainActivity", "moveToPage called: $position")
    }

    // 백 버튼 처리
    override fun onBackPressed() {
        if (binding.viewpager.currentItem != 0) {
            binding.viewpager.currentItem = 0
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item1 -> {  // 버전 정보, 평점 저장
                showRatingDialog()
                return true
            }
            R.id.nav_item2 -> {  // 문의하기(메일 보내기)
                sendEmailToDeveloper()
                return true
            }
            R.id.nav_item3 -> {  // 설정 이동
                binding.viewpager.currentItem = 4
                return true
            }
            R.id.nav_item4 -> {  // 로그아웃
                performLogout()
                return true
            }
        }
        return false
    }

    fun showRatingDialog() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // 현재 로그인된 사용자 uid 가져오기
        val uid = MyApplication.auth.currentUser?.uid

        val dialogView = layoutInflater.inflate(R.layout.dialog_rating, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val versionTextView = dialogView.findViewById<TextView>(R.id.versionTextView)

        // 사용자 uid 기반 키로 저장된 별점 불러오기
        val savedRating = prefs.getFloat("user_rating_$uid", 0f)
        ratingBar.rating = savedRating

        versionTextView.text = "버전 1.0.0"

        AlertDialog.Builder(this)
            .setTitle("MomCare+가 만족스러우신가요?")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                val rating = ratingBar.rating

                // 사용자 uid 기반 키로 별점 저장
                prefs.edit().putFloat("user_rating_$uid", rating).apply()

                Toast.makeText(this, "별점 $rating 점 감사합니다!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소", null)
            .show()
    }




    fun sendEmailToDeveloper() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"  // 이메일 전용 MIME 타입
            putExtra(Intent.EXTRA_EMAIL, arrayOf("dev@example.com"))
            putExtra(Intent.EXTRA_SUBJECT, "문의사항")
            putExtra(Intent.EXTRA_TEXT, "개발자에게 문의사항을 전송합니다.")
        }

        startActivity(Intent.createChooser(intent, "이메일 앱을 선택하세요"))
    }


    private fun performLogout() {
        // 로그아웃 로직 수행 (예: 세션 초기화, 토큰 삭제 등)

        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

        // 로그아웃 후 로그인 화면으로 이동
        MyApplication.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}
