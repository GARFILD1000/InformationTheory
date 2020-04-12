package com.example.enthropy.encoding

class Interval <T> {
    var start: T
    var end: T

    constructor(newStart: T, newEnd: T) {
        start = newStart
        end = newEnd
    }
}