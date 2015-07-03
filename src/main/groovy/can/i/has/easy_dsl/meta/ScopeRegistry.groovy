package can.i.has.easy_dsl.meta


class ScopeRegistry {
    static Map<Class, Map<String, String>> registry = [:].withDefault {[:]}

    static Map<String, String> getScopeMapping(Class clazz){
        return registry[clazz]
    }
}
