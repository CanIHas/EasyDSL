package can.i.has.easy_dsl.impl.utils


class ReflectionUtils {
    static final Map<Class, Class> BOXES = [
        (Boolean): boolean,
        (Character): char,
        (Byte): byte,
        (Short): short,
        (Integer): int,
        (Long): long,
        (Float): float,
        (Double): double
    ]

    static isInstance(Class clazz, o){
        if (BOXES.containsKey(clazz)){
            return clazz.isInstance(o) || BOXES[clazz].isInstance(o)
        }
        clazz.isInstance(o)
    }

    static final List<String> specialMembers = [
        "metaClass",
        "getMetaClass",
        "class",
        "getClass",
        "getAt",
        "putAt",
        "getProperty",
        "setProperty",
        "getAttribute",
        "setAttribute",
        "invokeMethod",
        "propertyMissing",
        "methodMissing"
    ].asImmutable()

    static boolean isSpecial(String fieldOrMethod){
        specialMembers.contains(fieldOrMethod)
    }
}
