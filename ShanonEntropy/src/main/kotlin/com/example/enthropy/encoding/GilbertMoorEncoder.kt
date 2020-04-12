package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import com.example.enthropy.Symbol
import java.io.FileInputStream
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import kotlin.math.ceil
import kotlin.math.log
import kotlin.math.roundToInt

class GilbertMoorEncoder(newAlphabet: Alphabet) : StatisticEncoder(newAlphabet) {
    private var symbols = emptyList<Symbol>()
    private var cumulative = emptyArray<Double>()
    private var codeLength = emptyArray<Int>()

    private fun prepareSymbols() {
        symbols = alphabet.symbols.sortedWith(Symbol.getValueComparator(true))
        cumulative = Array(symbols.size) { 0.0 }
        codeLength = Array(symbols.size) { 0 }

        for (i in 1 until symbols.size) {
            cumulative[i] += cumulative[i-1] + symbols[i-1].probability
        }

        for (i in symbols.indices) {
            cumulative[i] += symbols[i].probability / 2.0
            if (!symbols[i].probabilityEqualsZero()) {
                codeLength[i] = ceil(-log(symbols[i].probability, 2.0) + 1.0).roundToInt()
            }
        }
    }

    override fun createCode() {
        prepareSymbols()
        for (i in symbols.indices) {
            symbols[i].code.bits.clear()
            if (!symbols[i].probabilityEqualsZero()) {
                symbols[i].code.bits.addAll(getFirstBits(cumulative[i], codeLength[i]))
            }
        }
    }

    private fun getFirstBits(number: Double, count: Int): Array<Byte> {
        val bits = Array<Byte>(count) { 0 }
        var top = 1.0
        var bottom = 0.0
        var middle = 0.5

        for (i in 0 until count) {
            middle = bottom + ((top - bottom) / 2.0)
            if (number < middle) {
                top = middle
                bits[i] = 0
            } else {
                bottom = middle
                bits[i] = 1
            }
        }
        return bits
    }

    private fun getIntervalFromBits(bits: List<Byte>) : Interval<Double>{
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

    private fun getSymbolIdxFromInterval(interval : Interval<Double>): List<Int> {
        val symbolsInInterval = LinkedList<Int>()
        for (i in symbols.indices) {
            if (cumulative[i] > interval.end) {
                break
            }
            if (cumulative[i] >= interval.start) {
                symbolsInInterval.add(i)
            }
        }
        return symbolsInInterval
    }

    private fun decodeBits(bits: List<Byte>) : Symbol? {
        val interval = getIntervalFromBits(bits)
        val indices = getSymbolIdxFromInterval(interval)
        return if (indices.size == 1) {
            val symbolIndex = indices.first()
            if (codeLength[symbolIndex] == bits.size) {
                symbols[symbolIndex]
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun decodeBinarySequence(sequence: LinkedList<Byte>): LinkedList<Symbol> {
        var usedAllBits = false
        var counter = 0
        val decodedSymbols = LinkedList<Symbol>()
        val encodedBits = LinkedList<Byte>()

        var newLength = 1.0
        val interval = Interval<Double>(0.0, 1.0)

        while (!usedAllBits) {
            if (sequence.isEmpty() || counter == sequence.size) {
                usedAllBits = true
            } else {
                encodedBits.addLast(sequence[counter])

                newLength /= 2.0
                interval.start += newLength * sequence[counter]
                interval.end = interval.start + newLength

                val indices = getSymbolIdxFromInterval(interval)
                val decodedSymbol = if (indices.size == 1) {
                    val symbolIndex = indices.first()
                    if (codeLength[symbolIndex] <= encodedBits.size) {
                        symbols[symbolIndex]
                    } else {
                        null
                    }
                } else {
                    null
                }

                counter++
                if (decodedSymbol != null) {
//                    println("Decoded symbol ${decodedSymbol} from code: ${encodedBits}")
                    decodedSymbols.addLast(decodedSymbol)
                    for (i in 0 until counter) {
                        sequence.removeFirst()
                    }
                    encodedBits.clear()
                    counter = 0
                    interval.start = 0.0
                    interval.end = 1.0
                    newLength = 1.0
                }
            }
        }
        return decodedSymbols
    }

    override fun decodeFile(inputFilePath: String, outputFilePath: String) {
        prepareSymbols()
        decodeBinary(inputFilePath, outputFilePath)
    }


    private fun decodeBinary(inputFilePath: String, outputFilePath: String) {
        try {
            val fis = FileInputStream(inputFilePath)
            val fileWriter = FileWriter(outputFilePath)
            val byteArray = ByteArray(16)
            val bitsToDecode = LinkedList<Byte>()

            var readedSize = fis.read(byteArray)
            while (readedSize > 0) {
                bitsToDecode.addAll(bytesToBits(byteArray, readedSize))
                val symbols = decodeBinarySequence(bitsToDecode)
                fileWriter.write(symbols.joinToString(""))
                readedSize = fis.read(byteArray)
            }

            fileWriter.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun byteToBits(byte: Byte): LinkedList<Byte> {
        val bits = LinkedList<Byte>()
        for (i in 0 until 8) {
            val bit = if ((byte.toInt() and (1 shl (7 - i))) != 0) 1 else 0
            bits.addLast(bit.toByte())
        }
        return bits
    }

    private fun bytesToBits(bytes: ByteArray, bytesCount: Int): LinkedList<Byte> {
        val bits = LinkedList<Byte>()
        for (idx in 0 until bytesCount) {
            for (i in 0 until 8) {
                val bit = if ((bytes[idx].toInt() and (1 shl (7 - i))) != 0) 1 else 0
                bits.addLast(bit.toByte())
            }
        }
        return bits
    }

    private fun isBinaryCode(): Boolean {
        val codeSymbols = alphabet.getCodeAlphabetSymbols()
        return (codeSymbols.size == 1 || codeSymbols.size == 2)
                && (codeSymbols.contains(0) || codeSymbols.contains(1))
    }

}