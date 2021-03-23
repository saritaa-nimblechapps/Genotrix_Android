package com.genotrixcube.model

import java.io.Serializable

data class LED_DO(
    var pwm_value: Byte,
    var current_value: Byte
 ) : Serializable