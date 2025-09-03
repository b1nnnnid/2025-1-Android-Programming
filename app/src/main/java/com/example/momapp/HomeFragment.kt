package com.example.momapp


import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.net.Uri


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFindSitter = view.findViewById<View>(R.id.btnFindSitter)
        val btnFindFacility = view.findViewById<View>(R.id.btnFindFacility)

        val cornerRadius = 24f * resources.displayMetrics.density // 24dp -> px
        val bgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(0xFFFF8AAE.toInt())  // 원하는 색 (ARGB 형태로 Int 변환)
            this.cornerRadius = cornerRadius
        }

        val bgDrawable2 = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(0xFFFF8AAE.toInt())
            this.cornerRadius = cornerRadius
        }

        btnFindSitter.post {
            val width = btnFindSitter.width
            val newSize = (width * 2 / 3f).toInt()
            btnFindSitter.layoutParams.height = newSize
            btnFindSitter.layoutParams.width = newSize
            btnFindSitter.requestLayout()
        }

        btnFindFacility.post {
            val width = btnFindFacility.width
            val newSize = (width * 2 / 3f).toInt()
            btnFindFacility.layoutParams.height = newSize
            btnFindFacility.layoutParams.width = newSize
            btnFindFacility.requestLayout()
        }


        btnFindSitter.setOnClickListener {
            // 액티비티를 가져와서 뷰페이저 페이지 1 (CareFragment)로 이동 요청
            (activity as? MainActivity)?.moveToPage(1)
        }

        btnFindFacility.setOnClickListener {
            // 뷰페이저 페이지 2 (FacilityFragment)로 이동 요청
            (activity as? MainActivity)?.moveToPage(2)
        }

        val btnKorea = view.findViewById<ImageView>(R.id.btnKorea)
        val btnWomen = view.findViewById<ImageView>(R.id.btnWomen)
        val btnYouTube = view.findViewById<ImageView>(R.id.btnYouTube)
        val btnBokjiro = view.findViewById<ImageView>(R.id.btnBokjiro)
        val btnIdolbom = view.findViewById<ImageView>(R.id.btnIdolbom)

        btnKorea.setOnClickListener {
            openUrl("https://kws.or.kr/business/single01.asp")  // 대한사회복지회 공식 홈페이지
        }

        btnWomen.setOnClickListener {
            openUrl("https://www.mogef.go.kr/singleparent/main.do")  // 여성가족부 공식 홈페이지
        }

        btnYouTube.setOnClickListener {
            openUrl("https://youtu.be/5SNEWlr2luw?si=4j_S7kTOPlzjmUnz")  // 유튜브 영상 URL로 변경
        }

        btnBokjiro.setOnClickListener {
            openUrl("https://www.bokjiro.go.kr/")  // 복지로 포털
        }

        btnIdolbom.setOnClickListener {
            openUrl("https://idolbom.go.kr/")  // 아이돌봄 누리집
        }
    }
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        startActivity(intent)
    }
    }

