package com.charf.compillar.utils

import org.apache.commons.text.StringEscapeUtils
import java.math.BigInteger
import java.security.MessageDigest

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

private fun unescapeString(value: String): String {
    return StringEscapeUtils.unescapeJava(value.substring(1, value.length - 1))
}