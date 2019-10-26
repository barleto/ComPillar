package com.charf.compillar.grammar

class FollowTable(private val firstTable : FirstTable, private val grammarTable: HashMap<String, MutableList<MutableList<Grammar.Term>>>, startNonTerminal : String, val debug : Boolean = false){

    var follow : HashMap<String,MutableSet<String>> = hashMapOf()

    init{
        //place $ in FOLLOW for start symbol
        follow[startNonTerminal] = mutableSetOf("$")

        for (rule in grammarTable){
            createFollow(Pair(rule.key, rule.value))
        }
        if(debug){
            for(f in follow){
                println("FOLLOW: $f")
            }
        }
    }

    private fun createFollow(rule: Pair<String, MutableList<MutableList<Grammar.Term>>>) {
        if(follow.containsKey(rule.first)){
            return
        }
        follow[rule.first] = mutableSetOf()
        for (entry in grammarTable) {
            if(entry.key == rule.first){
                continue
            }
            for(description in entry.value) {
                var filteredDescription = description.filter { it.value == rule.first }
                if(!filteredDescription.any()){
                    continue
                }
                //If there is a production A-> aB, then everything in FOLLOW(A) is in FOLLOW(B).
                if(filteredDescription[0] == description.last()){
                    createFollow(Pair(entry.key, grammarTable[entry.key]!!))
                    val entryFollow = follow[entry.key]!!
                    follow[rule.first]!!.addAll(entryFollow)
                    break
                }else{
                    val followingTermIndex = description.indexOf(filteredDescription.last()) + 1
                    val nextTerm = description[followingTermIndex]
                    if(firstTable[nextTerm.value]!!.contains("")){
                        //If there is a production A -> aBb, where FIRST(b) contains empty, then everything in FOLLOW(A) is in FOLLOW(B).
                        createFollow(Pair(entry.key, grammarTable[entry.key]!!))
                        val entryFollow = follow[entry.key]!!
                        entryFollow.remove("")
                        follow[rule.first]!!.addAll(entryFollow)
                    }else{
                        //If there is a production A -> aBb, then everything in FIRST(b) except empty is in FOLLOW(B).
                        follow[rule.first]!!.addAll(firstTable[nextTerm.value]!!)
                    }
                }
            }
        }
    }
}