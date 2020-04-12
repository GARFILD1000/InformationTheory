package com.example.enthropy.encoding

import com.example.enthropy.Alphabet
import com.example.enthropy.Symbol
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.lang.Exception
import java.util.*

abstract class StatisticEncoder(val alphabet: Alphabet) {
    private var encodeTree = TreeMap<String, Code>()
    private var decodeTree = TreeMap<Code, Symbol>()

    private var encodeTextTree = TreeMap<String, TextCode>()
    private var decodeTextTree = TreeMap<TextCode, Symbol>()

    public abstract fun createCode()

    open fun encodeFile(inputFilePath: String, outputFilePath: String) {
        val saveAsBinary = isBinaryCode()
        if (saveAsBinary) {
            encodeBinary(inputFilePath, outputFilePath)
        } else {
            encodeText(inputFilePath, outputFilePath)
        }

    }

    private fun encodeBinary(inputFilePath: String, outputFilePath: String) {
        encodeTree.clear()
        alphabet.symbols.forEach {
            encodeTree.put(it.value, it.code)
        }
        try {
            val fis = FileInputStream(inputFilePath)
            val fos = BufferedOutputStream(FileOutputStream(outputFilePath))
            val fileReader = Scanner(fis)
            fileReader.useDelimiter("")
            val sequenceToWrite = LinkedList<Byte>()
            while (fileReader.hasNext(Alphabet.allCharPattern)) {
                val char = fileReader.next(Alphabet.allCharPattern)
                if (alphabet.pattern.matcher(char).matches()) {
                    val encoded = encodeTree.get(char)
                    if (encoded != null) {
                        sequenceToWrite.addAll(encoded.bits)
                        writeBits(fos, sequenceToWrite)
                    }
                }
            }
            if (sequenceToWrite.isNotEmpty()) {
                while (sequenceToWrite.size < 8) {
                    sequenceToWrite.addLast(0)
                }
                writeBits(fos, sequenceToWrite)
            }

            fos.close()
            fileReader.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun encodeText(inputFilePath: String, outputFilePath: String) {
        encodeTextTree.clear()
        alphabet.symbols.forEach {
            encodeTextTree.put(it.value, it.textCode)
        }
        try {
            val fis = FileInputStream(inputFilePath)
            val fileWriter = FileWriter(outputFilePath)
            val fileReader = Scanner(fis)
            fileReader.useDelimiter("")
            while (fileReader.hasNext(Alphabet.allCharPattern)) {
                val char = fileReader.next(Alphabet.allCharPattern)
                if (alphabet.pattern.matcher(char).matches()) {
                    val encoded = encodeTextTree.get(char)
                    if (encoded != null) {
                        fileWriter.write(encoded.toString())
                    }
                }
            }
            fileWriter.close()
            fileReader.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun writeBits(fos: BufferedOutputStream, bitsToWrite: LinkedList<Byte>) {
        while (bitsToWrite.size >= 8) {
            var byte: Byte = 0
            for (i in 0 until 8) {
                val bit = bitsToWrite.pollFirst().toInt() shl (7 - i)
                byte = (byte.toInt() or bit).toByte()
            }
            fos.write(ByteArray(1).apply { set(0, byte) })
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

    private fun decodeBinarySequence(sequence: LinkedList<Byte>): LinkedList<Symbol> {
        var usedAllBits = false
        var counter = 0
        val decodedSymbols = LinkedList<Symbol>()
        val code = Code()
//        println("Decode bits: ${bitsToDecode.joinToString("")}")
        while (!usedAllBits) {
            if (sequence.isEmpty() || counter == sequence.size) {
                usedAllBits = true
            } else {
                code.bits.addLast(sequence[counter])
                counter++
                val decodedSymbol = decodeTree.get(code)
                if (decodedSymbol != null) {
                    decodedSymbols.addLast(decodedSymbol)
                    for (i in 0 until counter) {
                        sequence.removeFirst()
                    }
                    code.bits.clear()
                    counter = 0
                }
            }
        }
        return decodedSymbols
    }

    private fun decodeTextSequence(sequence: LinkedList<String>): LinkedList<Symbol> {
        var usedAllBits = false
        var counter = 0
        val decodedSymbols = LinkedList<Symbol>()
        val code = TextCode()
//        println("Decode bits: ${bitsToDecode.joinToString("")}")
        while (!usedAllBits) {
            if (sequence.isEmpty() || counter == sequence.size) {
                usedAllBits = true
            } else {
                code.chars.addLast(sequence[counter])
                counter++
                val decodedSymbol = decodeTextTree.get(code)
                if (decodedSymbol != null) {
                    decodedSymbols.addLast(decodedSymbol)
                    for (i in 0 until counter) {
                        sequence.removeFirst()
                    }
                    code.chars.clear()
                    counter = 0
                }
            }
        }
        return decodedSymbols
    }

    open fun decodeFile(inputFilePath: String, outputFilePath: String) {
        if (isBinaryCode()) {
            //binary code (0, 1 bits)
            decodeBinary(inputFilePath, outputFilePath)
        } else {
            //text code (symbols)
            decodeText(inputFilePath, outputFilePath)
        }
    }

    private fun decodeText(inputFilePath: String, outputFilePath: String) {
        decodeTextTree.clear()
        alphabet.symbols.forEach {
            decodeTextTree.put(it.textCode, it)
        }
        try {
            val fis = FileInputStream(inputFilePath)
            val fileReader = Scanner(fis)
            val fileWriter = FileWriter(outputFilePath)

            val sequenceToDecode = LinkedList<String>()
            fileReader.useDelimiter("")
            while (fileReader.hasNext(Alphabet.allCharPattern)) {
                val char = fileReader.next(Alphabet.allCharPattern)
                sequenceToDecode.addLast(char)
                val symbols = decodeTextSequence(sequenceToDecode)
                symbols.forEach {
                    fileWriter.write(it.value)
                }
            }

            fileReader.close()
            fileWriter.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun decodeBinary(inputFilePath: String, outputFilePath: String) {
        decodeTree.clear()
        alphabet.symbols.forEach {
            decodeTree.put(it.code, it)
        }
        try {
            val fis = FileInputStream(inputFilePath)
            val fileWriter = FileWriter(outputFilePath)
            val byteArray = ByteArray(4)
            val bitsToDecode = LinkedList<Byte>()
            var readedSize = fis.read(byteArray)
            while (readedSize > 0) {
                for (i in 0 until readedSize) {
                    bitsToDecode.addAll(byteToBits(byteArray[i]))
                }
                val symbols = decodeBinarySequence(bitsToDecode)
                symbols.forEach {
                    fileWriter.write(it.value)
                }
                readedSize = fis.read(byteArray)
            }

            fileWriter.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun isBinaryCode(): Boolean {
        val codeSymbols = alphabet.getCodeAlphabetSymbols()
        return (codeSymbols.size == 1 || codeSymbols.size == 2)
                && (codeSymbols.contains(0) || codeSymbols.contains(1))
    }

}