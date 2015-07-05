package can.i.has.easy_dsl

import can.i.has.easy_dsl.impl.MetaClassProvider
import can.i.has.easy_dsl.impl.ScopeRegistry

class Configurator {
    Object traitThis;
//    String currentScope;
    List<String> scopeStack = []

    static Closure defaultConstructor = { Class clazz ->
        switch (clazz){
            case Map: return [:]
            case List: return []
            case Set: return [].toSet()
            default: clazz.newInstance()
        }
    }


    Configurator(Object traitThis) {
        this.traitThis = traitThis
        MetaClassProvider.instance.enhanceMetaClass(this)
        this.metaClass.initialize()
    }

    List getTarget(){
        if (!scopeStack)
            return null
        def currentScope = scopeStack.last()
        assert targetPerScope.containsKey(currentScope)
        return traitThis.metaClass.getProperty(traitThis, targetPerScope[currentScope])
    }

    Map<String, String> getTargetPerScope(){
        ScopeRegistry.getScopeMapping(this.traitThis.class)
    }

//    def methodMissing(String name, args){
//        WithMethod annotation = getFieldWithMethodAnnotation($traitThis, name)
//        Field collectedField = $traitThis.class.declaredFields.find {
//            getCollect(it)?.elementName() == name
//        }
//        Collect collectAnn = collectedField ? getCollect(collectedField) : null
//        if (annotation){
//            if (collectAnn) {
//                if (collectAnn.type() == Closure) {
//                    assert args.size() == 1 && args[0] instanceof Closure
//                    MOPUtils.getProperty($traitThis, collectedField.name).add (args[0])
//                } else {
//                    def a = preprocessArgs(args)
//                    return doWithMethod($traitThis, annotation, name, collectAnn.elementName(), a[0], a[1], a[2])
//                }
//            } else {
//                if (ConfigurationResolver.instance.propertyType($traitThis.class, name) == Closure) {
//                    assert args.size() == 1 && args[0] instanceof Closure
//                    $traitThis.metaClass.setProperty($traitThis, name, args[0])
//                    return
//                } else {
//                    def a = preprocessArgs(args)
//                    return doWithMethod($traitThis, annotation, null, name, a[0], a[1], a[2])
//                }
//            }
//        } else if (isDelegatedMethod($traitThis, name)){
//            return $traitThis.metaClass.invokeMethod($traitThis, name, args)
//        } else {
//            throw new MissingMethodException(name, $traitThis.class, args)
//        }
//    }
//
//    def propertyMissing(String name){
//        if (isFieldWithGetter($traitThis, name)){
//            return $traitThis.metaClass.getProperty($traitThis, name)
//        }
//        throw new MissingPropertyException(name, $traitThis.class)
//    }
//    def propertyMissing(String name, val){
//        if (isFieldWithSetter($traitThis, name)){
//            return $traitThis.metaClass.setProperty($traitThis, name, val)
//        }
//        throw new MissingPropertyException(name, $traitThis.class)
//    }

}
