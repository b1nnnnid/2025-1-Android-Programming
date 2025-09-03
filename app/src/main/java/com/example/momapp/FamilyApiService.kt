package com.example.momapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//endpoint  https://apis.data.go.kr/1383000/gmis/snparntFamSrftServiceV2/getSnparntFamSrftListV2

interface FamilyApiService {
    @GET("1383000/gmis/snparntFamSrftServiceV2/getSnparntFamSrftListV2")
    fun getJsonList(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("type") type: String = "json",
        @Query("fcltNm") facilityName: String? = null,
        @Query("ctpvNm") city: String? = null,
        @Query("sggNm") district: String? = null
    ): Call<FamilyJsonResponse>
}

