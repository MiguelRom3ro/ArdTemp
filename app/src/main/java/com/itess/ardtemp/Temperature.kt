package com.itess.ardtemp

import com.google.gson.annotations.SerializedName

data class Temperature(
    @SerializedName("temperature") val temperature : Int,
    @SerializedName("date") val date : String,
    @SerializedName("time") val time : String
)
