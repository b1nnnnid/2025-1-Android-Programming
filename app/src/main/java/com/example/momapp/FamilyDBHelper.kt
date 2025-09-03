package com.example.momapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FamilyDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "family_db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "family"

        private const val COL_FACILITY_NAME = "facilityName"
        private const val COL_FACILITY_TYPE = "facilityType"
        private const val COL_FACILITY_SUPPORT_TYPE = "facilitySupportType"
        private const val COL_REPRESENTATIVE_NAME = "representativeName"
        private const val COL_OPERATING_BODY = "operatingBody"
        private const val COL_INSTALLATION_TYPE = "installationType"
        private const val COL_CITY = "city"
        private const val COL_DISTRICT = "district"
        private const val COL_ROAD_ADDRESS = "roadAddress"
        private const val COL_LOTNO_ADDRESS = "lotnoAddress"
        private const val COL_HOMEPAGE = "homepage"
        private const val COL_PHONE_NUMBER = "phoneNumber"
        private const val COL_FAX = "fax"
        private const val COL_EMAIL = "email"
        private const val COL_BIZ_NUMBER = "bizNumber"
        private const val COL_SUPPORT_CONTENTS = "supportContents"
        private const val COL_EVAL_GRADE = "evalGrade"
        private const val COL_CAPACITY_UNIT = "capacityUnit"
        private const val COL_CAPACITY_COUNT = "capacityCount"
        private const val COL_IS_OPERATING = "isOperating"
        private const val COL_ENTRY_TARGET = "entryTarget"
        private const val COL_ENTRY_PERIOD = "entryPeriod"
        private const val COL_ENTRY_PROCESS = "entryProcess"
        private const val COL_HAS_PARKING = "hasParking"
        private const val COL_NEARBY_SUBWAY = "nearbySubway"
        private const val COL_NEARBY_BUS_STOP = "nearbyBusStop"
        private const val COL_CREATED_DATE = "createdDate"
        private const val COL_USER_ID = "user_id"  // 추가
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FACILITY_NAME TEXT,
                $COL_FACILITY_TYPE TEXT,
                $COL_FACILITY_SUPPORT_TYPE TEXT,
                $COL_REPRESENTATIVE_NAME TEXT,
                $COL_OPERATING_BODY TEXT,
                $COL_INSTALLATION_TYPE TEXT,
                $COL_CITY TEXT,
                $COL_DISTRICT TEXT,
                $COL_ROAD_ADDRESS TEXT,
                $COL_LOTNO_ADDRESS TEXT,
                $COL_HOMEPAGE TEXT,
                $COL_PHONE_NUMBER TEXT,
                $COL_FAX TEXT,
                $COL_EMAIL TEXT,
                $COL_BIZ_NUMBER TEXT,
                $COL_SUPPORT_CONTENTS TEXT,
                $COL_EVAL_GRADE TEXT,
                $COL_CAPACITY_UNIT TEXT,
                $COL_CAPACITY_COUNT TEXT,
                $COL_IS_OPERATING TEXT,
                $COL_ENTRY_TARGET TEXT,
                $COL_ENTRY_PERIOD TEXT,
                $COL_ENTRY_PROCESS TEXT,
                $COL_HAS_PARKING TEXT,
                $COL_NEARBY_SUBWAY TEXT,
                $COL_NEARBY_BUS_STOP TEXT,
                $COL_CREATED_DATE TEXT,
                $COL_USER_ID TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 데이터 보존을 원하면 여기서 ALTER TABLE문으로 컬럼 추가 가능
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert
    fun insertFamilyItem(item: JsonFamilyItem, userId: String): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COL_FACILITY_NAME, item.facilityName)
            put(COL_FACILITY_TYPE, item.facilityType)
            put(COL_FACILITY_SUPPORT_TYPE, item.facilitySupportType)
            put(COL_REPRESENTATIVE_NAME, item.representativeName)
            put(COL_OPERATING_BODY, item.operatingBody)
            put(COL_INSTALLATION_TYPE, item.installationType)
            put(COL_CITY, item.city)
            put(COL_DISTRICT, item.district)
            put(COL_ROAD_ADDRESS, item.roadNmAddr)
            put(COL_LOTNO_ADDRESS, item.lotnoAddr)
            put(COL_HOMEPAGE, item.homepage)
            put(COL_PHONE_NUMBER, item.phoneNumber)
            put(COL_FAX, item.fax)
            put(COL_EMAIL, item.email)
            put(COL_BIZ_NUMBER, item.bizNumber)
            put(COL_SUPPORT_CONTENTS, item.supportContents)
            put(COL_EVAL_GRADE, item.evalGrade)
            put(COL_CAPACITY_UNIT, item.capacityUnit)
            put(COL_CAPACITY_COUNT, item.capacityCount)
            put(COL_IS_OPERATING, item.isOperating)
            put(COL_ENTRY_TARGET, item.entryTarget)
            put(COL_ENTRY_PERIOD, item.entryPeriod)
            put(COL_ENTRY_PROCESS, item.entryProcess)
            put(COL_HAS_PARKING, item.hasParking)
            put(COL_NEARBY_SUBWAY, item.nearbySubway)
            put(COL_NEARBY_BUS_STOP, item.nearbyBusStop)
            put(COL_CREATED_DATE, item.createdDate)
            put(COL_USER_ID, userId)  // 추가
        }

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    // 사용자별로 저장된 아이템 조회
    fun getFamilyItemsByUser(userId: String): List<JsonFamilyItem> {
        val list = mutableListOf<JsonFamilyItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_USER_ID = ?",
            arrayOf(userId),
            null,
            null,
            "id DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val item = JsonFamilyItem(
                    facilityName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FACILITY_NAME)),
                    facilityType = cursor.getString(cursor.getColumnIndexOrThrow(COL_FACILITY_TYPE)),
                    facilitySupportType = cursor.getString(cursor.getColumnIndexOrThrow(COL_FACILITY_SUPPORT_TYPE)),
                    representativeName = cursor.getString(cursor.getColumnIndexOrThrow(COL_REPRESENTATIVE_NAME)),
                    operatingBody = cursor.getString(cursor.getColumnIndexOrThrow(COL_OPERATING_BODY)),
                    installationType = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTALLATION_TYPE)),
                    city = cursor.getString(cursor.getColumnIndexOrThrow(COL_CITY)),
                    district = cursor.getString(cursor.getColumnIndexOrThrow(COL_DISTRICT)),
                    roadNmAddr = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROAD_ADDRESS)),
                    lotnoAddr = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOTNO_ADDRESS)),
                    homepage = cursor.getString(cursor.getColumnIndexOrThrow(COL_HOMEPAGE)),
                    phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE_NUMBER)),
                    fax = cursor.getString(cursor.getColumnIndexOrThrow(COL_FAX)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    bizNumber = cursor.getString(cursor.getColumnIndexOrThrow(COL_BIZ_NUMBER)),
                    supportContents = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUPPORT_CONTENTS)),
                    evalGrade = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVAL_GRADE)),
                    capacityUnit = cursor.getString(cursor.getColumnIndexOrThrow(COL_CAPACITY_UNIT)),
                    capacityCount = cursor.getString(cursor.getColumnIndexOrThrow(COL_CAPACITY_COUNT)),
                    isOperating = cursor.getString(cursor.getColumnIndexOrThrow(COL_IS_OPERATING)),
                    entryTarget = cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_TARGET)),
                    entryPeriod = cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_PERIOD)),
                    entryProcess = cursor.getString(cursor.getColumnIndexOrThrow(COL_ENTRY_PROCESS)),
                    hasParking = cursor.getString(cursor.getColumnIndexOrThrow(COL_HAS_PARKING)),
                    nearbySubway = cursor.getString(cursor.getColumnIndexOrThrow(COL_NEARBY_SUBWAY)),
                    nearbyBusStop = cursor.getString(cursor.getColumnIndexOrThrow(COL_NEARBY_BUS_STOP)),
                    createdDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_DATE)),
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID))  // 추가
                )
                list.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Delete by facility name and user_id (삭제 시 사용자별 구분)
    fun deleteFamilyItemByFacilityNameAndUser(facilityName: String, userId: String): Int {
        val db = writableDatabase
        val result = db.delete(
            TABLE_NAME,
            "$COL_FACILITY_NAME = ? AND $COL_USER_ID = ?",
            arrayOf(facilityName, userId)
        )
        db.close()
        return result
    }
}
