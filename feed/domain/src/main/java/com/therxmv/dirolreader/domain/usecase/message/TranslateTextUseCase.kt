package com.therxmv.dirolreader.domain.usecase.message

import com.therxmv.dirolreader.domain.repository.TranslationRepository
import javax.inject.Inject

class TranslateTextUseCase @Inject constructor(
    private val translationRepository: TranslationRepository,
) {

    suspend operator fun invoke(text: String): String =
        translationRepository.translate(text)
}
