package com.therxmv.otaupdates.domain.utils

/**
 * Semver-ish comparison for GitHub release tags ("v2.0.1") against
 * BuildConfig.VERSION_NAME ("2.0.1"). Non-digit segments are dropped, so
 * suffixes like "-rc1" are ignored by design.
 */
object VersionComparator {

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
        Regex("\\d+(\\.\\d+)*")
            .find(this)
            ?.value
            ?.split(".")
            ?.mapNotNull { it.toIntOrNull() }
            .orEmpty()
}
