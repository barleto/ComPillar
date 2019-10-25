fun main(args: Array<String>) {
    val input = """<sintaxe> ::= <lista-de-regras>
<lista-de-regras> ::= <regra> | <regra> <lista-de-regras>
<regra>   ::=  <nome-da-regra>  "::=" <expressao>
<expressao>  ::= <lista-de-termos> | <lista-de-termos> "|" <expressao>
<lista-de-termos>  ::= <termo> | <termo>  <lista-de-termos>
<termo> ::= <LITERAL> | <nome-da-regra> |
<nome-da-regra> ::= "<" <LITERAL> ">"
<LITERAL> ::= "'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\""
"""

    var lex = Lexer(input)
    while (true) {
        val tok = lex.getToken()
        println("<${tok.type}, ${tok.value}> ")
        if (tok.type == TokenType.EOF) {
            var a = ""
            break
        }
    }
    lex = Lexer(input)
    val bnfAstTree = LLParser(lex, true).parse()
    var grammar = GrammarGenerator(true).start(bnfAstTree)
}

