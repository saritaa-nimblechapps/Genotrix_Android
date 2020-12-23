package com.mystrica.DatabaseHendler

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.mystrica.model.FileDataDO




class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "EmployeeDatabase"
        private val TABLE_CONTACTS = "EmployeeTable"
        private val TABLE_RECORD = "RECORD"

        private val TABLE_RECORD_ID = "record_id"
        private val TABLE_REACTION_TIME = "reaction_time"
        private val TABLE_REACTION_RESULT = "reaction_result"
        private val TABLE__PATIENT_ID = "patient_id"
        private val TABLE_REAGENT_ID = "reagent_id"
        private val TABLE_USER_ID = "user_id"
        private val TABLE_SAMPLE_TYPE = "sample_type"
        private val TABLLE_TEST_PERFORMED = "test_perfomed"
        private val TABLE_TESTING_DATE_TIME = "testing_date_time"
        private val TABLE_QC_PERFORMED = "qc_performed"
        private val TABLE_QC_DATE_TIME = "qc_data_time"
        private val TABLE_REACTION_DURATION = "reaction_duration"
        private val TABLE_INTERVAL = "interval"
        private val TABLE_END_OF_PROCESS_FLAG = "end_of_process_flag"
        private val TABLE_SAMPLE_ADDITION_DATE_TIME = "sample_addition_date_time"
        private val TABLE__ACQUISION_START_DATE_TIME = "acquision_satart_date_time"
        private val TABLE_SAMPLE_ADDITION = "sample_addition"
        private val TABLE_DEVICE_ID = "device_id"
        private val TABLE_SOFTWARE_VERSION = "software_version"
        private var TABLE_TEST_REOCORD_ID = "record_test_id"

        //        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
//        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
//                 + KEY_NAME + " TEXT,"
//                + KEY_EMAIL + " TEXT" + ")")
//        db?.execSQL(CREATE_CONTACTS_TABLE)


        val CREATE_RECORD_TABLE = ("CREATE TABLE " + TABLE_RECORD + "("
                + TABLE_RECORD_ID + " TEXT,"
                + TABLE_REACTION_TIME + " TEXT,"
                + TABLE_REACTION_RESULT + " TEXT,"
                + TABLE__PATIENT_ID + " TEXT,"
                + TABLE_REAGENT_ID + " TEXT,"
                + TABLE_USER_ID + " TEXT,"
                + TABLE_SAMPLE_TYPE + " TEXT,"
                + TABLLE_TEST_PERFORMED + " TEXT,"
                + TABLE_TESTING_DATE_TIME + " TEXT,"
                + TABLE_QC_PERFORMED + " TEXT,"
                + TABLE_QC_DATE_TIME + " TEXT,"
                + TABLE_REACTION_DURATION + " TEXT,"
                + TABLE_INTERVAL + " TEXT,"
                + TABLE_END_OF_PROCESS_FLAG + " TEXT,"
                + TABLE_SAMPLE_ADDITION_DATE_TIME + " TEXT,"
                + TABLE__ACQUISION_START_DATE_TIME + " TEXT,"
                + TABLE_SAMPLE_ADDITION + " TEXT,"
                + TABLE_DEVICE_ID + " TEXT,"
                + TABLE_SOFTWARE_VERSION + " TEXT,"
                + TABLE_TEST_REOCORD_ID + " TEXT" + ")")
        db?.execSQL(CREATE_RECORD_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD)
        onCreate(db)
    }


    //method to insert data
    fun addEmployee(emp: EmpModelClass): Long {

        var success: Long = -1
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(KEY_NAME, emp.userName) // EmpModelClass Name
            contentValues.put(KEY_EMAIL, emp.userEmail) // EmpModelClass Phone
            // Inserting Row
            success = db.insert(TABLE_CONTACTS, null, contentValues)
            //2nd argument is String containing nullColumnHack
            db.close() // Closing database connection

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return success
    }


    fun addTestRecord(testRecord: FileDataDO): Long {

        var success: Long = -1
        try {

            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(TABLE_RECORD_ID, testRecord.reacordId)
            contentValues.put(TABLE_REACTION_TIME,testRecord.reactionTime )
            contentValues.put(TABLE_REACTION_RESULT,testRecord.reactionResult )
            contentValues.put(TABLE__PATIENT_ID,testRecord.patientId )
            contentValues.put(TABLE_REAGENT_ID,testRecord.regentId )
            contentValues.put(TABLE_USER_ID,testRecord.userId )
            contentValues.put(TABLE_SAMPLE_TYPE,testRecord.sampleType )
            contentValues.put(TABLLE_TEST_PERFORMED,testRecord.testPerformed )
            contentValues.put(TABLE_TESTING_DATE_TIME,testRecord.testDateTime )
            contentValues.put(TABLE_QC_PERFORMED,testRecord.qcPerformed )
            contentValues.put(TABLE_QC_DATE_TIME,testRecord.lastQcDateTime )
            contentValues.put(TABLE_REACTION_DURATION,testRecord.reactionDuratio )
            contentValues.put(TABLE_INTERVAL,testRecord.interval )
            contentValues.put(TABLE_END_OF_PROCESS_FLAG,testRecord.endOfPrescessFalg )
            contentValues.put(TABLE_SAMPLE_ADDITION_DATE_TIME,testRecord.sampleDateTime )
            contentValues.put(TABLE__ACQUISION_START_DATE_TIME,testRecord.acquisionDateTime )
            contentValues.put(TABLE_SAMPLE_ADDITION,testRecord.sampleOkpress )
            contentValues.put(TABLE_DEVICE_ID,testRecord.deviceId )
            contentValues.put(TABLE_SOFTWARE_VERSION,testRecord.softwareVersion )
            contentValues.put(TABLE_TEST_REOCORD_ID,testRecord.id )

            // Inserting Row
            success = db.insert(TABLE_RECORD, null, contentValues)
            //2nd argument is String containing nullColumnHack
            db.close() // Closing database connection

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return success
    }

    //method to read data
    fun viewEmployee(): List<EmpModelClass> {
        val empList: ArrayList<EmpModelClass> = ArrayList<EmpModelClass>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Double
        var userName: String
        var userEmail: String
        if (cursor.moveToFirst()) {
            do {
//                userId = cursor.getDouble(cursor.getColumnIndex("id"))
                userName = cursor.getString(cursor.getColumnIndex("name"))
                userEmail = cursor.getString(cursor.getColumnIndex("email"))
                val emp = EmpModelClass(userName = userName, userEmail = userEmail)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

    fun getLastRecord(): String {
        var lastRecordId: String = ""

        val db = this.readableDatabase

        val countqurery= "SELECT * FROM " + TABLE_RECORD.toString()

        val selectQuery =
            "SELECT * FROM " + TABLE_RECORD.toString() + " ORDER BY "+ TABLE_RECORD_ID.toString() +" DESC LIMIT 1"

        var countCursor: Cursor? = null
        var cursor: Cursor? = null

        countCursor = db.rawQuery(countqurery, null)

        if(countCursor.count>0){
            try {
                cursor = db.rawQuery(selectQuery, null)

                if (cursor.moveToFirst()) {
                    lastRecordId = cursor.getString(cursor.getColumnIndex(TABLE_RECORD_ID))
                }
                cursor.close()

            } catch (e: SQLiteException) {
                db.execSQL(selectQuery)
            }

        }





        return lastRecordId
    }


    //method to read data
    fun viewTestReocord(): List<FileDataDO> {
        val testList: ArrayList<FileDataDO> = ArrayList<FileDataDO>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        val testDetailsDO: FileDataDO? = null
        if (cursor.moveToFirst()) {
            do {

                val record_id=cursor.getString(cursor.getColumnIndex("record_id"))
                val reactionTime = cursor.getString(cursor.getColumnIndex("reaction_time"))
                val reaction_result = cursor.getString(cursor.getColumnIndex("reaction_result"))
                val patient_id = cursor.getString(cursor.getColumnIndex("patient_id"))
                val reagent_id = cursor.getString(cursor.getColumnIndex("reagent_id"))
                val user_id = cursor.getString(cursor.getColumnIndex("user_id"))
                val sample_type = cursor.getString(cursor.getColumnIndex("sample_type"))
                val test_perfomed = cursor.getString(cursor.getColumnIndex("test_perfomed"))
                val testing_date_time = cursor.getString(cursor.getColumnIndex("testing_date_time"))
                val qc_performed = cursor.getString(cursor.getColumnIndex("qc_performed"))
                val qc_data_time = cursor.getString(cursor.getColumnIndex("qc_data_time"))
                val reaction_duratin = cursor.getString(cursor.getColumnIndex("reaction_duration"))
                val interval = cursor.getString(cursor.getColumnIndex("interval"))
                val end_of_process_flag =
                    cursor.getString(cursor.getColumnIndex("end_of_process_flag"))
                val sample_addition_date_time =
                    cursor.getString(cursor.getColumnIndex("sample_addition_date_time"))
                val acquision_satart_date_time =
                    cursor.getString(cursor.getColumnIndex("acquision_satart_date_time"))
                val sample_addition = cursor.getString(cursor.getColumnIndex("sample_addition"))
                val device_id = cursor.getString(cursor.getColumnIndex("device_id"))
                val software_version = cursor.getString(cursor.getColumnIndex("software_version"))
                val record_test_id = cursor.getString(cursor.getColumnIndex("record_test_id"))

                val objtestData = FileDataDO(
                    reacordId= record_id,
                    reactionTime = reactionTime,
                    reactionResult = reaction_result,
                    patientId = patient_id,
                    regentId = reagent_id,
                    userId = user_id,
                    sampleType = sample_type,
                    testPerformed = test_perfomed,
                    testDateTime = testing_date_time,
                    qcPerformed = qc_performed,
                    lastQcDateTime = qc_data_time,
                    reactionDuratio = reaction_duratin,
                    interval = interval,
                    endOfPrescessFalg = end_of_process_flag,
                    sampleDateTime = sample_addition_date_time,
                    acquisionDateTime = acquision_satart_date_time,
                    sampleOkpress = sample_addition,
                    deviceId = device_id,
                    softwareVersion = software_version,
                    id = record_test_id
                )

                testList.add(objtestData)
            } while (cursor.moveToNext())
        }
        return testList
    }

    //method to update data
    fun updateEmployee(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId)
        contentValues.put(KEY_NAME, emp.userName) // EmpModelClass Name
        contentValues.put(KEY_EMAIL, emp.userEmail) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues, "id=" + emp.userName, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to delete data
    fun deleteEmployee(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS, "id=" + emp.userName, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

}