import java.math.BigInteger
import java.security.MessageDigest

class Grammar(val grammarTable : HashMap<String,MutableList<MutableList<String>>>){

    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }



}