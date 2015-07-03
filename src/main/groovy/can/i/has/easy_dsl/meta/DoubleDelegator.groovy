package can.i.has.easy_dsl.meta

import groovy.transform.Canonical

@Canonical
class DoubleDelegator {
    Object first
    Object second

    def propertyMissing(String name){
        first."$name" ?: second."$name"
    }

    def propertyMissing(String name, val){
        try {
            first."$name" = val
        } catch (MissingPropertyException mpe){
            second."$name" = val
        }
    }

    def invokeMethod(String name, args){
        try {
            first.metaClass.invokeMethod(name, args)
        } catch (MissingMethodException mpe){
            second.metaClass.invokeMethod(name, args)
        }
    }
}