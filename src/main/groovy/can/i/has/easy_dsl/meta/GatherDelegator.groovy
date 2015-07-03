package can.i.has.easy_dsl.meta

import can.i.has.easy_dsl.Configurator
import can.i.has.easy_dsl.impl.DelegationUtils

import groovy.transform.Canonical

@Canonical
class GatherDelegator {
    String name
    Object target
    Closure $appender
    Configurator $configurator

    GatherDelegator(String name, Object target, Closure $appender, Configurator $configurator) {
        this.name = name
        this.target = target
        this.$appender = $appender
        this.$configurator = $configurator
    }

    def methodMissing(String name, args){
        if (name == this.name)
            return DelegationUtils.callWithDelegate(this, $appender.call(args))
        throw new MissingMethodException(name, Configurator, args)
    }

    def propertyMissing(String name){
        $configurator.metaClass.getProperty($configurator, name)
    }

    def propertyMissing(String name, val){
        $configurator.metaClass.setProperty($configurator, name, val)
    }
}
