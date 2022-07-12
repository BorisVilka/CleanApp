package com.smartbooster.junkcleaner.network

import com.smartbooster.junkcleaner.model.RequestModel
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APIService {




  @POST("reff_id")
  fun register(@Body body: RequestModel): Call<ResponseBody>


  companion object {

    var retrofitService: APIService? = null

    fun getInstance() : APIService {

      if (retrofitService == null) {
        val retrofit = Retrofit.Builder()
          .client(OkHttpClient())
          .baseUrl("http://92.205.104.237")
          .addConverterFactory(GsonConverterFactory.create())
          .build()
        retrofitService = retrofit.create(APIService::class.java)
      }
      return retrofitService!!
    }
  }
}