package com.charf.compillar.grammar

class Grammar(val grammarEntryTable: HashMap<String, MutableList<MutableList<Term>>>, val debug: Boolean = false){

    var tokens : MutableSet<String> = mutableSetOf()
    private var firstTable: FirstTable
    private var followTable: FollowTable

    init {
        FindAnonymousTokens()
        firstTable = FirstTable(grammarEntryTable, tokens, debug)
        //TODO CHARF get first symbol automatically
        followTable = FollowTable(firstTable, grammarEntryTable, "<sintaxe>", debug)
    }

    private fun FindAnonymousTokens() {
        for (rule in grammarEntryTable) {
            for (desc in rule.value) {
                for(term in desc){
                    if(term is TerminalTerm){
                        val tokenName : String = term.value
                        if(!tokens.contains(tokenName)){
                            tokens.add(tokenName)
                            dLog("com.charf.Token found: $tokenName")
                        }
                    }
                }
            }
        }
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

