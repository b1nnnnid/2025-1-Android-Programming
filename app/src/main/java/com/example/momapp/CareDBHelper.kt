import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.example.momapp.JsonCareItem

class CareDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "care_items.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "care_items"

        private const val COLUMN_ID = "childCareInstNo"  // 기본키
        private const val COLUMN_NAME = "childCareInstNm"
        private const val COLUMN_CRTRYM = "crtrYm"
        private const val COLUMN_UPID = "upChildCareInstNo"
        private const val COLUMN_UPNAME = "upChildCareInstNm"
        private const val COLUMN_THMM_SITTR_CNT = "thmmSittrCnt"
        private const val COLUMN_THMM_PTTGTH_TYPE_PSBLT_SITTR_CNT = "thmmPttgthTypePsbltSittrCnt"
        private const val COLUMN_THMM_CRTFT_PSSN_SITTR_CNT = "thmmCrtftPssnSittrCnt"
        private const val COLUMN_THMM_STRHT_LINK_PSBLT_SITTR_CNT = "thmmStrhtLinkPsbltSittrCnt"
        private const val COLUMN_ACMLT_CRTFT_PSSN_SITTR_CNT = "acmltCrtftPssnSittrCnt"

        private const val COLUMN_USER_ID = "user_id"  // 사용자 ID 추가
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID TEXT,
                $COLUMN_NAME TEXT,
                $COLUMN_CRTRYM TEXT,
                $COLUMN_UPID TEXT,
                $COLUMN_UPNAME TEXT,
                $COLUMN_THMM_SITTR_CNT INTEGER,
                $COLUMN_THMM_PTTGTH_TYPE_PSBLT_SITTR_CNT INTEGER,
                $COLUMN_THMM_CRTFT_PSSN_SITTR_CNT INTEGER,
                $COLUMN_THMM_STRHT_LINK_PSBLT_SITTR_CNT INTEGER,
                $COLUMN_ACMLT_CRTFT_PSSN_SITTR_CNT INTEGER,
                $COLUMN_USER_ID TEXT,
                PRIMARY KEY ($COLUMN_ID, $COLUMN_USER_ID)
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert or update by (childCareInstNo + userId) key
    fun insertCareItem(item: JsonCareItem, userId: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, item.childCareInstNo)
            put(COLUMN_NAME, item.childCareInstNm)
            put(COLUMN_CRTRYM, item.crtrYm)
            put(COLUMN_UPID, item.upChildCareInstNo)
            put(COLUMN_UPNAME, item.upChildCareInstNm)
            put(COLUMN_THMM_SITTR_CNT, item.thmmSittrCnt)
            put(COLUMN_THMM_PTTGTH_TYPE_PSBLT_SITTR_CNT, item.thmmPttgthTypePsbltSittrCnt)
            put(COLUMN_THMM_CRTFT_PSSN_SITTR_CNT, item.thmmCrtftPssnSittrCnt)
            put(COLUMN_THMM_STRHT_LINK_PSBLT_SITTR_CNT, item.thmmStrhtLinkPsbltSittrCnt)
            put(COLUMN_ACMLT_CRTFT_PSSN_SITTR_CNT, item.acmltCrtftPssnSittrCnt)
            put(COLUMN_USER_ID, userId)
        }

        val result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    // userId별 데이터 조회
    fun getCareItemsByUser(userId: String): List<JsonCareItem> {
        val careItems = mutableListOf<JsonCareItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId), null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val item = JsonCareItem(
                    crtrYm = getString(getColumnIndexOrThrow(COLUMN_CRTRYM)),
                    childCareInstNo = getString(getColumnIndexOrThrow(COLUMN_ID)),
                    childCareInstNm = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                    upChildCareInstNo = getString(getColumnIndexOrThrow(COLUMN_UPID)),
                    upChildCareInstNm = getString(getColumnIndexOrThrow(COLUMN_UPNAME)),
                    thmmSittrCnt = getInt(getColumnIndexOrThrow(COLUMN_THMM_SITTR_CNT)),
                    thmmPttgthTypePsbltSittrCnt = getInt(getColumnIndexOrThrow(COLUMN_THMM_PTTGTH_TYPE_PSBLT_SITTR_CNT)),
                    thmmCrtftPssnSittrCnt = getInt(getColumnIndexOrThrow(COLUMN_THMM_CRTFT_PSSN_SITTR_CNT)),
                    thmmStrhtLinkPsbltSittrCnt = getInt(getColumnIndexOrThrow(COLUMN_THMM_STRHT_LINK_PSBLT_SITTR_CNT)),
                    acmltCrtftPssnSittrCnt = getInt(getColumnIndexOrThrow(COLUMN_ACMLT_CRTFT_PSSN_SITTR_CNT))
                )
                careItems.add(item)
            }
        }
        cursor.close()
        return careItems
    }

    fun deleteCareItem(childCareInstNo: String, userId: String): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?",
            arrayOf(childCareInstNo, userId)
        )
        return result > 0
    }
}
