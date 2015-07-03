package can.i.has.easy_dsl.meta

import can.i.has.easy_dsl.api.modifiers.CollectScope


class GatherCategory {
    static private String name
    static private Object target
    static private Closure appender

    static void $setContext(String name, target, Closure appender){
        GatherCategory.name = name
        GatherCategory.target = target
        GatherCategory.appender = appender
    }

    static def getTarget(){
        target
    }

    static def methodMissing(String name, args){
        if (name == GatherCategory.name)
            return appender.call(args)
        throw new MissingMethodException(name, CollectScope, args)
    }
}
