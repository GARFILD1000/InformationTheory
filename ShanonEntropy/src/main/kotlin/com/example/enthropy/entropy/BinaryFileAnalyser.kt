package com.example.enthropy.entropy

import com.example.enthropy.Alphabet
import com.example.enthropy.Symbol
import java.io.FileInputStream
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.math.log

class BinaryFileAnalyser () {
    var sequences = HashMap<String, Long>()
    var frequenciesTable = LinkedList<HashMap<String, Double>>()
    var sequencesCount = 0
    var filePath = ""
    var entropy = 1.0

    private var alphabetPattern = Alphabet.allCharPattern
    var alphabet = Alphabet().apply{
        symbols.add(Symbol("0"))
        symbols.add(Symbol("1"))
    }


    fun analyse(filePath: String, maxSequenceLength: Int = 1) {
        this.filePath = filePath
        this.frequenciesTable.clear()

        println("Analysing file ${filePath}...")
        for (i in 1 .. maxSequenceLength) {
            analyse(i)
        }
        println("Analysing completed")
    }

    private fun analyse(sequenceLength: Int) {
        sequences = HashMap()
        sequencesCount = 0

        try {
            val fis = FileInputStream(filePath)
            val byteArray = ByteArray(4)
            val bitsToAnalyse = LinkedList<Byte>()
            var sequenceToAnalyse = ""
            var readedSize = fis.read(byteArray)

            while (readedSize > 0) {
                for (i in 0 until readedSize) {
                    bitsToAnalyse.addAll(byteToBits(byteArray[i]))
                }
                while (bitsToAnalyse.size >= sequenceLength) {
                    sequenceToAnalyse = ""
                    for (i in 0 until sequenceLength) {
                        sequenceToAnalyse += bitsToAnalyse[i].toString()
                    }
                    sequences.put(sequenceToAnalyse, sequences.getOrDefault(sequenceToAnalyse, 0) + 1)
                    sequencesCount++
                    bitsToAnalyse.removeFirst()
                }
                readedSize = fis.read(byteArray)
            }
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        for (sequence in sequences) {
            println("${sequence.key} ${sequence.value}")
        }

        calcFrequencies()
        calcEntropy(sequenceLength)

        println("Entropy for sequence length $sequenceLength is $entropy")
    }

    private fun byteToBits(byte: Byte): LinkedList<Byte> {
        val bits = LinkedList<Byte>()
        for (i in 0 until 8) {
            val bit = if ((byte.toInt() and (1 shl (7-i))) != 0) 1 else 0
            bits.addLast(bit.toByte())
        }
        return bits
    }

    private fun calcFrequencies() {
        entropy = 0.0
        val frequencies = HashMap<String, Double>()
        for (symbol in sequences) {
            val p = symbol.value.toDouble() / sequencesCount
            frequencies.put(symbol.key, p)
        }
        frequenciesTable.addLast(frequencies)
    }

    private fun calcEntropy(sequenceLength: Int) {
        frequenciesTable.lastOrNull()?.let{
            entropy = 0.0
            for (symbol in it) {
                entropy += -symbol.value * log(symbol.value, 2.0)
            }
            entropy /= sequenceLength
        }
    }

}