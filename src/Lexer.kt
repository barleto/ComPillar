import java.util.regex.Pattern

class Lexer(var input: String) {

    var lineno: Int = 1
    var inputPointer = 0
    val definitions: MutableList<TokenDefinition> = mutableListOf()

    init {
        //https://stackoverflow.com/questions/2498635/java-regex-for-matching-quoted-string-with-escaped-quotes
        // '([^\\']+|\\([btnfr"'\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|"([^\\"]+|\\([btnfr"'\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*"
        definitions.add(TokenDefinition("'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"", TokenType.LITERAL))
        println(definitions.last().pattern)

        definitions.add(TokenDefinition("<[_a-zA-z][-_a-zA-z0-9]*>", TokenType.RULE))
        definitions.add(TokenDefinition("(:)\\1=", TokenType.ARROW))
        definitions.add(TokenDefinition("[|]", TokenType.OR))
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
        val token: Token
        while (inputPointer < input.length) {
            val match = scan()
            if (match.isMatch && match.start == inputPointer) {
                inputPointer = match.end
                return Token(match.type, match.value, lineno, inputPointer)
            } else {
                if (input[inputPointer] == '\n') {
                    lineno += 1
                }
                inputPointer += 1
            }
        }
        return Token(TokenType.EOF, "", 0,0)
    }
}

class Token(val type: TokenType, val value: String, val lineno: Int, val startPos: Int)

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
    LITERAL,
    OR,
    ARROW,
    RULE,
    EOF
}