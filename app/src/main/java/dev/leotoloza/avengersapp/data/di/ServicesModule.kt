package dev.leotoloza.avengersapp.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.leotoloza.avengersapp.BuildConfig
import dev.leotoloza.avengersapp.data.service.AvengersClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
private const val BASE_URL = "https://api.disneyapi.dev/"

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        // configuracion para que solo muestre los logs en modo DEBUG
        val level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY // Muestra headers, body, etc.
        } else {
            HttpLoggingInterceptor.Level.NONE // No muestra nada en producción
        }
        return HttpLoggingInterceptor().setLevel(level)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAvengersApiClient(retrofit: Retrofit): AvengersClient {
        return retrofit.create(AvengersClient::class.java)
    }

    @Provides
    @Singleton
    fun provideFirestore(): com.google.firebase.firestore.FirebaseFirestore {
        return com.google.firebase.firestore.FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideRemoteConfig(): com.google.firebase.remoteconfig.FirebaseRemoteConfig {
        val remoteConfig = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance()
        val configSettings = com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        return remoteConfig
    }
}