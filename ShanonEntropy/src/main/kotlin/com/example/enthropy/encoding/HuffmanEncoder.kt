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

class HuffmanEncoder(newAlphabet: Alphabet) : StatisticEncoder(newAlphabet){

    override fun createCode() {
        alphabet.symbols.forEach { it.code.bits.clear() }
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
}