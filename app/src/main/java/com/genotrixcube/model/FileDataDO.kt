package com.genotrixcube.model

import java.io.Serializable
import java.io.StringReader


val header = listOf("Reaction Time (Seconds)", "Reaction Result", "Patient ID","Reagent ID",
    "User ID","Sample Type","Test Performed","Testing Date and Time","QC Performed","Last QC Date and Time","Reaction Duration",
    "Intervals","End of Process Flag","Sample Addition Date and Time","Acquision Start Date and Time",
    "Sample Addition / Reaction Started by Pressing OK","Device ID","Software Version","ID")


data class FileDataDO (
    val reacordId : String,
    val reactionTime : String,
    val reactionResult : String,
    val patientId : String,
    val regentId : String,
    val userId : String,
    val sampleType : String,
    val testPerformed : String,
    val testDate : String,
    val testTime : String,
    val qcPerformed :String,
    val lastQcDateTime : String,
    val reactionDuratio :String,
    val interval : String,
    val endOfPrescessFalg : String,
    val sampleDateTime : String,
    val acquisionDateTime :String,
    val sampleOkpress : String,
    val deviceId : String,
    val softwareVersion : String,
    val id : String,
    val record_title: String,
    val estimated_second: String,
    val max_mailisecond: String,
    val estimated_flag_mint  : String
) : Serializable