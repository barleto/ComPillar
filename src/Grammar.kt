import java.math.BigInteger
import java.security.MessageDigest

class Grammar{

    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    val table: HashMap<String,MutableList<MutableList<String>>> = hashMapOf()

    fun createRule(name : BNFSyntaxNodes.RuleNameDeclarationNode, ruleBody: BNFSyntaxNodes.RuleBody){

    }

    fun addToken(regexp : String){

    }
}