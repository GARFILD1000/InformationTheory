package com.example.enthropy

object Factorial{
    private fun prodTree(l: Int, r: Int): Int {
        if (l > r)
            return 1
        if (l == r)
            return l
        if (r - l == 1)
            return l * r
        val m = (l + r) / 2
        return prodTree(l, m) * prodTree(m + 1, r)
    }

    fun calc(n: Int): Int {
        if (n < 0)
            return 0
        if (n == 0)
            return 1
        if (n == 1 || n == 2)
            return n
        return prodTree(2, n)
    }
}