package com.example.enthropy

object Combinatiric{
    fun calcC(n: Int, k: Int) : Int {
        if (n < k) return 0
        return Factorial.calc(n) / (Factorial.calc(n-k) * Factorial.calc(k))
    }
}