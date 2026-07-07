package com.package.aaybari.pdf

import org.junit.Assert.assertTrue
import org.junit.Test

class BengaliNumberToWordsTest {

    @Test
    fun testZero() {
        val words = BengaliNumberToWords.toWords(0.0)
        assertTrue(words.contains("শূন্য") || words.contains("টাকা"))
    }

    @Test
    fun testSimpleAmount() {
        val words = BengaliNumberToWords.toWords(125.5)
        // basic sanity checks
        assertTrue(words.contains("টাকা"))
        assertTrue(words.contains("পয়সা") || words.contains("পয়সা") )
    }

    @Test
    fun testLargeAmount() {
        val words = BengaliNumberToWords.toWords(1234567.89)
        assertTrue(words.contains("কোটি") || words.contains("লাখ") || words.contains("হাজার"))
    }
}
