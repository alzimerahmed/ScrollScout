package com.therxmv.dirolreader.domain.repository

interface TranslationRepository {

    /**
     * Translates [text] into the device's current locale language.
     * Returns the translated string; throws on network/parse failure.
     */
    suspend fun translate(text: String): String
}
