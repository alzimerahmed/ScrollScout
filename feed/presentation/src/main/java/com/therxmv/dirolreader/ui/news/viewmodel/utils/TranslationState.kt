package com.therxmv.dirolreader.ui.news.viewmodel.utils

sealed class TranslationState {
    data object Idle : TranslationState()
    data object Loading : TranslationState()
    data class Ready(val text: String) : TranslationState()
    data object Error : TranslationState()
}
