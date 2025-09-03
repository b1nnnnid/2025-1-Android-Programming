package com.example.momapp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CareJsonResponse(
    val response: JsonCareResponse
)

data class JsonCareResponse(
    val header: JsonCareHeader,
    val body: JsonCareBody
)

data class JsonCareHeader(
    val resultCode: String,
    val resultMsg: String
)

data class JsonCareBody(
    val items: CareItems,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class CareItems(
    val item: List<JsonCareItem>
)

data class JsonCareItem(
    val crtrYm: String?,
    val childCareInstNo: String?,
    val childCareInstNm: String?,
    val upChildCareInstNo: String?,
    val upChildCareInstNm: String?,
    val thmmSittrCnt: Int?,
    val thmmPttgthTypePsbltSittrCnt: Int?,
    val thmmCrtftPssnSittrCnt: Int?,
    val thmmStrhtLinkPsbltSittrCnt: Int?,
    val acmltCrtftPssnSittrCnt: Int?,
    var userId: String? = null
) : Serializable
