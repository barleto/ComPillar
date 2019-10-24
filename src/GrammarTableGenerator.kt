import java.lang.Exception
import java.lang.reflect.InvocationTargetException

class GrammarTableGenerator(var debug : Boolean = false){

    val table: HashMap<String,MutableList<MutableList<String>>> = hashMapOf()

    fun start(node : BNFSyntaxNodes.AstNode) : Grammar {
        visit(node)
        return Grammar(table)
    }

    private fun visit(node : BNFSyntaxNodes.AstNode){
        var visitmethod = getMethodFromClassName(node)
        visitmethod(node)
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
        if(node is BNFSyntaxNodes.AstNodeWithChildren) {
            for (n in node.children) {
                visit(n)
            }
        }
    }

    private fun p_RuleExpressionNode(n : BNFSyntaxNodes.RuleExpressionNode){
        table[n.ruleName] = mutableListOf()
        for(rd in n.body.castChildren<BNFSyntaxNodes.RuleDescription>()){
            var description : MutableList<String> = mutableListOf()
            dLog(n.ruleName)
            dLog(" -> ")
            for(t in rd.children){
                when(t){
                    is BNFSyntaxNodes.LiteralNode -> description.add(t.value)
                    is BNFSyntaxNodes.RuleReferenceNode -> description.add(t.value)
                    is BNFSyntaxNodes.EmptyLiteralNode -> description.add("")
                }
                dLog(description.last()+ " ")
            }
            table[n.ruleName]!!.add(description)
            println()
        }
    }

    private fun dLog( s : Any){
        if (!debug) return
        print(s)
    }
}