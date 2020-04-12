package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import com.example.enthropy.BinaryAlphabet
import com.example.enthropy.Symbol
import com.example.enthropy.entropy.BinaryFileAnalyser
import com.example.enthropy.entropy.FileAnalyser

class Encoding {
    var alphabetFilePath = "text/алфавит.txt"
    var allSymbolsFilePath = "text/всесимволы.txt"
    var binaryAlphabetFilePath = "text/двоичныйАлфавит.txt"

    val realFilePath = "text/реальный.txt"
    val uniformFilePath = "text/равномерно.txt"
    val probabilitiesFilePath = "text/вероятностно.txt"

    val encodedHuffmanFilePath = "text/закодированныйХаффман.txt"
    val decodedHuffmanFilePath = "text/декодированныйХаффман.txt"
    val encodedFanoFilePath = "text/закодированныйФано.txt"
    val decodedFanoFilePath = "text/декодированныйФано.txt"
    val encodedShannonFilePath = "text/закодированныйШеннон.txt"
    val decodedShannonFilePath = "text/декодированныйШеннон.txt"
    val encodedShannonNBFilePath = "text/закодированныйНедвоичныйШеннон.txt"
    val decodedShannonNBFilePath = "text/декодированныйНедвоичныйШеннон.txt"
    val encodedGilbertMoorFilePath = "text/закодированныйГилбертМур.txt"
    val decodedGilbertMoorFilePath = "text/декодированныйГилбертМур.txt"
    val encodedBlockFilePath = "text/закодированныйБлочный.txt"
    val decodedBlockFilePath = "text/декодированныйБлочный.txt"


    val alphabet = Alphabet().apply{
        readAlphabet(allSymbolsFilePath)
    }

    fun updateAlphabet() {
        alphabet.readAlphabet(allSymbolsFilePath)
    }

    fun start() {

//        encodeWithHuffman(realFilePath)
//        encodeWithHuffman(uniformFilePath)
//        encodeWithHuffman(probabilitiesFilePath)

//        encodeWithFano(realFilePath)
//        encodeWithFano(uniformFilePath)
//        encodeWithFano(probabilitiesFilePath)

//        encodeWithShannon(realFilePath)
//        encodeWithShannon(uniformFilePath)
//        encodeWithShannon(probabilitiesFilePath)

//        encodeWithShannonNonBinary(realFilePath)
//        encodeWithShannonNonBinary(uniformFilePath)
//        encodeWithShannonNonBinary(probabilitiesFilePath)

//        encodeWithGilbertMoor(realFilePath)
//        encodeWithGilbertMoor(uniformFilePath)
//        encodeWithGilbertMoor(probabilitiesFilePath)

        encodeWithShannonBlock()

    }

    private fun encodeWithShannonNonBinary(filePath: String) {
        Alphabet.alphabetFileFromText(filePath, allSymbolsFilePath)
        updateAlphabet()
        val encoder = ShannonNonBinaryEncoder(alphabet)
        val encodeAlphabet = Alphabet().apply {
            symbols.add(Symbol("0"))
            symbols.add(Symbol("1"))
            symbols.add(Symbol("2"))
            updatePattern()
        }

        encoder.createCode(encodeAlphabet)
        alphabet.print(Symbol.getProbabilitiesComparator(false))
        println("Text entropy: ${alphabet.calcEntropy()}")
//        CodeInfo.checkCraftTheorem(alphabet)
//        CodeInfo.checkCodeLosses(alphabet)

        CodeInfo.checkPrefix(alphabet)
        encoder.encodeFile(filePath, encodedShannonNBFilePath)
        encoder.decodeFile(encodedShannonNBFilePath, decodedShannonNBFilePath)

        val analyser = FileAnalyser(encodeAlphabet)
        analyser.analyse(encodedShannonNBFilePath, 3)

//        val binaryAnalyser = BinaryFileAnalyser()
//        binaryAnalyser.analyse(encodedShannonFilePath, 1)
    }

    private fun encodeWithGilbertMoor(filePath: String) {
        Alphabet.alphabetFileFromText(filePath, allSymbolsFilePath)
        updateAlphabet()
        val encoder = GilbertMoorEncoder(alphabet)
        encoder.createCode()
        alphabet.print(Symbol.getProbabilitiesComparator(false))
        println("Text entropy: ${alphabet.calcEntropy()}")
        CodeInfo.checkCraftTheorem(alphabet)
        CodeInfo.checkCodeLosses(alphabet)

        CodeInfo.checkPrefix(alphabet)
        encoder.encodeFile(filePath, encodedGilbertMoorFilePath)
        encoder.decodeFile(encodedGilbertMoorFilePath, decodedGilbertMoorFilePath)

        val binaryAnalyser = BinaryFileAnalyser()
        binaryAnalyser.analyse(encodedGilbertMoorFilePath, 3)
    }

    private fun encodeWithShannon(filePath: String) {
        Alphabet.alphabetFileFromText(filePath, allSymbolsFilePath)
        updateAlphabet()
        val encoder = ShannonEncoder(alphabet)
        encoder.createCode()
        alphabet.print(Symbol.getProbabilitiesComparator(false))
        println("Text entropy: ${alphabet.calcEntropy()}")
        CodeInfo.checkCraftTheorem(alphabet)
        CodeInfo.checkCodeLosses(alphabet)

        CodeInfo.checkPrefix(alphabet)
        encoder.encodeFile(filePath, encodedShannonFilePath)
        encoder.decodeFile(encodedShannonFilePath, decodedShannonFilePath)

        val binaryAnalyser = BinaryFileAnalyser()
        binaryAnalyser.analyse(encodedShannonFilePath, 3)
    }

    private fun encodeWithFano(filePath: String) {
        Alphabet.alphabetFileFromText(filePath, allSymbolsFilePath)
        updateAlphabet()
        val encoder = FanoEncoder(alphabet)
        encoder.createCode()
        alphabet.print(Symbol.getProbabilitiesComparator(false))
        println("Text entropy: ${alphabet.calcEntropy()}")
        CodeInfo.checkCraftTheorem(alphabet)
        CodeInfo.checkCodeLosses(alphabet)

        CodeInfo.checkPrefix(alphabet)
        encoder.encodeFile(filePath, encodedFanoFilePath)
        encoder.decodeFile(encodedFanoFilePath, decodedFanoFilePath)

        val binaryAnalyser = BinaryFileAnalyser()
        binaryAnalyser.analyse(encodedFanoFilePath, 3)
    }

    private fun encodeWithHuffman(filePath: String) {
        Alphabet.alphabetFileFromText(filePath, allSymbolsFilePath)
        updateAlphabet()
        val encoder = HuffmanEncoder(alphabet)
        encoder.createCode()
        alphabet.print(Symbol.getProbabilitiesComparator(false))
        println("Text entropy: ${alphabet.calcEntropy()}")
        CodeInfo.checkCraftTheorem(alphabet)
        CodeInfo.checkCodeLosses(alphabet)

        CodeInfo.checkPrefix(alphabet)
        encoder.encodeFile(filePath, encodedHuffmanFilePath)
        encoder.decodeFile(encodedHuffmanFilePath, decodedHuffmanFilePath)

        val binaryAnalyser = BinaryFileAnalyser()
        binaryAnalyser.analyse(encodedHuffmanFilePath, 3)
    }

    private fun encodeWithShannonBlock() {
        val filePath = probabilitiesFilePath
        Alphabet.alphabetFileFromText(filePath, binaryAlphabetFilePath)
        val tempAlphabet = Alphabet()
        tempAlphabet.readAlphabet(binaryAlphabetFilePath)
        val alphabet = BinaryAlphabet().apply{
            tempAlphabet.symbols.find { it == Symbol.ONE }?.let{
                this.ONE.probability = it.probability
            }
            tempAlphabet.symbols.find { it == Symbol.ZERO }?.let{
                this.ZERO.probability = it.probability
            }
        }

        alphabet.print(Symbol.getProbabilitiesComparator(false))
        println("Text entropy: ${alphabet.calcEntropy()}")

        var fileAnalyser = FileAnalyser(tempAlphabet)
        fileAnalyser.analyse(filePath)


        val encoder = GilbertMoorBlockEncoder(alphabet, 20)
        encoder.encodeFile(filePath, encodedBlockFilePath)
//
//        for (i in 1 .. 10) {
//            val encoder = GilbertMoorBlockEncoder(alphabet, i)
//            encoder.encodeFile(filePath, encodedBlockFilePath)
//        }


//        val binaryAnalyser = BinaryFileAnalyser()
//        binaryAnalyser.analyse(encodedBlockFilePath, 3)
    }
}