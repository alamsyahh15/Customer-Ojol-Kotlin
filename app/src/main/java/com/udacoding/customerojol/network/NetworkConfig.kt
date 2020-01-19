package com.udacoding.customerojol.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkConfig {

    fun getOkhttp(): OkHttpClient{
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return  OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .build()
    }


    fun getRetrofit():Retrofit{
        val retrofit  = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/directions/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOkhttp())
            .build()

        return retrofit
    }

    fun getService():ApiService{
        val service: ApiService = getRetrofit().create(ApiService::class.java)
        return service
    }

    fun getRetrofitFcm():Retrofit{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOkhttp())
            .build()
        return retrofit
    }

    fun getServiceFcm():ApiService{
        val service : ApiService = getRetrofitFcm().create(ApiService::class.java)
        return service
    }

}