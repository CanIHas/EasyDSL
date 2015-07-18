package can.i.has.gast.utils

import java.util.regex.Pattern


class SourceUtils {
    static Pattern typeNamePattern = Pattern.compile( // todo: add generics support
        /(?:class|interface|enum)\s+(?<typeName>\w+)\s*(?:\s+(extends|implements)\s+\w+([,]\w+)*)\{/
    )

    static String getTypeName(String source){
        def matcher = typeNamePattern.matcher(source)
        matcher.find()
        matcher.group("typeName")
    }

    static String getPackage(String source){
        def line = source.trim().readLines().first()
        line.startsWith("package") ?
            line.replaceFirst("package", "").trim() :
            null
    }
}
