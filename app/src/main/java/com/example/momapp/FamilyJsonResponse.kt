package com.example.momapp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FamilyJsonResponse(
    @SerializedName("response")
    val response: JsonFamilyResponse
)

data class JsonFamilyResponse(
    @SerializedName("header")
    val header: JsonFamilyHeader,
    @SerializedName("body")
    val body: JsonFamilyBody
)

data class JsonFamilyHeader(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String
)

data class JsonFamilyBody(
    @SerializedName("items")
    val items: JsonFamilyItems?,
    @SerializedName("numOfRows")
    val numOfRows: Int,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("totalCount")
    val totalCount: Int
)

data class JsonFamilyItems(
    @SerializedName("item")
    val itemList: List<JsonFamilyItem>?
)

data class JsonFamilyItem(
    @SerializedName("fcltNm") val facilityName: String?,             // 시설명
    @SerializedName("fcltSeNm") val facilityType: String?,           // 시설세부유형
    @SerializedName("fcltTypeCn") val facilitySupportType: String?,  // 시설 유형
    @SerializedName("rprsvNm") val representativeName: String?,      // 대표자
    @SerializedName("operMbyCn") val operatingBody: String?,         // 운영법인
    @SerializedName("instlTypeCn") val installationType: String?,    // 설치주체
    @SerializedName("ctpvNm") val city: String?,                     // 시/도
    @SerializedName("sggNm") val district: String?,                  // 구/군
    @SerializedName("roadNmAddr") val roadNmAddr: String?,           // 도로명 주소
    @SerializedName("lotnoAddr") val lotnoAddr: String?,             // 지번 주소
    @SerializedName("hmpgAddr") val homepage: String?,               // 홈페이지
    @SerializedName("rprsTelno") val phoneNumber: String?,           // 대표 전화번호
    @SerializedName("fxno") val fax: String?,                        // 팩스번호
    @SerializedName("emlAddr") val email: String?,                   // 이메일
    @SerializedName("brno") val bizNumber: String?,                  // 사업자번호
    @SerializedName("sprtCnt") val supportContents: String?,         // 지원내용
    @SerializedName("fcltEvlGradVl") val evalGrade: String?,         // 시설 평가등급
    @SerializedName("cpctCrtrNm") val capacityUnit: String?,         // 정원 단위
    @SerializedName("cpctCnt") val capacityCount: String?,           // 정원 수
    @SerializedName("operYn") val isOperating: String?,              // 운영여부
    @SerializedName("etrTrgtCn") val entryTarget: String?,           // 입소 대상
    @SerializedName("etrPrdCn") val entryPeriod: String?,            // 입소 기간
    @SerializedName("etrPcsCn") val entryProcess: String?,           // 입소 절차
    @SerializedName("pknFcltYn") val hasParking: String?,            // 주차시설 여부
    @SerializedName("nrbSbwNm") val nearbySubway: String?,           // 근처 지하철
    @SerializedName("nrbBusStnNm") val nearbyBusStop: String?,       // 근처 버스정류장
    @SerializedName("crtrYmd") val createdDate: String?,              // 생성일
    var userId: String? = null
) : Serializable
