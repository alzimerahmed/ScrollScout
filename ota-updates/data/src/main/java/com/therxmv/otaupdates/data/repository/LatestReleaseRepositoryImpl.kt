package com.therxmv.otaupdates.data.repository

import com.therxmv.otaupdates.data.models.toDomain
import com.therxmv.otaupdates.data.source.remote.LatestReleaseRemoteDataSource
import com.therxmv.otaupdates.domain.repository.LatestReleaseRepository
import javax.inject.Inject

class LatestReleaseRepositoryImpl @Inject constructor(
    private val latestReleaseRemoteDataSource: LatestReleaseRemoteDataSource,
) : LatestReleaseRepository {
    override suspend fun getLatestRelease() =
        latestReleaseRemoteDataSource.getLatestRelease()?.let { json ->
            // Incomplete/malformed payloads must not crash the caller.
            runCatching { json.toDomain() }.getOrNull()
        }
}