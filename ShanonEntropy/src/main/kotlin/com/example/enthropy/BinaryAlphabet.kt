package com.example.enthropy

import java.io.FileInputStream
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern

class BinaryAlphabet(): Alphabet() {
    val ZERO = Symbol().apply {
        this.value = "0"
        this.probability = 0.5
    }

    val ONE = Symbol().apply {
        this.value = "1"
        this.probability = 0.5
    }

    init{
        symbols.add(ZERO)
        symbols.add(ONE)
        updatePattern()
    }
}