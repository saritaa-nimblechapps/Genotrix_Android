package com.genotrixcube.model

import java.io.Serializable

data class ReocrdDataHeaderDO(
    val reacordId : String,
    val patientId : String,
    val regentId : String,
    val userId : String,
    val testDate : String,
    val testTime : String
) : Serializable