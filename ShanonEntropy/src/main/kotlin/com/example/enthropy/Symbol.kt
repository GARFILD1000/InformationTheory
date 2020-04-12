package com.example.enthropy

import com.example.enthropy.encoding.Code
import com.example.enthropy.encoding.TextCode
import kotlin.math.abs

class Symbol() {
    companion object {
        private var DELTA = Double.MIN_VALUE
        val ZERO = Symbol().apply{
            value = "0"
        }
        val ONE = Symbol().apply{
            value = "1"
        }

        fun getValueComparator(isIncrement: Boolean = true) = Comparator<Symbol> { p0: Symbol?, p1: Symbol? ->
            val sign = if (isIncrement) 1 else -1
            p0 ?: return@Comparator -1 * sign
            p1 ?: return@Comparator 1 * sign
            return@Comparator p0.value.compareTo(p1.value) * sign
        }

        fun getProbabilitiesComparator(isIncrement: Boolean = true) = Comparator<Symbol> { p0: Symbol?, p1: Symbol? ->
            val sign = if (isIncrement) 1 else -1
            p0 ?: return@Comparator -1 * sign
            p1 ?: return@Comparator 1 * sign
            return@Comparator p0.probability.compareTo(p1.probability) * sign
        }
    }

    var value = ""
    var probability = 0.0
    var code = Code()
    var textCode = TextCode()

    fun probabilityEqualsZero(): Boolean {
        return abs(probability) < DELTA
    }

    constructor(symbol: String) : this() {
        value = symbol
    }

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        return other is Symbol && other.value == this.value
    }
}