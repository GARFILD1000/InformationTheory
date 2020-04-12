package com.example.enthropy.encoding

import java.util.*

class TextCode : Comparable<TextCode> {
    var chars = LinkedList<String>()

    fun hasIn(anotherCode: TextCode): Boolean {
        var result = true
        if (anotherCode.chars.size < chars.size || anotherCode.chars.size == 0 || chars.size == 0) return false

        for (i in chars.indices) {
            if (chars[i] != anotherCode.chars[i]) {
                result = false
                break
            }
        }
        return result
    }

    override fun toString(): String {
        return chars.joinToString("")
    }

    override fun compareTo(other: TextCode): Int {
        var result = 0
        val compareSize = this.chars.size.compareTo(other.chars.size)
        if (compareSize != 0) return compareSize

        for (i in chars.indices) {
            if (chars[i] < other.chars[i]) {
                result = -1
                break
            } else if (chars[i] > other.chars[i]) {
                result = 1
                break
            }
        }
//        println("$this compare to ${other} result = $result")
        return result
    }


}