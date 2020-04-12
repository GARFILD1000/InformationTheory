package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import com.example.enthropy.BinaryAlphabet
import com.example.enthropy.Factorial
import com.example.enthropy.Symbol
import java.io.FileInputStream
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.roundToInt
import com.example.enthropy.Combinatiric
import com.example.enthropy.Symbol.Companion.ONE
import java.lang.Math.pow
import kotlin.collections.ArrayList

class GilbertMoorBlockEncoder(val alphabet: BinaryAlphabet, var blockLength: Int) {
    private var groups = emptyArray<Group>()

    class Group(var blockLength: Int = 0) {
        var onesCount: Int = 0
        var size = 0
        var probabilityPerBlock = 0.0
        var codeLength = 0


        fun getProbability() : Double {
            return probabilityPerBlock * size
        }

        fun findIndex(block: List<Symbol>): Int {
            var n = blockLength
            var k = onesCount
            var index = 0

            for (i in 0 until block.size){
                if (block[i] == Symbol.ONE) {
                    index += Combinatiric.calcC(n-1, k)
                    k--
                }
                n--
                if (k < 0) break
            }
            return index
        }

    }

    init {
        groups = Array<Group>(blockLength+1) { Group(blockLength) }
        groups.forEachIndexed { idx, group ->
            group.onesCount = idx
            group.size = Combinatiric.calcC(blockLength, group.onesCount)
            group.probabilityPerBlock = pow(alphabet.ONE.probability, group.onesCount.toDouble()) *
                    pow(alphabet.ZERO.probability, blockLength-group.onesCount.toDouble())
            group.codeLength = calcCodeLength(group.probabilityPerBlock)
        }
    }

    private fun calcCodeLength(probability: Double) : Int {
        return if (probability > Double.MIN_VALUE) {
            ceil(-log(probability, 2.0) + 1.0).roundToInt()
        } else {
            0
        }
    }

    private fun getFirstBits(number: Double, count: Int): Array<String> {
        val bits = Array<String>(count) { "0" }
        var top = 1.0
        var bottom = 0.0
        var middle = 0.5
        for (i in 0 until count) {
            middle = bottom + ((top - bottom) / 2.0)
            if (number < middle) {
                top = middle
                bits[i] = "0"
            } else {
                bottom = middle
                bits[i] = "1"
            }
        }
        return bits
    }

    private fun getIntervalFromCode(bits: List<Byte>) : Interval<Double>{
        var top = 1.0
        var bottom = 0.0
        var length = 1.0
        for (i in bits.indices) {
            length /= 2.0
            bottom += length * bits[i]
            top = bottom + length
        }
        return Interval(bottom, top)
    }


//    private fun decodeBits(bits: List<Byte>) : Symbol? {
//        val interval = getIntervalFromBits(bits)
//        val indices = getSymbolIdxFromInterval(interval)
//        return if (indices.size == 1) {
//            val symbolIndex = indices.first()
//            if (codeLength[symbolIndex] == bits.size) {
//                symbols[symbolIndex]
//            } else {
//                null
//            }
//        } else {
//            null
//        }
//    }

//    private fun decodeBinarySequence(sequence: LinkedList<Byte>): LinkedList<Symbol> {
//        var usedAllBits = false
//        var counter = 0
//        val decodedSymbols = LinkedList<Symbol>()
//        val encodedBits = LinkedList<Byte>()
//
//        var newLength = 1.0
//        val interval = Interval<Double>(0.0, 1.0)
//
//        while (!usedAllBits) {
//            if (sequence.isEmpty() || counter == sequence.size) {
//                usedAllBits = true
//            } else {
//                encodedBits.addLast(sequence[counter])
//
//                newLength /= 2.0
//                interval.start += newLength * sequence[counter]
//                interval.end = interval.start + newLength
//
//                val indices = getSymbolIdxFromInterval(interval)
//                val decodedSymbol = if (indices.size == 1) {
//                    val symbolIndex = indices.first()
//                    if (codeLength[symbolIndex] <= encodedBits.size) {
//                        symbols[symbolIndex]
//                    } else {
//                        null
//                    }
//                } else {
//                    null
//                }
//
//                counter++
//                if (decodedSymbol != null) {
////                    println("Decoded symbol ${decodedSymbol} from code: ${encodedBits}")
//                    decodedSymbols.addLast(decodedSymbol)
//                    for (i in 0 until counter) {
//                        sequence.removeFirst()
//                    }
//                    encodedBits.clear()
//                    counter = 0
//                    interval.start = 0.0
//                    interval.end = 1.0
//                    newLength = 1.0
//                }
//            }
//        }
//        return decodedSymbols
//    }

//    fun decodeFile(inputFilePath: String, outputFilePath: String) {
//        prepareSymbols()
//        decodeBinary(inputFilePath, outputFilePath)
//    }
//
//    private fun decodeBinary(inputFilePath: String, outputFilePath: String) {
//        try {
//            val fis = FileInputStream(inputFilePath)
//            val fileWriter = FileWriter(outputFilePath)
//            val byteArray = ByteArray(16)
//            val bitsToDecode = LinkedList<Byte>()
//
//            var readedSize = fis.read(byteArray)
//            while (readedSize > 0) {
//                bitsToDecode.addAll(bytesToBits(byteArray, readedSize))
//                val symbols = decodeBinarySequence(bitsToDecode)
//                fileWriter.write(symbols.joinToString(""))
//                readedSize = fis.read(byteArray)
//            }
//
//            fileWriter.close()
//            fis.close()
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }
//
//    private fun byteToBits(byte: Byte): LinkedList<Byte> {
//        val bits = LinkedList<Byte>()
//        for (i in 0 until 8) {
//            val bit = if ((byte.toInt() and (1 shl (7 - i))) != 0) 1 else 0
//            bits.addLast(bit.toByte())
//        }
//        return bits
//    }
//
//    private fun bytesToBits(bytes: ByteArray, bytesCount: Int): LinkedList<Byte> {
//        val bits = LinkedList<Byte>()
//        for (idx in 0 until bytesCount) {
//            for (i in 0 until 8) {
//                val bit = if ((bytes[idx].toInt() and (1 shl (7 - i))) != 0) 1 else 0
//                bits.addLast(bit.toByte())
//            }
//        }
//        return bits
//    }


    fun getCumulative(blockToEncode: List<Symbol>): Double {
        var cumulative = 0.0
        val onesCount = blockToEncode.count { it == ONE }
        for (group in groups) {
            if (group.onesCount < onesCount) {
                cumulative += group.getProbability()
            } else if (group.onesCount == onesCount) {
                val index = group.findIndex(blockToEncode)
                if (index > 0) {
                    cumulative += index * group.probabilityPerBlock
                }
                cumulative += group.probabilityPerBlock / 2
                break
            }
        }
        return cumulative
    }

    fun encodeBlock(blockToEncode: List<Symbol>) : Array<String> {
        var encodedSequence = arrayOf<String>()
        val onesCount = blockToEncode.count { it == ONE }
        groups.find { it.onesCount == onesCount }?.let{ group ->
            val index = group.findIndex(blockToEncode)
            val cumulative = getCumulative(blockToEncode)
            encodedSequence = getFirstBits(cumulative, group.codeLength)
            //println("Block $blockToEncode encoded to ${encodedSequence.joinToString("")}")
        }
        return encodedSequence
    }

    var symbolsCounter = 0
    var codeLength = 0

    fun encodeFile(inputFilePath: String, outputFilePath: String) {
        try {
            val fis = FileInputStream(inputFilePath)
            val fileWriter = FileWriter(outputFilePath)
            val fileReader = Scanner(fis)
            fileReader.useDelimiter("")
            val sequenceToEncode = LinkedList<Symbol>()
            var encodedSequence = emptyArray<String>()

            while (fileReader.hasNext(Alphabet.allCharPattern)) {
                val char = fileReader.next(Alphabet.allCharPattern)
                val symbol = alphabet.symbols.find {
                    it.value == char
                }
                symbol?.let{
                    sequenceToEncode.addLast(it)
                    if (sequenceToEncode.size >= blockLength) {
                        symbolsCounter += blockLength
                        encodedSequence = encodeBlock(sequenceToEncode)
                        codeLength += encodedSequence.size
                        fileWriter.write(encodedSequence.joinToString(""))
                        sequenceToEncode.clear()
                    }
                }
            }
            if (sequenceToEncode.size > 0) {
                for (i in 0 until (blockLength-sequenceToEncode.size)) {
                    sequenceToEncode.addLast(Symbol.ZERO)
                }
                symbolsCounter += blockLength
                encodedSequence = encodeBlock(sequenceToEncode)
                fileWriter.write(encodedSequence.joinToString(""))
                codeLength += encodedSequence.size
                sequenceToEncode.clear()
            }

            println("Avg code length = ${codeLength.toDouble() / symbolsCounter.toDouble()}")
            fileWriter.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

//    fun getBlocksCountFromInterval() : Int {
//
//        for (group in groups) {
//
//        }
//    }
//
//    fun decodeSequence(sequence: LinkedList<String>): List<Symbol>? {
//        var usedAllBits = false
//        var counter = 0
//        val decodedSymbols = LinkedList<Symbol>()
//        val codeSymbols = LinkedList<String>()
//
//        var newLength = 1.0
//        val interval = Interval<Double>(0.0, 1.0)
//
//        while (!usedAllBits) {
//            if (sequence.isEmpty() || counter == sequence.size) {
//                usedAllBits = true
//            } else {
//                codeSymbols.addLast(sequence[counter])
//
//                newLength /= 2.0
//                interval.start += newLength * sequence[counter].toByte()
//                interval.end = interval.start + newLength
//
//                val indices = getSymbolIdxFromInterval(interval)
//                val decodedSymbol = if (indices.size == 1) {
//                    val symbolIndex = indices.first()
//                    if (codeLength[symbolIndex] <= encodedBits.size) {
//                        symbols[symbolIndex]
//                    } else {
//                        null
//                    }
//                } else {
//                    null
//                }
//
//                counter++
//                if (decodedSymbol != null) {
////                    println("Decoded symbol ${decodedSymbol} from code: ${encodedBits}")
//                    decodedSymbols.addLast(decodedSymbol)
//                    for (i in 0 until counter) {
//                        sequence.removeFirst()
//                    }
//                    encodedBits.clear()
//                    counter = 0
//                    interval.start = 0.0
//                    interval.end = 1.0
//                    newLength = 1.0
//                }
//            }
//        }
//        return decodedSymbols
//    }
//
//    fun decodeFile(inputFilePath: String, outputFilePath: String) {
//        try {
//            val fis = FileInputStream(inputFilePath)
//            val fileWriter = FileWriter(outputFilePath)
//            val fileReader = Scanner(fis)
//            fileReader.useDelimiter("")
//            val sequenceToDecode = LinkedList<String>()
//            var decodedSequence: Array<String>? = emptyArray<String>()
//
//            while (fileReader.hasNext(Alphabet.allCharPattern)) {
//                val char = fileReader.next(Alphabet.allCharPattern)
//                    sequenceToDecode.addLast(char)
//                    decodedSequence = decodeSequence(sequenceToDecode)
//                    if (decodedSequence != null) {
//                        fileWriter.write(decodedSequence.joinToString(""))
//                    }
//
//            }
//            fileWriter.close()
//            fis.close()
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }

}

fun main() {
    val alphabet = BinaryAlphabet().apply{
        this.ONE.probability = 0.2
        this.ZERO.probability = 0.8
    }
    val encoder = GilbertMoorBlockEncoder(alphabet, 4)

    val block = listOf(alphabet.ONE, alphabet.ZERO, alphabet.ZERO, alphabet.ONE)
    encoder.encodeBlock(block)

}
