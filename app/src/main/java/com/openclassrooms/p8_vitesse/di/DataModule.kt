package com.openclassrooms.p8_vitesse.di

import com.openclassrooms.p8_vitesse.data.network.ConverterClient
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val dataModule: Module = module {

    // OkHttpClient for network operations, with logging interceptor
    single {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }

    // Moshi instance for JSON serialization/deserialization
    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    // Retrofit instance for API calls, configured with base URL, Moshi converter, and OkHttpClient
    single {
        Retrofit.Builder()
            .baseUrl("https://latest.currency-api.pages.dev/v1") // ou l'adresse IP de votre ordinateur pour un appareil physique
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
    }

    // LoginClient instance created from Retrofit
    single {
        get<Retrofit>().create(ConverterClient::class.java)
    }


    // LoginRepository with dependency on LoginClient
    single { CandidateRepository(get()) }

}