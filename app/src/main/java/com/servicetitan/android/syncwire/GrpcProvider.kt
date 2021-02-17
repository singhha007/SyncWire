package com.servicetitan.android.syncwire

import com.squareup.wire.GrpcClient
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import sync.protocol.SyncServiceClient
import java.time.Duration

private const val TIMEOUT_DURATION = 10L
private const val HEADER_AUTH_TOKEN = "token"
private const val HEADER_DEVICE_ID = "deviceid"
private const val HEADER_USER_ID = "userid"

private const val GRPC_BASE_URL = "http://10.0.2.2:5005"

object GrpcProvider {

    fun provideSyncServiceClient() =
        GrpcClient.Builder()
            .client(provideOkHttpClient())
            .baseUrl(GRPC_BASE_URL)
            .build()
            .create(SyncServiceClient::class)

    private fun provideOkHttpClient() =
        OkHttpClient.Builder()
            .readTimeout(Duration.ofMinutes(TIMEOUT_DURATION))
            .writeTimeout(Duration.ofMinutes(TIMEOUT_DURATION))
            .callTimeout(Duration.ofMinutes(TIMEOUT_DURATION))
            .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .addInterceptor {
                it.proceed(
                    it.request().newBuilder()
                        .addHeader(HEADER_AUTH_TOKEN, "")
                        .addHeader(HEADER_DEVICE_ID, "55")
                        .addHeader(HEADER_USER_ID, "555")
                        .build()
                )
            }
            .build()
}