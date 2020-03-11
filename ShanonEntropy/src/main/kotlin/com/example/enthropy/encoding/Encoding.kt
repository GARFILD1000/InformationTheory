package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import com.example.enthropy.Symbol
import com.example.enthropy.entropy.BinaryFileAnalyser

class Encoding {
    var alphabetFilePath = "text/алфавит.txt"
    var allSymbolsFilePath = "text/всесимволы.txt"

    val realFilePath = "text/реальный.txt"
    val encodedHuffmanFilePath = "text/закодированныйХаффман.txt"
    val decodedHuffmanFilePath = "text/декодированныйХаффман.txt"


    val alphabet = Alphabet().apply{
        readAlphabet(allSymbolsFilePath)
    }


    fun start() {
//        Alphabet.alphabetFileFromText(realFilePath, allSymbolsFilePath)

        var encoder = HuffmanEncoder(alphabet)
        encoder.createCode()
        alphabet.print(Symbol.getProbabilitiesComparator(false))
        println("Text entropy: ${alphabet.calcEntropy()}")
        CodeInfo.checkCraftTheorem(alphabet)
        CodeInfo.checkCodeLosses(alphabet)

//        alphabet.symbols.find {it.value == "о" }?.code?.bits?.apply {
//            clear()
//            addAll(listOf<Byte>(0, 0, 1))
//        }

        CodeInfo.checkPrefix(alphabet)

        encoder.encodeFile(realFilePath, encodedHuffmanFilePath)
        encoder.decodeFile(encodedHuffmanFilePath, decodedHuffmanFilePath)

        val binaryAnalyser = BinaryFileAnalyser()
        binaryAnalyser.analyse(encodedHuffmanFilePath, 5)
    }
}