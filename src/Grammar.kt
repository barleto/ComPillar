import java.math.BigInteger
import java.security.MessageDigest

class Grammar{

    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    val table: HashMap<String,MutableList<MutableList<Term>>> = hashMapOf()

    fun createRule(name : BNFSyntaxNodes.RuleNameDeclarationNode, ruleBody: BNFSyntaxNodes.RuleBody){
        if(!table.containsKey(name.ruleName)){
            table[name.ruleName] = mutableListOf()
        }
        val entry = table[name.ruleName]
        for(ruleDesc in ruleBody.children){
            val description : MutableList<Term> = mutableListOf()
            entry!!.add(description)
            for(term in ruleDesc.children){
                lateinit var grammarTerm : Term
                var node = if(term.first() is BNFSyntaxNodes.OptionalOpNode){
                    term.first().first()
                }else{
                    term.first()
                }
                when(node) {
                    is BNFSyntaxNodes.LiteralNode -> {
                        grammarTerm = LiteralTerm(node.value)
                    }
                    is BNFSyntaxNodes.RuleReferenceNode ->{
                        grammarTerm = RuleNameTerm(node.value)
                    }
                }
                if(term.first() is BNFSyntaxNodes.OptionalOpNode){
                    grammarTerm = OptionalTerm(grammarTerm!!)
                }
                description.add(grammarTerm!!)
            }
        }
    }

    fun addToken(regexp : String){

    }

    abstract class Term
    class OptionalTerm(val term: Term) : Term()
    class RuleNameTerm(val value : String) : Term()
    class LiteralTerm(val value : String) : Term()
}