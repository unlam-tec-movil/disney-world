package dev.leotoloza.avengersapp.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.leotoloza.avengersapp.data.repository.CharactersRepositoryImpl
import dev.leotoloza.avengersapp.data.repository.FavoritesRepositoryImpl
import dev.leotoloza.avengersapp.domain.repository.CharactersRepository
import dev.leotoloza.avengersapp.domain.repository.FavoritesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
     @Binds
     @Singleton
     abstract fun bindCharactersRepository(
         charactersRepositoryImpl: CharactersRepositoryImpl
     ): CharactersRepository

     @Binds
     @Singleton
     abstract fun bindFavoritesRepository(
         favoritesRepositoryImpl: FavoritesRepositoryImpl
     ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindRemoteConfigRepository(
        remoteConfigRepositoryImpl: dev.leotoloza.avengersapp.data.repository.RemoteConfigRepositoryImpl
    ): dev.leotoloza.avengersapp.data.repository.RemoteConfigRepository
}