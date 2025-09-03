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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CareFragment : Fragment() {

    private lateinit var savedRecyclerView: RecyclerView
    private val savedList = mutableListOf<JsonCareItem>()
    private lateinit var savedAdapter: CareJsonAdapter
    private lateinit var dbHelper: CareDbHelper

    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchInstitution: EditText
    private lateinit var btnSearch: Button

    private val careList = mutableListOf<JsonCareItem>()
    private lateinit var adapter: CareJsonAdapter

    private val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
    private val currentMonth = sdf.format(Date())

    private var currentPage = 1
    private var isLoading = false
    private var totalCount = 0
    private var lastSearchKeyword: String? = null


    override fun onResume() {
        super.onResume()
        loadSavedItems()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.care_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerSitterList)
        etSearchInstitution = view.findViewById(R.id.etSearchInstitution)
        btnSearch = view.findViewById(R.id.btnSearch_c)

        adapter = CareJsonAdapter(careList) { selectedItem ->
            val intent = Intent(requireContext(), CareDetailActivity::class.java).apply {
                putExtra("care_item", selectedItem)  // Serializable or Parcelable 필요
            }
            startActivity(intent)
        }


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        savedRecyclerView = view.findViewById(R.id.savedRecyclerView)
        dbHelper = CareDbHelper(requireContext())

        savedAdapter = CareJsonAdapter(savedList) { selectedItem ->
            // 상세페이지 이동
            val intent = Intent(requireContext(), CareDetailActivity::class.java)
            intent.putExtra("care_item", selectedItem)
            startActivity(intent)
        }

        savedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        savedRecyclerView.adapter = savedAdapter

        // 저장된 기관 목록 불러오기
        loadSavedItems()

        // 스크롤 페이징 처리 - RecyclerView 끝에 도달하면 다음 페이지 로딩 시도
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)

                if (dy <= 0) return  // 아래로 스크롤 아닐 땐 무시

                val layoutManager = rv.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    // 다음 페이지가 있을 때만 로딩
                    if (careList.size < totalCount) {
                        currentPage++
                        loadCareData(lastSearchKeyword, "202002", currentMonth, currentPage, append = true)
                    }
                }
            }
        })

        btnSearch.setOnClickListener {
            val institutionName = etSearchInstitution.text.toString().trim()
            loadCareData(if (institutionName.isEmpty()) null else institutionName)

        // 검색 키워드 저장 및 페이지 초기화
            lastSearchKeyword = institutionName
            currentPage = 1
            careList.clear()
            adapter.notifyDataSetChanged()

            loadCareData(institutionName, "202002", currentMonth, currentPage)
        }

        // 최초 로딩 (기관명 없이)
        lastSearchKeyword = null
        currentPage = 1
        loadCareData(null, "202002", currentMonth, currentPage)

        return view
    }
    private fun loadSavedItems() {
        val currentUserId = MyApplication.auth.currentUser?.uid ?: "unknown_user"
        savedList.clear()
        savedList.addAll(dbHelper.getCareItemsByUser(currentUserId))
        savedAdapter.notifyDataSetChanged()
    }


    private fun loadCareData(
        institutionName: String? = null,
        fromYm: String = "202002",
        toYm: String = currentMonth,
        pageNo: Int = 1,
        numOfRows: Int = 50,
        append: Boolean = false
    ) {
        isLoading = true

        val serviceKey = "BV9GQ+oqsvpjz1Aitc6gt0miKWYiCksd8SYBn1PFIxAZOyZVvlm+kgmKNrBJrLdF4iYAFntLE7LYEbu/QgQEpg=="

        val call = if (institutionName.isNullOrEmpty()) {
            RetrofitClient.careApiService.getJsonListWithoutName(
                serviceKey = serviceKey,
                pageNo = pageNo,
                numOfRows = numOfRows,
                type = "json",
                crtrYmFrom = fromYm,
                crtrYmTo = toYm
            )
        } else {
            RetrofitClient.careApiService.getJsonListWithName(
                serviceKey = serviceKey,
                pageNo = pageNo,
                numOfRows = numOfRows,
                type = "json",
                crtrYmFrom = fromYm,
                crtrYmTo = toYm,
                childCareInstNm = institutionName
            )
        }

        call.enqueue(object : Callback<CareJsonResponse> {
            override fun onResponse(call: Call<CareJsonResponse>, response: Response<CareJsonResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val body = response.body()?.response?.body
                    val items = body?.items?.item ?: emptyList()
                    totalCount = body?.totalCount ?: 0

                    // API가 기관명 필터링을 제대로 못 하는 경우 앱 내 필터링 수행
                    val filteredItems = if (!institutionName.isNullOrBlank()) {
                        items.filter {
                            (it.childCareInstNm ?: "")
                                .replace("\\s".toRegex(), "")
                                .contains(institutionName.replace("\\s".toRegex(), ""), ignoreCase = true)
                        }
                    } else {
                        items
                    }


                    if (append) {
                        careList.addAll(filteredItems)
                    } else {
                        careList.clear()
                        careList.addAll(filteredItems)
                    }

                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("CareFragment", "API 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<CareJsonResponse>, t: Throwable) {
                isLoading = false
                Log.e("CareFragment", "네트워크 실패: ${t.message}")
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
