package com.therxmv.dirolreader.data.source.remote.translation

import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

/**
 * Parses the response of the Google Translate `translate_a/single` endpoint:
 * a JSON array whose first element is a list of translated segments, each
 * segment being an array with the translated chunk at index 0.
 */
object TranslationParser {

    fun parse(body: String): String {
        val root = JsonParser.parseString(body)
        val segments = root.asJsonArray.getOrNull(0)?.asJsonArray
            ?: throw IllegalArgumentException("Unexpected translation response")

        return segments.joinToString(separator = "") { segment ->
            segment.asJsonArray.getOrNull(0)?.asString ?: ""
        }
    }
}

interface TranslationApi {

    @GET("translate_a/single")
    suspend fun translate(
        @Query("client") client: String = "gtx",
        @Query("sl") sourceLanguage: String = "auto",
        @Query("tl") targetLanguage: String,
        @Query("dt") dataType: String = "t",
        @Query("q") text: String,
    ): ResponseBody
}

class TranslationRemoteSource @Inject constructor(
    private val translationApi: TranslationApi,
) : TranslationSource {

    override suspend fun translate(text: String, targetLanguage: String): String =
        TranslationParser.parse(translationApi.translate(targetLanguage = targetLanguage, text = text).string())
}

interface TranslationSource {
    suspend fun translate(text: String, targetLanguage: String): String
}
