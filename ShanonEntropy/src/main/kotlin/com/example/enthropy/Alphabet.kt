package com.example.enthropy

import com.example.enthropy.entropy.ShannonEntropy
import java.io.FileInputStream
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern
import kotlin.math.log

open class Alphabet {

    var symbols = LinkedList<Symbol>()
    var pattern = Pattern.compile("")

    fun readAlphabet(alphabetFilePath: String) {
        symbols.clear()
        var charsForRegex = LinkedList<String>()
        try {
            val fis = FileInputStream(alphabetFilePath)
            val fileReader = Scanner(fis)
            while (fileReader.hasNextLine()) {
                var line = fileReader.nextLine()
                val char = line.getOrNull(0).toString() ?: ""
                val symbol = Symbol(char)
                val charForRegex = when(char) {
                    "Э" -> "\\" + char
                    "Ь" -> "\\" + char
                    "[", "]" -> "\\" + char
                    else -> char
                }
                charsForRegex.add(charForRegex)
                line = line.drop(2)
                symbol.probability = line.toDoubleOrNull() ?: 0.0
                symbols.add(symbol)
            }
            fileReader.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        pattern = Pattern.compile("[${charsForRegex.joinToString("")}]")
    }

    fun checkSum(): Boolean {
        var sum = 0.0
        for (s in symbols) {
            sum += s.probability
        }
        var valid = sum in 1.0 - ShannonEntropy.DELTA..1.0 + ShannonEntropy.DELTA
        println("Probability sum = ${sum} is valid = ${valid}")
        return valid
    }

    fun calcEntropy(): Double {
        return if (symbols.isNotEmpty()) {
            var entropy = 0.0
            symbols.forEach {
                val log = -it.probability * log(it.probability, 2.0)
                entropy += if (log.isNaN()) 0.0 else log
            }
            entropy
        } else {
            0.0
        }
    }

    fun calcAvgCodeLength(): Double {
        var avgLength = 0.0
        symbols.forEach {
            avgLength += it.probability * it.code.bits.size
        }
        return avgLength
    }

    fun print(comparator: Comparator<Symbol> = Symbol.getValueComparator()) {
        symbols.sortedWith(comparator).forEach {
            println("${it.value} ${it.probability} ${it.code}")
        }
    }

    companion object {
        fun alphabetFileFromText(textPath: String, alphabetPath: String) {
            val symbols = HashMap<String, Int>()
            var symbolsCount = 0
            try {
                val fis = FileInputStream(textPath)
                val fileReader = Scanner(fis)
                fileReader.useDelimiter("")
                var sequence = ""
                while (fileReader.hasNext(allCharPattern)) {
                    val char = fileReader.next(allCharPattern)
                    sequence = char
                    symbols.put(sequence, symbols.getOrDefault(sequence, 0) + 1)
                    symbolsCount++
                }
                fileReader.close()
                fis.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            println("Symbols in text: ${symbolsCount}")
            println("Different symbols: ${symbols.size}")

            try {
                val fileWriter = FileWriter(alphabetPath)

                for (symbol in symbols) {
                    val p = symbol.value.toDouble() / symbolsCount
                    println("${symbol.key} $p")
                    fileWriter.write("${symbol.key} $p\n")
                }

                fileWriter.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }

        val allCharPattern = Pattern.compile(".", Pattern.DOTALL)
    }
}