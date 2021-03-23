package com.genotrixcube.model

import java.io.Serializable
import java.util.ArrayList

data class TestTyepSampleDataDO(


    var title: String,
    var strSelectedLed: String,
    var update_vaule: LED_DO,
    var default_value: LED_DO,
    var isupdated: Boolean,
    var isEnable: Boolean,
   var ledTypeArray: ArrayList<DefaultLEDDO>
) : Serializable