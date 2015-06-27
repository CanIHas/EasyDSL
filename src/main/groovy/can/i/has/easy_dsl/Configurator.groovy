package can.i.has.easy_dsl

import can.i.has.easy_dsl.impl.ConfigurationSetupResolver

import groovy.transform.Canonical

@Canonical
class Configurator {
    Object traitThis;

    def methodMissing(String name, args){
        def closure = ConfigurationSetupResolver.instance.resolveMethod(traitThis.class, name)
        assert closure //todo: exception
        closure.call(traitThis, args)
    }

    def propertyMissing(String name){
        def closure = ConfigurationSetupResolver.instance.resolveGetter(traitThis.class, name)
        assert closure //todo: exception
        closure.call(traitThis)
    }
    def propertyMissing(String name, val){
        def closure = ConfigurationSetupResolver.instance.resolveSetter(traitThis.class, name)
        assert closure //todo: exception
        closure.call(traitThis, val)
    }
}
