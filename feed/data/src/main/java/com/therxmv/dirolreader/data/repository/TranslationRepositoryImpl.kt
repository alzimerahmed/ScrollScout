package com.therxmv.dirolreader.data.repository

import com.therxmv.dirolreader.data.source.remote.translation.TranslationSource
import com.therxmv.dirolreader.domain.repository.TranslationRepository
import java.util.Locale
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    private val translationSource: TranslationSource,
) : TranslationRepository {

    override suspend fun translate(text: String): String =
        translationSource.translate(
            text = text,
            targetLanguage = Locale.getDefault().language.ifEmpty { "en" },
        )
}
