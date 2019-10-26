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
        FindAnonymousTokens()
        var firstAndFollowTableGenerators = FirstAndFollowTableGenerators(grammarEntryTable, tokens)
        for(entry in firstAndFollowTableGenerators.first){
            println("ENTRY: ${entry}")
        }
    }

    private fun FindAnonymousTokens() {
        for (rule in grammarEntryTable) {
            for (desc in rule.value) {
                for(term in desc){
                    if(term is TerminalTerm){
                        val tokenName : String = unescapeString(term.value)
                        if(!tokens.contains(tokenName)){
                            tokens.add(tokenName)
                            dLog("Token found: $tokenName")
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

