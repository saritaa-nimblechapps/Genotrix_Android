package com.mystrica.DatabaseHendler

import java.io.Serializable


data class TestDetailsDO  (
                      val recordId : String,
                      val reactionTime:String ,
                      val reactionResult: String,
                      val patinent_id: String,
                      val regent_id: String,
                      val userid: String,
                      val sampeType: String,
                      val testPerormed: String,
                      val testDateTime: String,
                      val qcPerformed: String,
                      val qcDateTime: String,
                      val interval: String,
                      val endOfProgressFlag: String,
                      val sampleAdditionDateTime: String,
                      val acquisionStartDateTime: String,
                      val sampleAddition: String,
                      val deviceId: String,
                      val softwareVersion: String,
                      val record_Id: String
)
                     : Serializable