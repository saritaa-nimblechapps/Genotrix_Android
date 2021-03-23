package com.genotrixcube.util

import java.util.*

class Constant {


    companion object {


        val MY_TEST_SERVICE_UUID = UUID.fromString("8ad2b6b0-6bd6-413e-9f6a-58021fad2f30")

        //Characteristics
        val MYSTERIA_CHARACTERISTIC_TEST_UUID =
            UUID.fromString("8ad2b6b1-6bd6-413e-9f6a-58021fad2f30")

        val MYSTERIA_CHARACTERISTIC_RESILT_UUID =
            UUID.fromString("8ad2b6b2-6bd6-413e-9f6a-58021fad2f30")

        val MYSTERIA_CHARACTERISTIC_CALIBRATE_UUID =
            UUID.fromString("8ad2b6b3-6bd6-413e-9f6a-58021fad2f30")

        val HARDWARE_BATTERY_UUID = UUID.fromString("8ad2b6b4-6bd6-413e-9f6a-58021fad2f30")

        val MYSTERIA_TEST_RSSI = UUID.fromString("8ad2b6b5-6bd6-413e-9f6a-58021fad2f30")

        val CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805F9B34FB"

        val intconvert : Int= 144
        val pwmconvert : Int=  245


        val addExtraIntrval : Long=  24000  // few seconds only add

        val isconnecting : String ="isconnecting"

        val deviceUniqueId : String ="deviceUniqueId"
        val deviceUniqueName : String ="deviceUniqueName"

        var resultvalue = "resultvalue"
        var userprefid : String = "userid"
        var selectTest = "TestName"
        var selectSubTest = "subTestName"

        var strQcUse = "run qc"
        var strDate = "date"

        var qcCounter = "qcCounter"

        var maxMint: String="maxmint"
        var limitMilisecond : String="limitsecond"

        var qcMaxmiliSecond = "qcMaxmiliSecond"
        var qcStartmiliSecond = "qcStartMaxmiliSecond"
        var qcintervalSecond = "qcintervalSecond"
        var testinterval = "testintervalSecond"
        var estimateFlag="estimateFlag"
        var testFlag = "testFlag"
        var sample_date_time = "sample_additional_date_time"
        var qcStartintervalSecond = "qcStartintervalSecond"
        var qcMaxMiliAddSecond = "qcMaxMiliAddSecond"


        const val maxDefaultMinit: Long = 624000
        const val maxDefalultInterval: Long = 10000
        const val dropsDefault : Int= 3
        const val estimatedFlag : Int= 10
        const val estimatedFlag_int : Int=10000
        const val x = 0.07f

        var lastQcDateTime :String ="last_qc_date_time"

        var deviceName : String ="device_name"
        var testPerformd : String ="tetPerfomed"
        var test_date : String ="txt_test_datee4result"
        var test_time : String ="txt_test_time4result"
        var drops : String ="Drops"
        var reaction : String ="reaction"
        var acquisistionStartTime : String="acquisistionStartTime"

        var arrayLedSettinglist :String="arrayLedSettinglist"

    }
}