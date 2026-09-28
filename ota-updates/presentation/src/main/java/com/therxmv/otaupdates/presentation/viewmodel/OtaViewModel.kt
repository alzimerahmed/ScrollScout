package com.therxmv.otaupdates.presentation.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.therxmv.otaupdates.domain.models.LatestReleaseModel
import com.therxmv.otaupdates.domain.usecase.DownloadUpdateUseCase
import com.therxmv.otaupdates.domain.usecase.GetLatestReleaseUseCase
import com.therxmv.otaupdates.presentation.viewmodel.utils.OtaUiEvent
import com.therxmv.otaupdates.presentation.viewmodel.utils.OtaUiState
import com.therxmv.otaupdates.presentation.viewmodel.utils.toDownloadState
import com.therxmv.sharedpreferences.repository.AppSharedPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class OtaViewModel @Inject constructor(
    private val getLatestReleaseUseCase: GetLatestReleaseUseCase,
    private val downloadUpdateUseCase: DownloadUpdateUseCase,
    private val appSharedPrefsRepository: AppSharedPrefsRepository,
    @Named("VersionCode") private val versionCode: Int,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OtaUiState>(OtaUiState.InitialState)
    val uiState = _uiState.asStateFlow()

    private lateinit var updatePrefsListener: SharedPreferences.OnSharedPreferenceChangeListener

    init {
        loadLatestRelease()
    }

    fun onEvent(event: OtaUiEvent) {
        when (event) {
            is OtaUiEvent.Retry -> loadLatestRelease()
            is OtaUiEvent.DownloadUpdate -> downloadUpdate(event.updateModel)
            is OtaUiEvent.InstallUpdate -> installUpdate(event.context, event.updateModel)
        }
    }

    private fun isUpdateFileExists(updateModel: LatestReleaseModel): Boolean {
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, updateModel.fileName)

        return file.exists()
    }

    private fun isUpdateDownloaded(isDownloaded: Boolean, updateModel: LatestReleaseModel) {
        _uiState.update {
            (isDownloaded && isUpdateFileExists(updateModel)).toDownloadState(updateModel)
        }
    }

    private fun setIsUpdateDownloadedListener(updateModel: LatestReleaseModel) {
        updatePrefsListener = appSharedPrefsRepository.isUpdateDownloadedChangeListener { isDownloaded ->
            isUpdateDownloaded(isDownloaded = isDownloaded, updateModel = updateModel)
            appSharedPrefsRepository.unregisterChangeListener(updatePrefsListener)
        }

        appSharedPrefsRepository.registerChangeListener(updatePrefsListener)
    }

    private fun loadLatestRelease() {
        _uiState.update { OtaUiState.InitialState }

        viewModelScope.launch(ioDispatcher) {
            val release = getLatestReleaseUseCase()

            if (release == null) {
                _uiState.update { OtaUiState.Error() }
            } else {
                isUpdateDownloaded(
                    isDownloaded = appSharedPrefsRepository.isUpdateDownloaded,
                    updateModel = release,
                )
            }
        }
    }

    private fun downloadUpdate(updateModel: LatestReleaseModel?) {
        updateModel?.let { model ->
            val downloadId = downloadUpdateUseCase(model)

            if (downloadId == INVALID_DOWNLOAD_ID) {
                _uiState.update { OtaUiState.Error(model) }
            } else {
                _uiState.update { OtaUiState.Downloading(model) }
                setIsUpdateDownloadedListener(model)
            }
        }
    }

    private fun installUpdate(context: Context, update: LatestReleaseModel?) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, update?.fileName.orEmpty())
        val uri = resolveApkUri(context, file)

        if (uri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, update?.contentType ?: APK_MIME_TYPE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "No activity can handle the apk install intent", e)
            }
        }
    }

    // FileProvider is required on every supported API level: with targetSdk 34 a
    // file:// uri throws FileUriExposedException even on Android < 9.
    private fun resolveApkUri(context: Context, file: File): Uri? =
        if (file.exists()) {
            try {
                FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.provider", file)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Cannot resolve FileProvider uri for $file", e)
                null
            }
        } else {
            null
        }

    private companion object {
        const val TAG = "OtaViewModel"
        const val INVALID_DOWNLOAD_ID = -1L
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
    }
}
