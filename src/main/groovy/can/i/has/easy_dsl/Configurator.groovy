package can.i.has.easy_dsl

import can.i.has.easy_dsl.api.field.WithMethod
import can.i.has.easy_dsl.impl.ConfigurationResolver

import static can.i.has.easy_dsl.impl.ConfiguratorUtils.*

class Configurator {
    private Object $traitThis;

    static Closure defaultConstructor = { Class clazz ->
        switch (clazz){
            case Map: return [:]
            case List: return []
            case Set: return [].toSet()
            default: clazz.newInstance()
        }
    }


    Configurator(Object $traitThis) {
        this.$traitThis = $traitThis
    }

    def methodMissing(String name, args){
        WithMethod annotation = getFieldWithMethodAnnotation($traitThis, name)
        if (annotation){
            if (ConfigurationResolver.instance.propertyType($traitThis.class, name) == Closure) {
                assert args.size() == 1 && args[0] instanceof Closure
                $traitThis.metaClass.setProperty($traitThis, name, args[0])
            } else {
                def a = preprocessArgs(args)
                doWithMethod($traitThis, annotation, name, a[0], a[1], a[2])
            }
        } else if (isDelegatedMethod($traitThis, name)){
            $traitThis.metaClass.invokeMethod($traitThis, name, args)
        } else {
            throw new MissingMethodException(name, $traitThis.class, args)
        }
    }

    def propertyMissing(String name){
        if (isFieldWithGetter($traitThis, name)){
            return $traitThis.metaClass.getProperty($traitThis, name)
        }
        throw new MissingPropertyException(name, $traitThis.class)
    }
    def propertyMissing(String name, val){
        if (isFieldWithSetter($traitThis, name)){
            return $traitThis.metaClass.setProperty($traitThis, name, val)
        }
        throw new MissingPropertyException(name, $traitThis.class)
    }

}
