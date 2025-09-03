package com.example.momapp

import CareDbHelper
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FamilyDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_detail)

        // 전달받은 Serializable 객체 받기
        val item = intent.getSerializableExtra("family_item") as? JsonFamilyItem
        val btnSave: Button = findViewById(R.id.btnSave)
        val dbHelper = FamilyDbHelper(this)

        btnSave.setOnClickListener {
            if (item != null) {
                val userId = MyApplication.auth.currentUser?.uid
                if (userId != null) {
                    val success = dbHelper.insertFamilyItem(item, userId)
                    if (success!= -1L) {
                        Toast.makeText(this, "기관이 저장되었습니다.", Toast.LENGTH_SHORT).show()

                        val alarmOn = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
                            .getBoolean("alarm_on", true)

                        if (alarmOn) {
                            val helper = MyNotificationHelper(this)
                            helper.showNotification("저장 완료!", "${item.facilityName ?: "기관"} 이(가) 저장되었습니다.")
                            finish()
                        }

                    } else {
                        Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }


        // 각 텍스트뷰 초기화
        val tvFacilityName = findViewById<TextView>(R.id.tvFacilityName)
        val tvFacilityType = findViewById<TextView>(R.id.tvFacilityType)
        val tvFacilitySupportType = findViewById<TextView>(R.id.tvFacilitySupportType)
        val tvRepresentative = findViewById<TextView>(R.id.tvRepresentative)
        val tvOperatingBody = findViewById<TextView>(R.id.tvOperatingBody)
        val tvInstallationType = findViewById<TextView>(R.id.tvInstallationType)
        val tvLocation = findViewById<TextView>(R.id.tvLocation)
        val tvAddress = findViewById<TextView>(R.id.tvAddress)
        val tvLotAddress = findViewById<TextView>(R.id.tvLotAddress)
        val tvHomepage = findViewById<TextView>(R.id.tvHomepage)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val tvFax = findViewById<TextView>(R.id.tvFax)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvBizNumber = findViewById<TextView>(R.id.tvBizNumber)
        val tvSupportContents = findViewById<TextView>(R.id.tvSupportContents)
        val tvEvalGrade = findViewById<TextView>(R.id.tvEvalGrade)
        val tvCapacity = findViewById<TextView>(R.id.tvCapacity)
        val tvIsOperating = findViewById<TextView>(R.id.tvIsOperating)
        val tvEntryTarget = findViewById<TextView>(R.id.tvEntryTarget)
        val tvEntryPeriod = findViewById<TextView>(R.id.tvEntryPeriod)
        val tvEntryProcess = findViewById<TextView>(R.id.tvEntryProcess)
        val tvHasParking = findViewById<TextView>(R.id.tvHasParking)
        val tvNearbySubway = findViewById<TextView>(R.id.tvNearbySubway)
        val tvNearbyBusStop = findViewById<TextView>(R.id.tvNearbyBusStop)
        val tvCreatedDate = findViewById<TextView>(R.id.tvCreatedDate)

        if (item != null) {
            // item 객체로부터 데이터 설정 (null일 경우 "정보 없음" 표시)
            tvFacilityName.text = item.facilityName ?: "정보 없음"
            tvFacilityType.text = item.facilityType ?: "정보 없음"
            tvFacilitySupportType.text = item.facilitySupportType ?: "정보 없음"
            tvRepresentative.text = item.representativeName ?: "정보 없음"
            tvOperatingBody.text = item.operatingBody ?: "정보 없음"
            tvInstallationType.text = item.installationType ?: "정보 없음"
            tvLocation.text = "${item.city ?: "정보 없음"} ${item.district ?: ""}".trim()
            tvAddress.text = item.roadNmAddr ?: "정보 없음"
            tvLotAddress.text = item.lotnoAddr ?: "정보 없음"
            tvHomepage.text = item.homepage ?: "정보 없음"
            tvPhone.text = item.phoneNumber ?: "정보 없음"
            tvFax.text = item.fax ?: "정보 없음"
            tvEmail.text = item.email ?: "정보 없음"
            tvBizNumber.text = item.bizNumber ?: "정보 없음"
            tvSupportContents.text = item.supportContents ?: "정보 없음"
            tvEvalGrade.text = item.evalGrade ?: "정보 없음"
            tvCapacity.text = "${item.capacityCount ?: "정보 없음"} ${item.capacityUnit ?: ""}".trim()
            tvIsOperating.text = if (item.isOperating == "Y") "운영 중" else "운영 안 함"
            tvEntryTarget.text = item.entryTarget ?: "정보 없음"
            tvEntryPeriod.text = item.entryPeriod ?: "정보 없음"
            tvEntryProcess.text = item.entryProcess ?: "정보 없음"
            tvHasParking.text = if (item.hasParking == "Y") "가능" else "불가능"
            tvNearbySubway.text = item.nearbySubway ?: "정보 없음"
            tvNearbyBusStop.text = item.nearbyBusStop ?: "정보 없음"
            tvCreatedDate.text = item.createdDate ?: "정보 없음"
        } else {
            Toast.makeText(this, "데이터를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }

        // 전화번호 클릭 시 다이얼 화면 열기
        tvPhone.setOnClickListener {
            val phoneNumber = tvPhone.text.toString().filter { it.isDigit() }
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "전화번호가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 홈페이지 클릭 시 브라우저 열기
        tvHomepage.setOnClickListener {
            val urlRaw = tvHomepage.text.toString()
            if (urlRaw.isNotEmpty() && urlRaw != "정보 없음") {
                val url = if (!urlRaw.startsWith("http")) "http://$urlRaw" else urlRaw
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else {
                Toast.makeText(this, "홈페이지 주소가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 이메일 클릭 시 이메일 앱 열기
        tvEmail.setOnClickListener {
            val email = tvEmail.text.toString()
            if (email.isNotEmpty() && email != "정보 없음") {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "이메일 주소가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 주소 클릭 시 지도 앱 열기 (지도 검색)
        tvAddress.setOnClickListener {
            val address = tvAddress.text.toString()
            if (address.isNotEmpty() && address != "정보 없음") {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "주소 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
