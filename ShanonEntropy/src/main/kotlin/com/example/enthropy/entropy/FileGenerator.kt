package com.example.enthropy.entropy

import com.example.enthropy.Alphabet
import com.example.enthropy.Symbol
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

class FileGenerator(val alphabet: Alphabet) {
    companion object {
        const val DEFAULT_LENGTH = 20_000L
    }

    //    равномерное распределение
    fun generateWithUniform(path: String, length: Long = DEFAULT_LENGTH): Boolean {
        var generated = false
        try {
            val fileWriter = FileWriter(path)

            for (i in 0 until length) {
                val randSymbol = alphabet.symbols.random()
                fileWriter.write(randSymbol.value)
            }

            fileWriter.close()
            generated = true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return generated
    }

    //   неравномерное распределение (с вероятностями)
    fun generateWithProbabilities(path: String, length: Long = DEFAULT_LENGTH): Boolean {
        var generated = false
        try {
            val fileWriter = FileWriter(path)
            val randGenerator =
                ProbabilitiesIndexGenerator(alphabet.symbols)
            for (i in 0 until length) {
                val randSymbol = alphabet.symbols.get(randGenerator.nextIndex())
                fileWriter.write(randSymbol.value)
            }

            fileWriter.close()
            generated = true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return generated

    }

}

class ProbabilitiesIndexGenerator(val symbols: List<Symbol>) {
    private val intervals = Array<Interval>(symbols.size) {
        Interval(
            0,
            0
        )
    }
    private val rand = Random(System.currentTimeMillis())
    private var probabilitiesSum = 1.0

    init {
        var lastLeft = 0L
        probabilitiesSum = 0.0
        symbols.forEach{ probabilitiesSum += it.probability }

        for (i in symbols.indices) {
            intervals[i].left = lastLeft
            intervals[i].right = lastLeft + (symbols[i].probability / probabilitiesSum * Long.MAX_VALUE).roundToLong()
            lastLeft = intervals[i].right
        }
    }

    fun nextIndex(): Int {
        var index = 0
        val randValue = rand.nextLong().absoluteValue
        for (i in intervals.indices) {
            if (randValue in intervals[i].left .. intervals[i].right) {
                index = i
                break
            }
        }
        return index
    }

    data class Interval(var left: Long, var right: Long)
}