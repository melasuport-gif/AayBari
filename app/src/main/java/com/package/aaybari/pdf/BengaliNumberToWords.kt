package com.package.aaybari.pdf

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.floor

/**
 * A lightweight Bengali number-to-words converter for currency amounts (BDT).
 * Handles up to crores for practical purposes. This is a simple implementation
 * and may be extended for more complex grammar rules.
 */
object BengaliNumberToWords {

    private val units = arrayOf("শূন্য","এক","দুই","তিন","চার","পাঁচ","ছয়","সাত","আট","নয়","দশ","এগারো","বারো","তেরো","চৌদ্দ","পনেরো","ষোলো","সতেরো"," আঠারো","উনিশ")
    private val tens = arrayOf("", "", "বিশ", "ত্রিশ", "চল্লিশ", "পঞ্চাশ", "ষাট", "সত্তর", "আশি", "নব্বই")

    private fun twoDigitToWords(n: Int): String {
        if (n < 20) return if (n in 0..19) units[n] else n.toString()
        val t = n / 10
        val u = n % 10
        return if (u == 0) tens[t] else tens[t] + " " + units[u]
    }

    private fun convertIntegerPart(n: Long): String {
        if (n == 0L) return "শূন্য"
        var num = n
        val parts = mutableListOf<String>()

        val crore = num / 10000000
        if (crore > 0) { parts.add("${convertSimple(crore.toInt())} কোটি"); num %= 10000000 }
        val lakh = num / 100000
        if (lakh > 0) { parts.add("${convertSimple(lakh.toInt())} লাখ"); num %= 100000 }
        val thousand = num / 1000
        if (thousand > 0) { parts.add("${convertSimple(thousand.toInt())} হাজার"); num %= 1000 }
        val hundred = num / 100
        if (hundred > 0) { parts.add("${units[hundred.toInt()]} শত"); num %= 100 }
        if (num > 0) parts.add(convertSimple(num.toInt()))

        return parts.joinToString(" ")
    }

    private fun convertSimple(n: Int): String {
        return if (n < 100) twoDigitToWords(n) else {
            val h = n / 100
            val rem = n % 100
            if (rem == 0) "${units[h]} শত" else "${units[h]} শত ${twoDigitToWords(rem)}"
        }
    }

    fun toWords(amount: Double): String {
        val bd = BigDecimal.valueOf(amount)
        val intPart = bd.toBigInteger()
        val fraction = bd.remainder(BigDecimal.ONE).movePointRight(2).toBigInteger().toInt()

        val intWords = convertIntegerPart(intPart.toLong())
        val paiseWords = if (fraction > 0) " এবং ${convertIntegerPart(fraction.toLong())} পয়সা" else ""
        return "$intWords টাকা$paiseWords মাত্র"
    }
}
