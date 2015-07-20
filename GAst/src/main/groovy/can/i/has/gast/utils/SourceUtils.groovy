package can.i.has.gast.utils

import java.util.regex.Pattern


class SourceUtils {
    static String word = /\w+/
    static String type = "$word([.]$word)*"
    static String name = word
    static String var = "($type$space)?$name"
    static lpar = /[(]/
    static rpar = /[)]/
    static space = /\s+/
    static possibleSpace = /\s*/
    static typeDef = /(class|interface|enum)/
    static inherit = /(extends|implements)/
    static modifier = /(static|volatile|public|private|protected|abstract|synchronized|final)/
    static comma = "[,]"
    static lbrace = /\{/
    static rbrace = /\}/
    static equals = /[=]/
    static list(String el, String sep) { "($el($sep$el)*)" }

    static String typeNameRegex = "$typeDef$space" +
        "(?<typeName>$name)$possibleSpace" +
        "($space$inherit$space" +
        "${list(type, "$possibleSpace$comma$possibleSpace")})?" +
        "$possibleSpace$lbrace"

    static Pattern typeNamePattern = Pattern.compile(typeNameRegex)

    static String methodNameRegex = "($modifier$space)*" +
        "($type$space)?" +
        "(?<methodName>$name)$possibleSpace" +
        "$lpar$possibleSpace(${list(var, "$possibleSpace$comma$possibleSpace")}$possibleSpace)?$rpar" +
        "$possibleSpace$lbrace"

    static Pattern methodNamePattern = Pattern.compile(methodNameRegex)

    static String fieldNameRegex = "($modifier$space)*" +
        "($type$space)?" +
        "(?<fieldName>$name)$possibleSpace$equals"

    static Pattern fieldNamePattern = Pattern.compile(fieldNameRegex)

    static String getTypeName(String source){
        def matcher = typeNamePattern.matcher(source)
        matcher.find()
        return matcher.group("typeName")
    }

    static String getMethodName(String source){
        def matcher = methodNamePattern.matcher(source)
        matcher.find()
        return matcher.group("methodName")
    }

    static String getFieldName(String source){
        def matcher = fieldNamePattern.matcher(source)
        matcher.find()
        return matcher.group("fieldName")
    }

    static String getPackage(String source){
        def line = source.trim().readLines().first()
        line.startsWith("package") ?
            line.replaceFirst("package", "").trim() :
            null
    }
}
