package mx.cubiccoding.model.networking

import mx.cubiccoding.model.networking.apis.CubicCodingMXApi
import mx.cubiccoding.model.networking.apis.CubicCodingManagerApi
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_MANAGER_URL
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_MX_URL
import mx.cubiccoding.model.utils.Constants.Companion.HTTP_WAIT_TIME_IN_SECS
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RequestsManager {

    lateinit var cubicCodingMXApi: CubicCodingMXApi
    lateinit var cubicCodingManagerApi: CubicCodingManagerApi

    init {
        initializeRetrofit()
    }

    private fun initializeRetrofit() {

        val httpClient = with (OkHttpClient.Builder()) {
            readTimeout(HTTP_WAIT_TIME_IN_SECS, TimeUnit.SECONDS)
            writeTimeout(HTTP_WAIT_TIME_IN_SECS, TimeUnit.SECONDS)
            connectTimeout(HTTP_WAIT_TIME_IN_SECS, TimeUnit.SECONDS)
            build()
        }

        setupMXApi(httpClient)
        setupManagerApi(httpClient)
    }

    private fun setupMXApi(httpClient: OkHttpClient) {
        cubicCodingMXApi = with (Retrofit.Builder()) {
            baseUrl(CUBICCODING_MX_URL)
            addConverterFactory(GsonConverterFactory.create())
            client(httpClient)
            build()
        }.create(CubicCodingMXApi::class.java)
    }

    private fun setupManagerApi(httpClient: OkHttpClient) {
        cubicCodingManagerApi = with (Retrofit.Builder()) {
            baseUrl(CUBICCODING_MANAGER_URL)
            addConverterFactory(GsonConverterFactory.create())
            client(httpClient)
            build()
        }.create(CubicCodingManagerApi::class.java)
    }
}
