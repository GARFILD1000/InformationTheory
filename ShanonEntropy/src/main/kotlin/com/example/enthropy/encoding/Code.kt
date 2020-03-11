package com.example.enthropy.encoding

import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt

class Code : Comparable<Code> {
    var bits = LinkedList<Byte>()

    fun hasIn(anotherCode: Code): Boolean {
        var result = true
        if (anotherCode.bits.size < bits.size) return false

        for (i in bits.indices) {
            if (bits[i] != anotherCode.bits[i]) {
                result = false
                break
            }
        }
        return result
    }

    override fun toString(): String {
        return bits.joinToString("")
    }

    override fun compareTo(other: Code): Int {
        var result = 0
        val compareSize = this.bits.size.compareTo(other.bits.size)
        if (compareSize != 0) return compareSize

        for (i in bits.indices) {
            if (bits[i] < other.bits[i]) {
                result = -1
                break
            } else if (bits[i] > other.bits[i]) {
                result = 1
                break
            }
        }
//        println("$this compare to ${other} result = $result")
        return result
    }


}