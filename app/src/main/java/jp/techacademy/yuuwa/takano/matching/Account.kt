package jp.techacademy.yuuwa.takano.matching

import java.io.Serializable
import java.util.ArrayList

class Account(var name:String,var address:String, var genre:String, var skill:String,val bytes: ByteArray): Serializable {
    val imageBytes: ByteArray

    init {
        imageBytes = bytes.clone()
    }
}

