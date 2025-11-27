package dev.leotoloza.avengersapp.domain.usecase

import dev.leotoloza.avengersapp.data.repository.RemoteConfigRepository
import javax.inject.Inject

class GetAuthUiConfigUseCase @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository
) {
    suspend operator fun invoke(): Boolean {
        remoteConfigRepository.fetchAndActivate()
        return remoteConfigRepository.isFirebaseUiEnabled()
    }
}
