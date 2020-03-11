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

class HuffmanEncoder(val alphabet: Alphabet) {
    private var decodeTree = TreeMap<Code, Symbol>()

    fun createCode() {
        val symbolsTree = prepareTree(alphabet)
        completeCode(symbolsTree)
    }

    private fun prepareTree(alphabet: Alphabet): TreeVertex<Symbol>? {
        var symbolsTree: TreeVertex<Symbol>? = null
        val probabilitiesComparator = Symbol.getProbabilitiesComparator(true)
        val treeComparator = Comparator<TreeVertex<Symbol>>{ p0, p1 ->
            probabilitiesComparator.compare(p0.data, p1.data)
        }
        var topVertexes = alphabet.symbols.map {
            TreeVertex(it)
        }
        topVertexes = topVertexes.sortedWith(treeComparator)
        var completed = false
        while (!completed) {
//            topVertexes.forEach {
//                print("${it.data.value} ${it.data.probability} | ")
//            }
//            println()

            val newVertexes = LinkedList<TreeVertex<Symbol>>()
            if (topVertexes.size == 1) {
                completed = true
                symbolsTree = topVertexes.first()
            } else {
                val vertexOne = topVertexes[0]
                val vertexTwo = topVertexes[1]
                val newSymbol = Symbol()
                newSymbol.probability = vertexOne.data.probability + vertexTwo.data.probability
                newSymbol.value = vertexOne.data.value + vertexTwo.data.value
                val newVertex = TreeVertex(newSymbol)
                newVertex.childs.add(vertexOne)
                newVertex.childs.add(vertexTwo)
                vertexOne.parent = newVertex
                vertexTwo.parent = newVertex
                newVertexes.add(newVertex)

                topVertexes.forEach {
                    if (it.parent == null) {
                        newVertexes.add(it)
                    }
                }
                topVertexes = newVertexes.sortedWith(treeComparator)
            }
        }
        return symbolsTree
    }

    private fun completeCode(parent: TreeVertex<Symbol>?) {
        parent?.childs?.getOrNull(0)?.let{
            it.data.code.bits.addAll(parent.data.code.bits)
            it.data.code.bits.add(0)
            completeCode(it)
        }
        parent?.childs?.getOrNull(1)?.let{
            it.data.code.bits.addAll(parent.data.code.bits)
            it.data.code.bits.add(1)
            completeCode(it)
        }
    }

    fun encodeFile(inputFilePath: String, outputFilePath: String) {
        try {
            val fis = FileInputStream(inputFilePath)
            val fos = FileOutputStream(outputFilePath)
            val fileReader = Scanner(fis)
            val bitsToWrite = LinkedList<Byte>()
            fileReader.useDelimiter("")

            while (fileReader.hasNext(Alphabet.allCharPattern)) {
                val char = fileReader.next(Alphabet.allCharPattern)
                if (alphabet.pattern.matcher(char).matches()) {
                    val idx = alphabet.symbols.indexOfFirst { it.value == char }
                    alphabet.symbols.getOrNull(idx)?.let {
                        bitsToWrite.addAll(it.code.bits)
                    }
                    writeBits(fos, bitsToWrite)
                }
            }

            if (bitsToWrite.isNotEmpty()) {
                while (bitsToWrite.size < 8) {
                    bitsToWrite.addLast(0)
                }
                writeBits(fos, bitsToWrite)
            }

            fos.close()
            fileReader.close()
            fis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun writeBits(fos: FileOutputStream, bitsToWrite: LinkedList<Byte>) {
        while (bitsToWrite.size >= 8) {
            var byte: Byte = 0
            for (i in 0 until 8) {
                val bit = bitsToWrite.pollFirst().toInt() shl (7-i)
                byte = (byte.toInt() or bit).toByte()
            }
            fos.write(ByteArray(1).apply{set(0, byte)})
        }
    }

    private fun byteToBits(byte: Byte): LinkedList<Byte> {
        val bits = LinkedList<Byte>()
        for (i in 0 until 8) {
            val bit = if ((byte.toInt() and (1 shl (7-i))) != 0) 1 else 0
            bits.addLast(bit.toByte())
        }
        return bits
    }

    private fun decodeBits(bitsToDecode: LinkedList<Byte>): LinkedList<Symbol> {
        var usedAllBits = false
        var counter = 0
        val decodedSymbols = LinkedList<Symbol>()
        val code = Code()
//        println("Decode bits: ${bitsToDecode.joinToString("")}")
        while(!usedAllBits) {
            if (bitsToDecode.isEmpty() || counter == bitsToDecode.size) {
                usedAllBits = true
            } else {
                code.bits.addLast(bitsToDecode[counter])
                counter++
                val decodedSymbol = decodeTree.get(code)
//                println("Try to find ${code}")
                if (decodedSymbol != null) {
//                    println("Decoded: ${decodedSymbol.code} to ${decodedSymbol.value}")
                    decodedSymbols.addLast(decodedSymbol)
                    for (i in 0 until counter) {
                        bitsToDecode.removeFirst()
                    }
                    code.bits.clear()
                    counter = 0
                }
            }
        }
        return decodedSymbols
    }

    fun decodeFile(inputFilePath: String, outputFilePath: String) {
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
                val symbols = decodeBits(bitsToDecode)
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
}


//                for (i in topVertexes.indices) {
//                    if ((i % 2 == 0) && (i + 1 < topVertexes.size)) {
//                        val newSymbol = Symbol()
//                        newSymbol.probability = topVertexes[i].data.probability + topVertexes[i+1].data.probability
//                        newSymbol.value = topVertexes[i].data.value + topVertexes[i+1].data.value
//                        val newVertex = TreeVertex(newSymbol)
//                        newVertex.childs.add(topVertexes[i])
//                        newVertex.childs.add(topVertexes[i+1])
//                        topVertexes[i].parent = newVertex
//                        topVertexes[i+1].parent = newVertex
//                        newVertexes.add(newVertex)
//                    }
//                }