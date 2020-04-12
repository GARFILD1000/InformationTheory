package com.example.enthropy.entropy

import com.example.enthropy.Alphabet
import java.io.FileInputStream
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.math.log

class FileAnalyser (val alphabet: Alphabet) {
    var sequences = HashMap<String, Long>()
    var frequenciesTable = LinkedList<HashMap<String, Double>>()
    var sequencesCount = 0
    var filePath = ""
    var entropy = 1.0

    private var alphabetPattern = Pattern.compile(".")


    fun analyse(filePath: String, maxSequenceLength: Int = 1) {
        this.filePath = filePath
        this.frequenciesTable.clear()
        this.alphabetPattern = alphabet.pattern

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
            val fileReader = Scanner(fis)
            fileReader.useDelimiter("")
            var sequence = ""
            while (fileReader.hasNext(Alphabet.allCharPattern)) {
                val char = fileReader.next(Alphabet.allCharPattern)
                if (alphabetPattern.matcher(char).matches()) {
                    sequence += char
                    if (sequence.length >= sequenceLength) {
                        sequences.put(sequence, sequences.getOrDefault(sequence, 0) + 1)
                        sequencesCount++
                        sequence = sequence.drop(1)
                    }
                } else {
                    sequence = ""
                }
            }
            fileReader.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

//        for (sequence in sequences) {
//            println("${sequence.key} ${sequence.value}")
//        }

        calcFrequencies()
        calcEntropy(sequenceLength)

        println("Entropy for sequence length $sequenceLength is $entropy")
        val theoreticalEntropy = sequenceLength * log(alphabet.symbols.size.toDouble(), 2.0)
        println("Theoretical entropy for sequence length $sequenceLength is ${theoreticalEntropy}")
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