import java.lang.Exception
import java.lang.reflect.InvocationTargetException
import kotlin.concurrent.thread
import kotlin.reflect.jvm.kotlinFunction

class BNFASTTreeVisitor{

    val grammar : Grammar = Grammar()

    fun visit(node : BNFSyntaxNodes.AstNode){
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
        for (n in node.children){
            visit(n)
        }
    }

    private fun p_RuleNameDeclarationNode(n : BNFSyntaxNodes.RuleNameDeclarationNode){

    }

    private fun p_RuleBody(n : BNFSyntaxNodes.AstNode){

    }

    private fun p_RuleDescription(n : BNFSyntaxNodes.AstNode){

    }

    private fun p_RuleReferenceNode(n : BNFSyntaxNodes.AstNode){

    }

    private fun p_LiteralNode(n : BNFSyntaxNodes.AstNode){

    }

    private fun p_OptionalOpNode(n : BNFSyntaxNodes.AstNode){

    }
}