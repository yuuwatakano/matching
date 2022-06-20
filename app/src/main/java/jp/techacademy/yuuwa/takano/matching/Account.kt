package jp.techacademy.yuuwa.takano.matching

import java.io.Serializable
import java.util.ArrayList

class Account(var name:String,var profile: String,var address:String, var genre:String, var skill:String,var id:String,val twitterid:String,val instagramid:String,val soundcloudid:String,val bytes: ByteArray): Serializable {
    val imageBytes: ByteArray

    init {
        imageBytes = bytes.clone()
    }
}

