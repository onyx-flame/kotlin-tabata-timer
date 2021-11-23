package com.onyx.tabatatimer.utils

import android.text.InputFilter
import android.text.Spanned

class DigitsInputFilter(minPositiveInt: Int, maxPositiveInt: Int) : InputFilter {

    private val minValue = minPositiveInt
    private val maxValue = maxPositiveInt

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val input =
            dest?.subSequence(0, dstart).toString() +
            source +
            dest?.subSequence(dend, dest.length).toString()

        if (input.trim('0').isEmpty()) {
            return minValue.toString()
        }

        if (input.trimStart('0') != input) {
            return ""
        }

        if (Integer.parseInt(input) > maxValue  || Integer.parseInt(input) < minValue) {
            return ""
        }
        return null
    }

}