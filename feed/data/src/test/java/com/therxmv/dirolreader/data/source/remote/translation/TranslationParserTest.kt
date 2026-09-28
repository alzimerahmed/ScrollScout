package com.therxmv.dirolreader.data.source.remote.translation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TranslationParserTest {

    @Test
    fun `parses single segment response`() {
        val body = """[[["Hola","Hello",null,null,10]],null,"en",null]"""

        assertEquals("Hola", TranslationParser.parse(body))
    }

    @Test
    fun `concatenates multiple segments without separators`() {
        val body = """[[["Hola ","Hello"],["mundo","world"]],null,"en"]"""

        assertEquals("Hola mundo", TranslationParser.parse(body))
    }

    @Test
    fun `throws on empty response`() {
        assertThrows(IllegalArgumentException::class.java) {
            TranslationParser.parse("[]")
        }
    }

    @Test
    fun `throws on malformed json`() {
        assertThrows(Exception::class.java) {
            TranslationParser.parse("not json")
        }
    }
}
