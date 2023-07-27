package com.dehucka.microservice.text

import java.util.*


/**
 * Created on 27.07.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class CodeGenerator {
    private val base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()
    private val random = Random()

    fun getBase10(length: Int): String {
        val sb = StringBuilder(length)
        for (i in 0 until length) sb.append(base62Chars[random.nextInt(10)])
        return sb.toString()
    }

    fun getBase36(length: Int): String {
        val sb = StringBuilder(length)
        for (i in 0 until length) sb.append(base62Chars[random.nextInt(36)])
        return sb.toString()
    }

    fun getBase62(length: Int): String {
        val sb = StringBuilder(length)
        for (i in 0 until length) sb.append(base62Chars[random.nextInt(62)])
        return sb.toString()
    }
}
