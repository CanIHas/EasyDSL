package can.i.has.easy_dsl

import can.i.has.easy_dsl.api.field.FieldConfigurationStrategy
import can.i.has.easy_dsl.api.field.WithMethod
import can.i.has.easy_dsl.impl.ConfigurationSetupResolver

class Conf {
    private Object $traitThis;
    private ConfigurationSetupResolver $resolver = ConfigurationSetupResolver.instance

    Conf(Object $traitThis) {
        this.$traitThis = $traitThis
    }

    def methodMissing(String name, args){
        println "methodMissing"
        WithMethod annotation = getFieldWithMethodAnnotation(name)
        if (annotation){
            doWithMethod(annotation, name, *$resolver.preprocessArgs(args))
        } else if (isDelegatedMethod(name)){
            $traitThis.metaClass.invokeMethod($traitThis, name, args)
        } else {
            throw new MissingMethodException(name, $traitThis.class, args)
        }
    }

    def propertyMissing(String name){
        if (isFieldWithGetter(name)){
            return $traitThis.metaClass.getProperty($traitThis, name)
        }
        throw new MissingPropertyException(name, $traitThis.class)
    }
    def propertyMissing(String name, val){
        if (isFieldWithSetter(name)){
            return $traitThis.metaClass.setProperty($traitThis, name, val)
        }
        throw new MissingPropertyException(name, $traitThis.class)
    }

    private boolean isFieldWithGetter(String name){
        def f = $traitThis.class.declaredFields.find {
            it.name == name
        }
//        return f ? $resolver.getGetter(f)!=null : $resolver.getGetter($traitThis.class) !=null
        return (f!=null && $resolver.getGetter(f)!=null) || $resolver.getGetter($traitThis.class) !=null
    }

    private boolean isFieldWithSetter(String name){
        def f = $traitThis.class.declaredFields.find {
            it.name == name
        }
        return (f!=null && $resolver.getSetter(f)!=null) || $resolver.getSetter($traitThis.class) !=null
    }

    private WithMethod getFieldWithMethodAnnotation(String name){
        def f = $traitThis.class.declaredFields.find {
            it.name == name
        }
        return f ? $resolver.getWithMethod(f) : $resolver.getWithMethod($traitThis.class)
    }

    private boolean isDelegatedMethod(String name){
        def m = $traitThis.class.declaredMethods.find {
            it.name == name
        }
        return m ? $resolver.getDelegate(m)!=null : $resolver.getDelegate($traitThis.class)!=null
    }

    private void doWithMethod(WithMethod ann, String name, Map kwargs, Object val, Closure closure){
        if (ann.withSetter()) {
            if (!ann.allowOverwrite() && $traitThis.metaClass.getProperty($traitThis, name))
                assert !val
            if (val)
                $traitThis.metaClass.setProperty($traitThis, name, val)
            else if (!$traitThis.metaClass.getProperty($traitThis, name)) {
//                            (ann.constructor() as Class).constructors.each {
//                                println it.parameterTypes
//                            }
//                        assert Closure.isAssignableFrom(ann.constructor() as Class)
                def thisObj = [:]
                Closure constr = ann.constructor().newInstance(this, thisObj)
                $traitThis.metaClass.setProperty(
                    $traitThis,
                    name,
                    constr.call(
                        $resolver.propertyType($traitThis.class, name)
                    )
                )
            }
        }
        def newVal = $traitThis.metaClass.getProperty($traitThis, name)

        if (!ann.withMapping())
            assert !kwargs
        if (kwargs)
            kwargs.each { k, v ->
                newVal.metaClass.setProperty(newVal, k, v)
            }

        switch (ann.value()) {
            case FieldConfigurationStrategy.NONE: return;
            case FieldConfigurationStrategy.BUILD:
//                        DelegationUtils.callWithDelegate(newVal, closure);
                newVal.with closure
                return;
            case FieldConfigurationStrategy.CONFIGURE:
                assert newVal instanceof ConfigurableTrait
//                        DelegationUtils.callWithDelegate(newVal.configurator, closure);
                newVal.configure closure
                return;
        }
    }

}
