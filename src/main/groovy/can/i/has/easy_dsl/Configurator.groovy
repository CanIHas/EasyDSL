package can.i.has.easy_dsl

import can.i.has.easy_dsl.impl.ConfigurationSetupResolver

import groovy.transform.Canonical

@Canonical
class Configurator {
    Object traitThis;

//    def methodMissing(String name, Object[] args){
//        def toCall = getMethods(traitThis.class)?.get(name) ?:
//            getGetters(traitThis.class)?.get() ?:
//                getSetters(traitThis.class)?.get()
//        assert toCall //todo:exception
//        return toCall.call(traitThis, *args)
//    }
//
    def propertyMissing(String name){
//        def toCall = getGetters(traitThis.class)?.get("get${name.capitalize()}")
//        assert toCall //todo:exception
//        return toCall.call(traitThis)
        def closure = ConfigurationSetupResolver.instance.resolveGetter(traitThis.class, name)
        assert closure //todo: exception
        closure.call(traitThis)
    }
    def propertyMissing(String name, val){
//        def toCall = getSetters(traitThis.class)?.get("set${name.capitalize()}")
//        assert toCall //todo:exception
//        return toCall.call(traitThis, val)
        def closure = ConfigurationSetupResolver.instance.resolveSetter(traitThis.class, name)
        assert closure //todo: exception
        closure.call(traitThis, val)
    }
}
