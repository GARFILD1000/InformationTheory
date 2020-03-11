package com.example.enthropy.entropy

import com.example.enthropy.Alphabet

class ShannonEntropy {
    companion object {
        const val DELIM = " "
        const val DELTA = 0.01
    }

    var alphabetFilePath = "text/алфавит.txt"
    var allSymbolsFilePath = "text/всесимволы.txt"

    var alphabet = Alphabet().apply {
        readAlphabet(alphabetFilePath)
    }
    var bigAlphabet = Alphabet().apply{
        readAlphabet(allSymbolsFilePath)
    }

    val uniformFilePath = "text/равномерно.txt"
    val probabilitiesFilePath = "text/вероятностно.txt"
    val realFilePath = "text/реальный.txt"

    fun start() {
//
        val fileGenerator = FileGenerator(alphabet)
        fileGenerator.generateWithUniform(uniformFilePath)
        fileGenerator.generateWithProbabilities(probabilitiesFilePath)
//
        val fileAnalyser = FileAnalyser(alphabet)
//        fileAnalyser.analyse(uniformFilePath, 5)
//        fileAnalyser.analyse(probabilitiesFilePath, 5)
        fileAnalyser.analyse(realFilePath, 15)

//
//        val fileAnalyser2 = FileAnalyser(bigAlphabet)
//        fileAnalyser2.analyse(realFilePath, 5)

    }





}