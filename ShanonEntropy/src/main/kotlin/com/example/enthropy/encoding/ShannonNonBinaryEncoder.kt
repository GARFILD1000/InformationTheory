package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import com.example.enthropy.Symbol
import kotlin.math.*

class ShannonNonBinaryEncoder(newAlphabet: Alphabet) : StatisticEncoder(newAlphabet) {
    private var codeAlphabet = Alphabet().apply {
        this.symbols.addLast(Symbol("0"))
        this.symbols.addLast(Symbol("1"))
    }

    fun createCode(newCodeAlphabet: Alphabet) {
        codeAlphabet = newCodeAlphabet
        createCode()
    }

    override fun createCode() {
        val comparator = Symbol.getProbabilitiesComparator(false)
        val symbols = alphabet.symbols.sortedWith(comparator)

        val codeAlphabetPower = codeAlphabet.symbols.size
        val q = Array(symbols.size) { 0.0 }
        var codeLength = 0

        for (i in 1 until symbols.size) {
            q[i] = q[i - 1] + symbols[i - 1].probability
        }

        for (i in symbols.indices) {
            symbols[i].code.bits.clear()
            if (!symbols[i].probabilityEqualsZero()) {
                codeLength = ceil(-log(symbols[i].probability, codeAlphabetPower.toDouble())).roundToInt()
                getFirstBits(q[i], codeLength, codeAlphabetPower).forEach { idx ->
                    codeAlphabet.symbols.getOrNull(idx)?.let {
                        symbols[i].textCode.chars.add(it.value)
                    }
                    Unit
                }

            }
        }
    }

    private fun getFirstBits(number: Double, count: Int, base: Int): Array<Int> {
        val bits = Array<Int>(count) { 0 }
        var top = 1.0
        var bottom = 0.0
        var value = 0.0
        var length = (top + bottom) / base.toDouble()

        for (i in 0 until count) {
            length = abs(top - bottom) / base.toDouble()
            value = floor((number - bottom)/ length)
            bits[i] = value.toInt()
            top = bottom + length * (value + 1)
            bottom = bottom + length * value
        }
        return bits
    }
}