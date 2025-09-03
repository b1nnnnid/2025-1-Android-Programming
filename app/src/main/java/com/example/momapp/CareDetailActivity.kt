package com.example.momapp

import CareDbHelper
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class CareDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_care_detail)

        val item = intent.getSerializableExtra("care_item") as? JsonCareItem
        val btnSave: Button = findViewById(R.id.btnSave)
        val dbHelper = CareDbHelper(this)

        val tvChildCareInstNm = findViewById<TextView>(R.id.tvChildCareInstNm)
        val tvChildCareInstNo = findViewById<TextView>(R.id.tvChildCareInstNo)
        val tvCrtrYm = findViewById<TextView>(R.id.tvCrtrYm)
        val tvUpChildCareInstNo = findViewById<TextView>(R.id.tvUpChildCareInstNo)
        val tvUpChildCareInstNm = findViewById<TextView>(R.id.tvUpChildCareInstNm)
        val tvThmmSittrCnt = findViewById<TextView>(R.id.tvThmmSittrCnt)
        val tvThmmPttgthTypePsbltSittrCnt = findViewById<TextView>(R.id.tvThmmPttgthTypePsbltSittrCnt)
        val tvThmmCrtftPssnSittrCnt = findViewById<TextView>(R.id.tvThmmCrtftPssnSittrCnt)
        val tvThmmStrhtLinkPsbltSittrCnt = findViewById<TextView>(R.id.tvThmmStrhtLinkPsbltSittrCnt)
        val tvAcmltCrtftPssnSittrCnt = findViewById<TextView>(R.id.tvAcmltCrtftPssnSittrCnt)
        val barChart = findViewById<BarChart>(R.id.barChart)

        item?.let {
            tvChildCareInstNm.text = it.childCareInstNm ?: "정보 없음"
            tvChildCareInstNo.text = it.childCareInstNo ?: "정보 없음"
            tvCrtrYm.text = it.crtrYm ?: "정보 없음"
            tvUpChildCareInstNo.text = it.upChildCareInstNo ?: "정보 없음"
            tvUpChildCareInstNm.text = it.upChildCareInstNm ?: "정보 없음"
            tvThmmSittrCnt.text = "${it.thmmSittrCnt ?: 0}"
            tvThmmPttgthTypePsbltSittrCnt.text = "${it.thmmPttgthTypePsbltSittrCnt ?: 0}"
            tvThmmCrtftPssnSittrCnt.text = "${it.thmmCrtftPssnSittrCnt ?: 0}"
            tvThmmStrhtLinkPsbltSittrCnt.text = "${it.thmmStrhtLinkPsbltSittrCnt ?: 0}"
            tvAcmltCrtftPssnSittrCnt.text = "${it.acmltCrtftPssnSittrCnt ?: 0}"

            val sitterCounts = listOf(
                it.thmmSittrCnt ?: 0,
                it.thmmPttgthTypePsbltSittrCnt ?: 0,
                it.thmmCrtftPssnSittrCnt ?: 0,
                it.thmmStrhtLinkPsbltSittrCnt ?: 0,
                it.acmltCrtftPssnSittrCnt ?: 0
            )

            val entries = sitterCounts.mapIndexed { index, count ->
                BarEntry(index.toFloat(), count.toFloat())
            }

            val barDataSet = BarDataSet(entries, "돌보미 수")
            barDataSet.colors = listOf(
                Color.parseColor("#F44336"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#9C27B0")
            )

            val barData = BarData(barDataSet)
            barData.barWidth = 0.8f

            barChart.data = barData

            val labels = listOf(
                "총 돌보미 수",
                "동거 가능 돌보미 수",
                "자격증 보유 돌보미 수",
                "직접 연결 가능 돌보미 수",
                "누적 자격증 보유 돌보미 수"
            )

            val xAxis = barChart.xAxis

            // 다크모드 체크
            val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
            val isDarkMode = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES

            if (isDarkMode) {
                // 다크모드일 때 라벨 텍스트 색상 밝은 색으로 변경
                xAxis.textColor = Color.WHITE
            } else {
                // 기본 색상
                xAxis.textColor = Color.BLACK
            }
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.labelRotationAngle = 45f
            xAxis.textSize = 10f

            barChart.setExtraOffsets(10f, 10f, 10f, 20f)

            barChart.axisRight.isEnabled = false
            barChart.description.isEnabled = false
            barChart.setFitBars(true)
            barChart.invalidate()
        }

        btnSave.setOnClickListener {
            if (item != null) {
                val userId = MyApplication.auth.currentUser?.uid
                if (userId != null) {
                    val success = dbHelper.insertCareItem(item, userId)
                    if (success) {
                        Toast.makeText(this, "기관이 저장되었습니다.", Toast.LENGTH_SHORT).show()

                        val alarmOn = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
                            .getBoolean("alarm_on", true)

                        if (alarmOn) {
                            val helper = MyNotificationHelper(this)
                            helper.showNotification("저장 완료!", "${item.childCareInstNm ?: "기관"} 이(가) 저장되었습니다.")
                            finish()
                        }

                    } else {
                        Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}
