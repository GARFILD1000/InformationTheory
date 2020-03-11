package com.example.enthropy.encoding

import java.util.*
import kotlin.Comparator

class TreeVertex<T> {
    var childs = LinkedList<TreeVertex<T>>()
    var parent: TreeVertex<T>? = null
    var data: T

    constructor(data: T) {
        this.data = data
    }

    fun sortChildsWith(comparator: Comparator<T>) {
        childs.sortWith(Comparator<TreeVertex<T>> { p0, p1 ->
            comparator.compare(p0.data, p1.data)
        })
    }
}
