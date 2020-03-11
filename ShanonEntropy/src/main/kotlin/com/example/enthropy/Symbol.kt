package com.example.enthropy

import com.example.enthropy.encoding.Code

class Symbol() {
    companion object{
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

    constructor(symbol: String): this() {
        value = symbol
    }

    override fun toString(): String {
        return value
    }




}