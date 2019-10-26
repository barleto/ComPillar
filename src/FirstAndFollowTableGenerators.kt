import java.lang.Exception

class FirstAndFollowTableGenerators(val grammarEntryTable: HashMap<String, MutableList<MutableList<Grammar.Term>>>, val tokens : MutableSet<String>){

    var first : HashMap<String,MutableSet<String>> = hashMapOf()
    var follow : HashMap<String,MutableSet<String>> = hashMapOf()

    init{
        for (rule in grammarEntryTable) {
            if(!first.containsKey(rule.key)){
                createFirstSetForRule(Pair(rule.key, rule.value))
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
                is Grammar.EmptyTerm -> {termIsEmpty(rule, term)}
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

    private fun termIsEmpty(rule: Pair<String, MutableList<MutableList<Grammar.Term>>>, term: Grammar.EmptyTerm) {
        first[rule.first]!!.add("")
    }

    private fun termIsTerminal(rule: Pair<String, MutableList<MutableList<Grammar.Term>>>, term: Grammar.TerminalTerm) {
        first[rule.first]!!.add(term.value)
    }
}