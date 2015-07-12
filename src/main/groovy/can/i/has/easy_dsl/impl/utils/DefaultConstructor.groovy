package can.i.has.easy_dsl.impl.utils


class DefaultConstructor extends Closure {
    DefaultConstructor(Object owner, Object thisObject) {
        super(owner, thisObject)
    }

    DefaultConstructor(Object owner) {
        super(owner)
    }

    def doCall(Class clazz) {
        switch (clazz) {
            case Map: return [:]
            case List: return []
            case Set: return [].toSet()
            default: clazz.newInstance()
        }
    }
}
