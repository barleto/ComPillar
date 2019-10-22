class LLParser(val lexer: Lexer, var debug : Boolean = false) {
    private var head: Token = lexer.getToken()
    private var lookAhead: Token = lexer.getToken()
    private var astRoot : BNFSyntaxNodes.SyntaxNode = BNFSyntaxNodes.SyntaxNode()
    private var beginStackCount : Int = 0
    fun consumeToken(type: TokenType) {
        if (head.type == type) {
            //println("CONSUMED ${type}")
            head = lookAhead
            lookAhead = lexer.getToken()
        } else {
            throw Error("Expected ${type}, found ${head.type} at line ${head.lineno} - pos ${head.startPos}")
        }
    }

    fun parse(): BNFSyntaxNodes.SyntaxNode {
        try {
            program()
        }catch (e: Exception){
            println(e.toString())
        }
        return astRoot
    }

    private fun program() {
        setStackCount()
        pLog()
        ruleList(astRoot)
    }

    private fun setStackCount() {
        beginStackCount = Thread.currentThread().stackTrace.size
    }

    private fun ruleList(syntaxNode: BNFSyntaxNodes.SyntaxNode) {
        pLog()
        while (head.type == TokenType.RULE) {
           ruleExpression(syntaxNode)
        }
    }

    private fun ruleExpression(syntaxNode: BNFSyntaxNodes.SyntaxNode) {
        pLog()
        syntaxNode.add(BNFSyntaxNodes.RuleExpressionNode())
        ruleDefinition(syntaxNode.last() as BNFSyntaxNodes.RuleExpressionNode)
    }

    private fun ruleDefinition(ruleExpressionNode: BNFSyntaxNodes.RuleExpressionNode) {
        pLog()
        ruleExpressionNode[0] = BNFSyntaxNodes.RuleNameDeclarationNode(head.value)
        consumeToken(TokenType.RULE)
        consumeToken(TokenType.ARROW)
        ruleExpressionNode[1] = BNFSyntaxNodes.RuleBody()
        ruleBody(ruleExpressionNode[1] as BNFSyntaxNodes.RuleBody)
    }

    private fun ruleBody(ruleBody: BNFSyntaxNodes.RuleBody) {
        pLog()
        ruleBody.add(BNFSyntaxNodes.RuleDescription())
        ruleDescription(ruleBody.last() as BNFSyntaxNodes.RuleDescription)

        while(head.type == TokenType.OR){
            consumeToken(TokenType.OR)
            ruleBody.add(BNFSyntaxNodes.RuleDescription())
            ruleDescription(ruleBody.last() as BNFSyntaxNodes.RuleDescription)
        }
    }

    private fun ruleDescription(ruleDescription: BNFSyntaxNodes.RuleDescription) {
        pLog()
        while ((head.type == TokenType.LITERAL || head.type == TokenType.RULE)) {
            getTerm(ruleDescription)
            if (isRuleDescriptionFinalized()) {
                break
            }
        }
        if (ruleDescription.children.size == 0) {
            EmptyLiteralAdded(ruleDescription)
        }
    }

    private fun EmptyLiteralAdded(ruleDescription: BNFSyntaxNodes.RuleDescription) {
        pLog()
        ruleDescription.add(BNFSyntaxNodes.EmptyLiteralNode())
    }

    private fun getTerm(ruleDescription: BNFSyntaxNodes.RuleDescription) {
        pLog()
        var resultingNode : BNFSyntaxNodes.AstNode? = null
        if(isRuleDescriptionFinalized()){
            resultingNode = BNFSyntaxNodes.EmptyLiteralNode()
        }else if (head.type == TokenType.RULE) {
            resultingNode = BNFSyntaxNodes.RuleReferenceNode(head.value)
            consumeToken(TokenType.RULE)
        } else if (head.type == TokenType.LITERAL) {
            resultingNode = BNFSyntaxNodes.LiteralNode(head.value)
            consumeToken(TokenType.LITERAL)
        }
        ruleDescription.add(resultingNode!!)
    }

    private fun isRuleDescriptionFinalized() = (head.type == TokenType.RULE && lookAhead.type == TokenType.ARROW) || head.type == TokenType.EOF

    private fun pLog(){
        if (!debug) return
        val i = Thread.currentThread().stackTrace.size - beginStackCount
        println("${"| ".repeat(i)}${Thread.currentThread().stackTrace[2].methodName}")
    }
}

class BNFSyntaxNodes {
    abstract class AstNode {
        val children : MutableList<AstNode> = mutableListOf()
        operator fun get(i : Int) : AstNode?{
            return children[i]
        }
        operator fun set(i : Int, v : AstNode){
            children.add(i, v)
        }
        fun add(v : AstNode){
            children.add(v)
        }
        fun last() : AstNode{
            return children.last()
        }
        fun first() : AstNode{
            return  children.first()
        }
    }
    class SyntaxNode : AstNode()
    class RuleExpressionNode : AstNode()
    class RuleNameDeclarationNode(val ruleName : String) : AstNode()
    class RuleBody : AstNode()
    class RuleDescription : AstNode()
    class RuleReferenceNode(val value : String) : AstNode()
    class LiteralNode(val value : String) : AstNode()
    class EmptyLiteralNode : AstNode()
}