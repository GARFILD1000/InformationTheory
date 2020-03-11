package com.example.enthropy

import com.example.enthropy.encoding.Encoding
import com.example.enthropy.entropy.ShannonEntropy

class Main{
    companion object{
        @JvmStatic
        fun main(args: Array<String>){
//            ShannonEntropy().start()
            Encoding().start()
        }
    }
}