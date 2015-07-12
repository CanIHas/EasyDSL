package can.i.has.easy_dsl.impl.utils


class MOPUtils {
    static void setProperty(obj, String name, val) {
        if (!obj instanceof Map && obj.metaClass)
            obj.metaClass.setProperty(obj, name, val)
        else
            obj."$name" = val
    }

    static def getProperty(obj, String name) {
        if (!obj instanceof Map && obj.metaClass)
            obj.metaClass.getProperty(obj, name)
        else
            obj."$name"
    }

    static def hasProperty(obj, String name) {
        if (obj.metaClass)
            return obj.metaClass.hasProperty(name)
        else
            try {
                obj."$name"
                return true
            } catch (MissingPropertyException mpe) {
                return false
            }
    }

    static def invoke(obj, String name, Object[] args) {
        if (obj.metaClass)
            return obj.metaClass.invokeMethod(obj, name, args)
        else
            return obj."$name"(*args)
    }

    static boolean hasMethod(obj, String name, Object[] args) {
        if (obj.metaClass && obj.metaClass.getMetaMethod(name, args) != null)
            return true
        obj.class.methods.any {
            it.name == name &&
                it.parameterTypes.size() == args.size() &&
                it.parameterTypes.eachWithIndex { Class<?> entry, int i ->
                    entry.isInstance(args[i])
                }
        }
    }
}
