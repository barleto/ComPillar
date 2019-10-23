import java.lang.Exception
import java.lang.reflect.InvocationTargetException
import kotlin.concurrent.thread
import kotlin.reflect.jvm.kotlinFunction

class BNFASTTreeVisitor(var debug : Boolean = false){
    private var stackCount : Int = -1

    val grammar : Grammar = Grammar()
    val table: HashMap<String,MutableList<MutableList<String>>> = hashMapOf()

    fun start(node : BNFSyntaxNodes.AstNode){
        visit(node)
    }

    private fun visit(node : BNFSyntaxNodes.AstNode){
        var visitmethod = getMethodFromClassName(node)
        stackCount ++
        visitmethod(node)
        stackCount --
    }

    private fun getMethodFromClassName(node: BNFSyntaxNodes.AstNode): (BNFSyntaxNodes.AstNode)->Unit {
        var className = node.javaClass.simpleName
        val indexOfMethod = this.javaClass.declaredMethods.map { i -> i.name }.indexOf("p_${className}")
        if(indexOfMethod < 0){
            return {n -> this.p_default(n)}
        }else{
            val method = this.javaClass.declaredMethods[indexOfMethod]
            method.isAccessible = true
            run {
                return { n ->
                    try{
                        method.invoke(this, n)
                    }catch (e : InvocationTargetException){
                        val ne = Exception("Exception in function ${method.name} : ${e.targetException}")
                        throw ne
                    }
                }
            }
        }
    }

    private fun p_default(node : BNFSyntaxNodes.AstNode){
        pLog()
        if(node is BNFSyntaxNodes.AstNodeWithChildren) {
            for (n in node.children) {
                visit(n)
            }
        }
    }

    private fun p_RuleExpressionNode(n : BNFSyntaxNodes.RuleExpressionNode){
        pLog()
        table[n.ruleName] = mutableListOf(mutableListOf())
        for(rd in n.body.castChildren<BNFSyntaxNodes.RuleDescription>()){
            for(t in rd.children){
                //TODO
            }
        }
    }

    private fun p_RuleBody(n : BNFSyntaxNodes.RuleBody){
        pLog()
    }

    private fun p_RuleDescription(n : BNFSyntaxNodes.RuleDescription){
        pLog()
    }

    private fun p_RuleReferenceNode(n : BNFSyntaxNodes.RuleReferenceNode){
        pLog()
    }

    private fun p_LiteralNode(n : BNFSyntaxNodes.LiteralNode){
        pLog()
    }

    private fun p_EmptyLiteralNode(n : BNFSyntaxNodes.EmptyLiteralNode){
        pLog()
    }

    private fun pLog(){
        if (!debug) return
        val i = stackCount
        println("${"| ".repeat(i)}${Thread.currentThread().stackTrace[2].methodName}")
    }
}