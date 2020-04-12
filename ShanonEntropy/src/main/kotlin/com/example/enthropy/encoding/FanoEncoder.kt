package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import com.example.enthropy.Symbol
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern
import kotlin.Comparator
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.abs

class FanoEncoder(newAlphabet: Alphabet) : StatisticEncoder(newAlphabet) {
    private var decodeTree = TreeMap<Code, Symbol>()

    override fun createCode() {
        alphabet.symbols.forEach { it.code.bits.clear() }
        createCode(alphabet)
    }

    private fun createCode(alphabet: Alphabet) {
        val probabilitiesComparator = Symbol.getProbabilitiesComparator(false)
        var symbolsList = LinkedList<List<Symbol>>()
        var newSymbolsList = LinkedList<List<Symbol>>()
        symbolsList.add(alphabet.symbols.sortedWith(probabilitiesComparator))


        var completed = false

        while (!completed) {
            completed = true
            for (part in symbolsList) {
                if (part.size > 1) {
                    val newParts = splitPart(part)
                    if (newParts.first.isNotEmpty()) {
                        newSymbolsList.addLast(newParts.first)
                    }
                    if (newParts.second.isNotEmpty()) {
                        newSymbolsList.addLast(newParts.second)
                    }
                    //when all parts size is less or equals 1, then we should end algorithm
                    completed = completed && (newParts.first.size <= 1) && (newParts.second.size <= 1)
                } else {
                    newSymbolsList.add(part)
                }
            }
            var sizeSum = 0
            newSymbolsList.forEach { sizeSum += it.size }
            symbolsList = newSymbolsList
            newSymbolsList = LinkedList()
        }
    }

    private fun splitPart(part: List<Symbol>): Pair<List<Symbol>, List<Symbol>> {
        if (part.size <= 1) {
            return Pair(part, emptyList())
        }
        var sumP = 0.0
        var minDelta = 0.0
        var accumulator = 0.0
        var medianIndex = 0
        part.forEach {
            sumP += it.probability
        }
        accumulator = part.first().probability
        minDelta = abs((sumP - accumulator) - accumulator)
        for (i in 1 until part.size) {
            accumulator += part[i].probability
            val delta = abs((sumP - accumulator) - accumulator)
            if (delta <= minDelta && i < part.size-1) {
                minDelta = delta
                medianIndex = i
            } else {
                break
            }
        }

        val firstPart = part.subList(0, medianIndex+1)
        firstPart.forEach { it.code.bits.addLast(0) }

        val secondPart = part.subList(medianIndex+1, part.size)
        secondPart.forEach { it.code.bits.addLast(1) }

        return Pair(firstPart, secondPart)
    }
}