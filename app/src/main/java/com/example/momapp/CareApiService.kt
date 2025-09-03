package com.example.momapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//endpoint  https://apis.data.go.kr/1383000/idis/careGiverService

interface CareApiService {
    @GET("1383000/idis/careGiverService/getCareGiverList")
    fun getJsonListWithName(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("type") type: String = "json",
        @Query("crtrYmFrom") crtrYmFrom: String,
        @Query("crtrYmTo") crtrYmTo: String,
        @Query("childCareInstNm") childCareInstNm: String
    ): Call<CareJsonResponse>

    @GET("1383000/idis/careGiverService/getCareGiverList")
    fun getJsonListWithoutName(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("type") type: String = "json",
        @Query("crtrYmFrom") crtrYmFrom: String,
        @Query("crtrYmTo") crtrYmTo: String
    ): Call<CareJsonResponse>
}
