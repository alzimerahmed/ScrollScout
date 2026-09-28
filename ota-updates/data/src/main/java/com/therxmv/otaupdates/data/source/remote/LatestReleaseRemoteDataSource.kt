package com.therxmv.otaupdates.data.source.remote

import android.util.Log
import com.google.gson.JsonParseException
import com.therxmv.common.GithubRepo.GITHUB_REPO
import com.therxmv.common.GithubRepo.GITHUB_USERNAME
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class LatestReleaseRemoteDataSource @Inject constructor(
    private val apiService: GithubApiService,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) {
    // Only typed exceptions are caught — CancellationException propagates.
    suspend fun getLatestRelease() = withContext(ioDispatcher) {
        try {
            apiService.getLatestRelease(GITHUB_USERNAME, GITHUB_REPO)
        } catch (e: IOException) {
            Log.e(TAG, "Latest release request failed", e)
            null
        } catch (e: HttpException) {
            Log.e(TAG, "Latest release request failed with code ${e.code()}", e)
            null
        } catch (e: JsonParseException) {
            Log.e(TAG, "Latest release response is malformed", e)
            null
        }
    }

    private companion object {
        const val TAG = "LatestReleaseRemoteDataSource"
    }
}
