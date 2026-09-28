package com.therxmv.otaupdates.domain.utils

/**
 * Semver-ish comparison for GitHub release tags ("v2.0.1") against
 * BuildConfig.VERSION_NAME ("2.0.1"). The parse is anchored at the string
 * start (optional leading v, then dotted digits) so leading junk like
 * "beta2-v1.0" yields no version instead of silently parsing the "2".
 */
object VersionComparator {

    private val VERSION_REGEX = Regex("^v?(\\d+(\\.\\d+)*)", RegexOption.IGNORE_CASE)

    fun isNewer(remoteVersion: String, localVersion: String): Boolean =
        compareVersions(remoteVersion, localVersion) > 0

    private fun compareVersions(a: String, b: String): Int {
        val partsA = a.toVersionParts()
        val partsB = b.toVersionParts()
        val size = maxOf(partsA.size, partsB.size)

        for (i in 0 until size) {
            val partA = partsA.getOrElse(i) { 0 }
            val partB = partsB.getOrElse(i) { 0 }

            if (partA != partB) {
                return partA.compareTo(partB)
            }
        }

        return 0
    }

    private fun String.toVersionParts(): List<Int> =
        VERSION_REGEX.find(this)
            ?.groupValues
            ?.getOrNull(1)
            ?.split(".")
            ?.mapNotNull { it.toIntOrNull() }
            .orEmpty()
}
