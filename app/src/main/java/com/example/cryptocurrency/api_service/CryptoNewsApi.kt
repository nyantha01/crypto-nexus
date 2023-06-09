package com.example.cryptocurrency.api_service
import com.example.cryptocurrency.model.model5.NftsNews
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoNewsApi {

    @GET("everything")
    suspend fun getCryptoNews(
        @Query("q") q: String,
        @Query("apiKey") apiKey: String,
    ) : Response<NftsNews>
}