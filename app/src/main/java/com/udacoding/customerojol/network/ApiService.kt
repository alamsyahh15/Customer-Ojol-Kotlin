package com.udacoding.customerojol.network

import com.udacoding.customerojol.ui.home.model.ResultRoute
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("json")
    fun actionRouter(@Query("origin") origin : String,
                     @Query("destination") destination: String,
                     @Query("key") key: String) : Flowable<ResultRoute>

    @Headers(
        "Authorization: key=AAAAVwxtFfY:APA91bFdFlLIZ7PSCHHi9a6mbRvoewrIdq9GQfVYH5oH2xsJ_72I0L2ugUrxP02Gf5dhcJfPQOUG8KbJSVaOxG5rZqnhoy-xQwhEQfFDP7NKzRcjxAdPCHPdcNGhd8fcRtK9PvmyXMT0",
        "Content-Type:application/json"
    )

    @POST("fcm/send")
    fun sendNotification(@Body requestNotofication : RequestNotification) : Call<ResponseBody>

}