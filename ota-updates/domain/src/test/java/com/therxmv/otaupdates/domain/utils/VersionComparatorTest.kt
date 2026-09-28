package com.therxmv.otaupdates.domain.utils

import io.kotest.matchers.shouldBe
import org.junit.Test

class VersionComparatorTest {

    @Test
    fun `returns true when remote tag is newer than local version`() {
        VersionComparator.isNewer("v2.1.0", "2.0.1") shouldBe true
        VersionComparator.isNewer("3.0.0", "2.0.1") shouldBe true
        VersionComparator.isNewer("2.0.2", "2.0.1") shouldBe true
    }

    @Test
    fun `returns false when remote tag equals local version`() {
        VersionComparator.isNewer("v2.0.1", "2.0.1") shouldBe false
    }

    @Test
    fun `returns false when remote tag is older than local version`() {
        VersionComparator.isNewer("v1.9.9", "2.0.1") shouldBe false
        VersionComparator.isNewer("v2.0.0", "2.0.1") shouldBe false
    }

    @Test
    fun `handles missing version segments`() {
        VersionComparator.isNewer("v2.1", "2.0.1") shouldBe true
        VersionComparator.isNewer("v2.0", "2.0.1") shouldBe false
    }

    @Test
    fun `ignores non-digit suffixes`() {
        VersionComparator.isNewer("v2.0.1-rc1", "2.0.1") shouldBe false
        VersionComparator.isNewer("release-2.0.2", "2.0.1") shouldBe false
    }

    @Test
    fun `leading junk does not silently parse as a version`() {
        VersionComparator.isNewer("beta2-v1.0", "0.9.9") shouldBe false
        VersionComparator.isNewer("beta2-v1.0", "2.0.1") shouldBe false
    }
}
