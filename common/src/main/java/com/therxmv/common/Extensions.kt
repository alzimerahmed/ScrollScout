package com.therxmv.common

import androidx.compose.ui.Modifier

fun String.extractVersion() = this.filter { it.isDigit() }.toInt()

inline fun Modifier.thenIf(
    predicate: Boolean,
    modifier: Modifier.() -> Modifier,
): Modifier =
    if (predicate) {
        this.modifier()
    } else {
        this
    }
