import org.apache.commons.text.StringEscapeUtils
import java.math.BigInteger
import java.security.MessageDigest

class Grammar(val grammarEntryTable: HashMap<String, MutableList<MutableList<Term>>>, val debug: Boolean = false){

    var tokens : MutableSet<String> = mutableSetOf()

    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    init {
        FindTokens()
    }

    private fun FindTokens() {
        for (rule in grammarEntryTable) {
            for (desc in rule.value) {
                for(term in desc){
                    if(term is TerminalTerm){
                        if(!tokens.contains(term.value)){
                            val unescapedValue : String = unescapeString(term.value)
                            tokens.add(unescapedValue)
                            dLog("Token found: $unescapedValue")
                        }
                    }
                }
            }
        }
    }

    private fun unescapeString(value: String): String {
        return StringEscapeUtils.unescapeJava(value.substring(1, value.length - 1))
    }

    fun dLog(s : Any){
        if(debug){
            println(s)
        }
    }

    abstract class Term(val value : String)
    class NonTerminalTerm(value : String) : Term(value)
    class TerminalTerm(value : String) : Term(value)
    class EmptyTerm : Term("")
}