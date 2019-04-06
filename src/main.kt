fun main(args: Array<String>) {
    val input = """
    s : rule_list
    rule_list : rule | rule rule_list
    rule : nterm ARROW desc_list
    nterm : ID
    desc_list : desc | desc OR desc_list
    desc : | name_list
    name_list : name | name name_list
    name : ID
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
    LLParser(lex).parse()
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

class LLParser(val lexer: Lexer) {
    var head: Token = lexer.getToken()
    var lookAhead: Token = lexer.getToken()
    var grammar: Grammar = Grammar()

    fun consumeToken(type: TokenType) {
        if (head.type == type) {
            //println("CONSUMED ${type}")
            head = lookAhead
            lookAhead = lexer.getToken()
        } else {
            println("Expected ${type}, found ${head.type} at line ${head.lineno} - pos ${head.startPos}")
            throw Error("")
        }
    }

    fun parse(): Grammar{
        try {
            program()
        }catch (e: Exception){

        }
        return grammar
    }

    private fun program() {
        println("prog")
        rule_list()
    }

    private fun rule_list() {
        println("   rule_list")
        while (head.type == TokenType.ID){
            rule()
        }
    }

    private fun rule() {
        println("       rule")
        nterm()
        arrow()
        desc_list()
    }

    private fun nterm() {
        println("           nterm")
        consumeToken(TokenType.ID)
    }

    private fun arrow() {
        println("           arrow")
        consumeToken(TokenType.ARROW)
    }

    private fun desc_list() {
        println("           desc_list")
        if(head.type == TokenType.OR){
            consumeToken(TokenType.OR)
            emty()
        }
        while (head.type == TokenType.ID && lookAhead.type != TokenType.ARROW) {
            desc()
            if (head.type != TokenType.OR) {
                break
            }
            t_or()
            if (lookAhead.type == TokenType.ARROW) {
                emty()
                break
            }
        }

    }

    private fun emty() {
        println("               empty")
    }

    private fun t_or() {
        println("               or")
        consumeToken(TokenType.OR)
    }

    private fun desc() {
        println("               desc")
        name_list()
    }

    private fun name_list() {
        println("                   name_list")
        while(head.type == TokenType.ID && lookAhead.type != TokenType.ARROW){
            name()
        }
    }

    private fun name() {
        println("                       name")
        consumeToken(TokenType.ID)
    }

}