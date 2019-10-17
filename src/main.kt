fun main(args: Array<String>) {
    val input = """<sintaxe> ::= <lista-de-regras>
    <lista-de-regras> ::= <regra> | <regra> <lista-de-regras>
    <regra>   ::=  <nome-da-regra>  "::=" <expressao>
    <expressao>  ::= <lista-de-termos> | <lista-de-termos> "|" <expressao>
    <lista-de-termos>  ::= <termo> | <termo>  <lista-de-termos>
    <termo> ::= <LITERAL>? | <nome-da-regra>?
    <nome-da-regra> ::= "<" <LITERAL> ">"
    <LITERAL> ::= '".*\"'
    """

    var lex = Lexer(input)
    while (true) {
        val tok = lex.getToken()
        println("<${tok.type}, ${tok.value}> ")
        if (tok.type == TokenType.EOF) {
            break
        }
    }
    lex = Lexer(input)
    val bnfAstTree = LLParser(lex, true).parse()
    BNFASTTreeTraversser().f()
}

class Grammar{

    val table: HashMap<String,MutableList<String>> = hashMapOf()

    fun addNonTerminal(lala: String){
        table[lala] = mutableListOf()
    }
    fun addTerminal(){

    }
    fun addRule(){

    }
}

class BNFASTTreeTraversser{


    fun f(){
        this.javaClass.methods.size
    }

}

