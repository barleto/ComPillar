package com.charf.compillar.lexer

import java.util.regex.Pattern

class Lexer(var input: String) {

    var lineno: Int = 1
    var lineCharCount: Int = 0
    var inputPointer = 0
    val definitions: MutableList<TokenDefinition> = mutableListOf()

    init {
        //https://stackoverflow.com/questions/2498635/java-regex-for-matching-quoted-string-with-escaped-quotes
        definitions.add(TokenDefinition("\"(?:[\\\\]\"|[^\"])*\"", TokenType.LITERAL))
        definitions.add(TokenDefinition("<[_a-zA-z][-_a-zA-z0-9]*>", TokenType.RULE))
        definitions.add(TokenDefinition("::=", TokenType.ARROW))
        definitions.add(TokenDefinition("[|]", TokenType.OR))
        definitions.add(TokenDefinition("[(]", TokenType.LEFT_PAREN))
        definitions.add(TokenDefinition("[)]", TokenType.RIGHT_PAREN))
        definitions.add(TokenDefinition("[+]", TokenType.PLUS))
        definitions.add(TokenDefinition("[*]", TokenType.STAR))
        definitions.add(TokenDefinition("IGNORE", TokenType.IGNORE_RULE))
        definitions.add(TokenDefinition(";", TokenType.SEMI_COL))
        definitions.add(TokenDefinition("//.*(?=\n)", TokenType.ONE_LINE_COMM))
        definitions.add(TokenDefinition("\\/\\*[\\s\\S]*?\\*\\/", TokenType.MULTI_LINE_COMM))
    }

    fun scan(): TokenDefinition.MatchResult {
        for (tokDef in definitions) {
            val matchRes = tokDef.isMatch(input, inputPointer)
            if (matchRes.isMatch && matchRes.start == inputPointer) {
                return matchRes
            }
        }
        return TokenDefinition.MatchResult(false, "")
    }

    fun getToken(): Token {
        while (inputPointer < input.length) {
            val match = scan()
            if (match.isMatch && match.start == inputPointer) {
                inputPointer = match.end
                var start = lineCharCount;
                lineCharCount += match.value.length;
                lineno += match.value.count{it == '\n'};
                return Token(match.type, match.value, lineno, start)
            } else {
                if (input[inputPointer] == '\n') {
                    lineno += 1
                    lineCharCount = 0;
                    inputPointer += 1
                    continue
                } else if(input[inputPointer].toString().isBlank()) {
                    inputPointer += 1;
                    lineCharCount += 1;
                    continue;
                }
                throw Exception("Unidentified character '${input[inputPointer]}' at [${lineno},${inputPointer}]");
            }
        }
        return Token(TokenType.EOF, "", lineno, lineCharCount)
    }
    
    fun getTokens() : List<Token> {
        val result = mutableListOf<Token>();
        do{
            val token = getToken();
            result.add(token);
        } while(token.type != TokenType.EOF);
        return result;
    }
}

class Token(val type: TokenType, val value: String, val lineno: Int, val startPos: Int){
    override fun toString(): String {
        return "${this.type.toString()}[${this.lineno},${this.startPos},${this.value.length}] = ${this.value}";
    }
}

class TokenDefinition(patternString: String, val type: TokenType) {
    val pattern: Pattern

    init {
        pattern = Pattern.compile(patternString)
    }

    fun isMatch(input: String, startIndex: Int): MatchResult {
        val matcher = pattern.matcher(input)
        return if (matcher.find(startIndex)) {
            MatchResult(true, matcher.group(), type, matcher.start(), matcher.end())
        } else {
            MatchResult(false, "")
        }

    }

    data class MatchResult(val isMatch: Boolean, val value: String, val type: TokenType = TokenType.LITERAL, val start: Int = 0, val end: Int = 0) {
        fun length(): Int {
            return value.length
        }
    }
}

enum class TokenType {
    EOF,
    LITERAL,
    IGNORE_RULE,
    OR,
    ARROW,
    RULE,
    LEFT_PAREN,
    RIGHT_PAREN,
    PLUS,
    STAR,
    SEMI_COL,
    ONE_LINE_COMM,
    MULTI_LINE_COMM,
}
