package com.openclassrooms.p8_vitesse.di

import com.openclassrooms.p8_vitesse.data.network.ExchangeRatesClient
import com.openclassrooms.p8_vitesse.data.repository.ExchangeRatesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Koin module for providing network-related dependencies.
 */
val dataModule: Module = module {

    /**
     * Provides an [OkHttpClient] instance with logging capabilities for network requests.
     */
    single {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }

    /**
     * Provides a [Moshi] instance for JSON serialization/deserialization.
     */
    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    /**
     * Provides a [Retrofit] instance configured with the base URL, [Moshi] converter, and [OkHttpClient].
     */
    single {
        Retrofit.Builder()
            .baseUrl("https://latest.currency-api.pages.dev/v1/")
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
    }

    /**
     * Provides an [ExchangeRatesClient] instance created from [Retrofit].
     */
    single {
        get<Retrofit>().create(ExchangeRatesClient::class.java)
    }

    /**
     * Provides an [ExchangeRatesRepository] instance with dependency on [ExchangeRatesClient].
     */
    single { ExchangeRatesRepository(get()) }
}