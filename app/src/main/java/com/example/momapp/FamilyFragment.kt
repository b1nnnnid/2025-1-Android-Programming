package com.example.momapp

import CareDbHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.JsonAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class FamilyFragment : Fragment() {

    private lateinit var savedRecyclerView: RecyclerView
    private lateinit var savedAdapter: FamilyJsonAdapter
    private val savedList = mutableListOf<JsonFamilyItem>()
    private lateinit var dbHelper: FamilyDbHelper

    private lateinit var adapter: FamilyJsonAdapter
    private val familyList = mutableListOf<JsonFamilyItem>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchFacilityName: EditText
    private lateinit var btnSearch: Button

    private var currentPage = 1
    private val numOfRows = 20
    private var isLoading = false
    private var isLastPage = false

    private var currentFacilityName: String? = null
    private var currentCity: String? = null
    private var currentDistrict: String? = null

    override fun onResume() {
        super.onResume()
        loadSavedItems()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.family_fragment, container, false)

        // 뷰 초기화
        recyclerView = view.findViewById(R.id.recyclerFacilityList)
        etSearchFacilityName = view.findViewById(R.id.etSearchFacilityName)
        btnSearch = view.findViewById(R.id.btnSearch_f)
        savedRecyclerView = view.findViewById(R.id.savedRecyclerView)

        // DB 헬퍼 초기화
        dbHelper = FamilyDbHelper(requireContext())

        adapter = FamilyJsonAdapter(familyList) { selectedItem ->
            val intent = Intent(requireContext(), FamilyDetailActivity::class.java).apply {
                putExtra("family_item", selectedItem)  // Serializable or Parcelable 필요
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        savedAdapter = FamilyJsonAdapter(savedList) { selectedItem ->
            // 상세페이지 이동
            val intent = Intent(requireContext(), FamilyDetailActivity::class.java)
            intent.putExtra("family_item", selectedItem)
            startActivity(intent)
        }
        savedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        savedRecyclerView.adapter = savedAdapter

        // 저장된 기관 불러오기
        loadSavedItems()

        // 스크롤 페이징 처리
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy <= 0) return

                val layoutManager = rv.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPos) >= totalItemCount
                        && firstVisibleItemPos >= 0) {
                        loadFamilyData(currentFacilityName, currentCity, currentDistrict, currentPage + 1)
                    }
                }
            }
        })

        btnSearch.setOnClickListener {
            val input = etSearchFacilityName.text.toString().trim()
            val words = input.split(" ")

            val facilityName: String?
            val city: String?
            val district: String?

            when {
                words.size >= 2 -> {
                    city = words[0]
                    val dist = words.subList(1, words.size).joinToString(" ")
                    district = if (dist.endsWith("구") || dist.endsWith("군") || dist.endsWith("시")) {
                        dist
                    } else {
                        "$dist 구"
                    }
                    facilityName = null
                }
                input.endsWith("구") || input.endsWith("군") || input.endsWith("시") -> {
                    city = null
                    district = input
                    facilityName = null
                }
                else -> {
                    city = null
                    district = if (input.isNotEmpty() && !(input.endsWith("구") || input.endsWith("군") || input.endsWith("시"))) {
                        "$input 구"
                    } else {
                        input
                    }
                    facilityName = null
                }
            }

            currentFacilityName = facilityName
            currentCity = city
            currentDistrict = district

            currentPage = 1
            isLastPage = false
            familyList.clear()
            adapter.notifyDataSetChanged()

            loadFamilyData(facilityName, city, district, currentPage)
        }

        // 초기 로딩
        currentFacilityName = null
        currentCity = null
        currentDistrict = null
        loadFamilyData(null, null, null, 1)

        return view
    }

    private fun loadSavedItems() {
        val currentUserId = MyApplication.auth.currentUser?.uid ?: "unknown_user"
        savedList.clear()
        savedList.addAll(dbHelper.getFamilyItemsByUser(currentUserId))
        savedAdapter.notifyDataSetChanged()
    }

    private fun loadFamilyData(
        facilityName: String?,
        city: String?,
        district: String?,
        pageNo: Int = 1
    ) {
        if (isLoading || isLastPage) return

        isLoading = true

        val call = RetrofitClient.familyApiService.getJsonList(
            serviceKey = "BV9GQ+oqsvpjz1Aitc6gt0miKWYiCksd8SYBn1PFIxAZOyZVvlm+kgmKNrBJrLdF4iYAFntLE7LYEbu/QgQEpg==",
            pageNo = pageNo,
            numOfRows = numOfRows,
            facilityName = facilityName,
            city = city,
            district = district
        )

        call.enqueue(object : Callback<FamilyJsonResponse> {
            override fun onResponse(
                call: Call<FamilyJsonResponse>,
                response: Response<FamilyJsonResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val items = response.body()?.response?.body?.items?.itemList ?: emptyList()
                    val totalCount = response.body()?.response?.body?.totalCount ?: 0

                    if (pageNo == 1) familyList.clear()
                    familyList.addAll(items)
                    adapter.notifyDataSetChanged()

                    if (familyList.size >= totalCount) {
                        isLastPage = true
                    }

                    currentPage = pageNo
                } else {
                    Log.e("FamilyFragment", "API 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "데이터 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FamilyJsonResponse>, t: Throwable) {
                isLoading = false
                Log.e("FamilyFragment", "네트워크 오류: ${t.message}")
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
