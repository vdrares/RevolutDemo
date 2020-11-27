package com.example.revolutdemo.networking

import com.example.revolutdemo.model.RatesResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RatesService {

    companion object {
        const val RATES_REQUEST = "https://hiring.revolut.codes/api/android/"
        const val BASE_QUERY = "base"
            fun create(): RatesService {
                val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(RATES_REQUEST)
                    .build()

                return retrofit.create(RatesService::class.java)
            }
    }

    @GET("latest")
    fun getRates(@Query(BASE_QUERY) base: String) : Observable<RatesResponse>

}