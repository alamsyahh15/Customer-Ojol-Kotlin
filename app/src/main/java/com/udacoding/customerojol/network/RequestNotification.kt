package com.udacoding.customerojol.network

import com.google.gson.annotations.SerializedName
import com.udacoding.customerojol.ui.home.model.Booking

class RequestNotification {
    @SerializedName("to")
    var token: String ? =null

    @SerializedName("data")
    var sendNotification: Booking? =null

    @SerializedName("notification")
    var notification: String? =null
}