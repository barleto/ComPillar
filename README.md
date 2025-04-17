# ComPillar
Study and implementation of LL(1) parser

A wordplay wih Compiler and Pillar, because this repository was made to study the pillars of compilation and parsing.

BNF in BNF:
```
<language> ::= <rule>*;
<rule> ::= <non-term> "::=" <prod-list> ";";
<prod-list> ::= <prod> | <prod> ("[|]" <prod>)+;
<prod> ::= <seg> | <seg><seg>+;
<seg> ::= <elem-group> | <elem-group>"+" | <elem-group>"*";
<elem-group> :: <elem> | "[(]"<elem>"[)]";
<elem> ::= <non-term> | <term> | <prod>;

<non-term> ::= "<[_a-zA-z][-_a-zA-z0-9]*>";
<term> ::= "\"(?:[\\]\"|[^\"])*\"";
```
