package com.genotrixcube.model

import java.io.Serializable

data class HomeDataDO(
    var title: String,
    var isEnable: Boolean
) : Serializable