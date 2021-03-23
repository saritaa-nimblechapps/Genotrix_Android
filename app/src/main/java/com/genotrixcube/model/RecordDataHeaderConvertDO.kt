package com.genotrixcube.model

import java.io.Serializable
import java.util.*

data class RecordDataHeaderConvertDO (val reacordId : String,
                                      val patientId : String,
                                      val regentId : String,
                                      val userId : String,
                                      val testDate : Date,
                                      val testTime : String
) : Serializable