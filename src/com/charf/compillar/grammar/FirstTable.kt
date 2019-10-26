package com.charf.compillar.grammar

import java.lang.Exception

class FirstTable(val grammarEntryTable: HashMap<String, MutableList<MutableList<Grammar.Term>>>, val tokens : MutableSet<String>, val debug : Boolean = false){

    var first : HashMap<String,MutableSet<String>> = hashMapOf()

    init{
        for (rule in grammarEntryTable) {
            if(!first.containsKey(rule.key)){
                createFirstSetForRule(Pair(rule.key, rule.value))
            }
        }
        //add tokens
        //add tokens
        for(token in tokens){
            first[token] = mutableSetOf()
            first[token]!!.add(token)
        }

        if(debug) {
            for (f in first) {
                println("FIRST: $f")
            }
        }

    }

    private fun createFirstSetForRule(rule: Pair<String, MutableList<MutableList<Grammar.Term>>>) {
        if(first.containsKey(rule.first)){
            return
        }
        first[rule.first] = mutableSetOf()
        for (description in rule.second){
            val term = description[0]
            when(term){
                is Grammar.TerminalTerm -> {termIsTerminal(rule,term)}
                is Grammar.EmptyTerm -> {termIsEmpty(rule)}
                is Grammar.NonTerminalTerm -> {termIsNonTerminal(rule, description)}
            }
        }
    }

    private fun termIsNonTerminal(rule: Pair<String, MutableList<MutableList<Grammar.Term>>>, description: MutableList<Grammar.Term>) {
        for(term in description){
            when(term){
                is Grammar.TerminalTerm -> {
                    first[rule.first]!!.add(term.value)
                    return
                }
                is Grammar.NonTerminalTerm -> {
                    createFirstSetForRule(Pair(term.value, grammarEntryTable[term.value]!!))
                    val firstOfTerm = first[term.value]!!
                    first[rule.first]!!.addAll(firstOfTerm)
                    if(!firstOfTerm.contains("")){
                        return
                    }
                }
            }
        }
        first[rule.first]!!.add("")
    }

    private fun termIsEmpty(rule: Pair<String, MutableList<MutableList<Grammar.Term>>>) {
        first[rule.first]!!.add("")
    }

    private fun termIsTerminal(rule: Pair<String, MutableList<MutableList<Grammar.Term>>>, term: Grammar.TerminalTerm) {
        first[rule.first]!!.add(term.value)
    }

    operator fun get(vararg termsList : String) : MutableSet<String>? {
        return get(termsList.asList())
    }

    operator fun get(termsList : List<String>) : MutableSet<String>?{
        if(termsList.isEmpty()){
            return null
        }else if(termsList.size == 1){
            return first[termsList[0]]
        }else{
            val resultingSet = mutableSetOf<String>()
            for(term in termsList){
                val firstOfTerm = first[term] ?: throw Exception("Term $term was not found at the First table.")
                resultingSet.addAll(firstOfTerm)
                if(!firstOfTerm.contains("")){
                    break
                }
            }
            return resultingSet
        }
    }
}