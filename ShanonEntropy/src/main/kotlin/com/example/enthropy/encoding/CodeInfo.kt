package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import java.lang.Math.pow

object CodeInfo{
    fun checkCraftTheorem(alphabet: Alphabet) {
        var sum = 0.0
        alphabet.symbols.forEach {
            sum += pow(2.0, -it.code.bits.size.toDouble())
        }
        val result = if (sum <= 1.0) {
            "checked successfully."
        } else {
            "checked without success!"
        }
        println("Craft theorem $result sum = $sum")
    }

    fun checkCodeLosses(alphabet: Alphabet) {
        val averageCodeLength = alphabet.calcAvgCodeLength()
        val entropy = alphabet.calcEntropy()
        val losses = averageCodeLength - entropy
        println("Code losses r = $averageCodeLength - $entropy = $losses")
    }

    fun checkPrefix(alphabet: Alphabet): Boolean {
        var result = true
        val N = alphabet.symbols.size
        for (i in 0 until N) {
            val code = alphabet.symbols[i].code
            for (j in i+1 until N) {
                val toCompare = alphabet.symbols[j].code
                if (code.hasIn(toCompare)) {
                    result = false
                    println("Prefix condition = $result: $code has in $toCompare")
                    return result
                }
            }
        }
        println("Prefix condition = $result")
        return result
    }
}
