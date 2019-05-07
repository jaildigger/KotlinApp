package com.ec.expresscheck.rest

import com.ec.expresscheck.BuildConfig
import com.ec.expresscheck.rest.client.*
import com.ec.expresscheck.rest.interceptor.AuthInterceptor
import com.ec.expresscheck.rest.interceptor.TestInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by jaydee on 20.12.17.
 */
object Services {

    var loggingInterceptor = HttpLoggingInterceptor()
    var testInterceptor = TestInterceptor()
    private val logging: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(testInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }
    val publicRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(logging)
            .build()
    }
    val secureRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(oAuthClient)
            .build()
    }
    val oAuthClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(testInterceptor)
            .addInterceptor(AuthInterceptor())
            .build()
    }

    init {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    }

    val authApi: AuthApi by lazy {
        publicRetrofit.create(AuthApi::class.java)
    }
    val patronApi: PatronApi by lazy {
        secureRetrofit.create(PatronApi::class.java)
    }

    val venuesLoggedApi: VenuesApi by lazy {
        secureRetrofit.create(VenuesApi::class.java)
    }

    val venuesPublicApi: VenuesApi by lazy {
        publicRetrofit.create(VenuesApi::class.java)
    }

    val eventsApi: EventsApi by lazy {
        publicRetrofit.create(EventsApi::class.java)
    }

    val cardsApi: CardApi by lazy {
        secureRetrofit.create(CardApi::class.java)
    }

    val paymentsApi: PaymentsApi by lazy {
        secureRetrofit.create(PaymentsApi::class.java)
    }

    val alertsApi: AlertsApi by lazy {
        secureRetrofit.create(AlertsApi::class.java)
    }

    val loyaltyApi: LoyaltyApi by lazy { secureRetrofit.create(LoyaltyApi::class.java) }




}
