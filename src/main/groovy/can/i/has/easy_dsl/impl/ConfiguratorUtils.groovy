package can.i.has.easy_dsl.impl

import can.i.has.easy_dsl.ConfigurableTrait
import can.i.has.easy_dsl.api.field.FieldConfigurationStrategy
import can.i.has.easy_dsl.api.field.WithMethod


class ConfiguratorUtils {
    private static ConfigurationResolver resolver = ConfigurationResolver.instance

    static boolean isFieldWithGetter(Object traitThis, String name){
        def f = traitThis.class.declaredFields.find {
            it.name == name
        }
//        return f ? resolver.getGetter(f)!=null : resolver.getGetter(traitThis.class) !=null
        return (f!=null && resolver.getGetter(f)!=null) || resolver.getGetter(traitThis.class) !=null
    }

    static boolean isFieldWithSetter(Object traitThis, String name){
        def f = traitThis.class.declaredFields.find {
            it.name == name
        }
        return (f!=null && resolver.getSetter(f)!=null) || resolver.getSetter(traitThis.class) !=null
    }

    static WithMethod getFieldWithMethodAnnotation(Object traitThis, String name){
        def f = traitThis.class.declaredFields.find {
            it.name == name
        }
        return f ? resolver.getWithMethod(f) : resolver.getWithMethod(traitThis.class)
    }

    static boolean isDelegatedMethod(Object traitThis, String name){
        def m = traitThis.class.declaredMethods.find {
            it.name == name
        }
        if (m){
            def out = resolver.getDelegate(m)
            return out != null
        }
        def out = resolver.getDelegate(traitThis.class)
        return out != null
//        return m ? resolver.getDelegate(m)!=null : resolver.getDelegate(traitThis.class)!=null
    }

    static void doWithMethod(Object traitThis, WithMethod ann, String name, Map kwargs, Object val, Closure closure){
        if (ann.withSetter()) {
            if (!ann.allowOverwrite() && MOPUtils.getProperty(traitThis, name))
                assert !val
            if (val)
                MOPUtils.setProperty(traitThis, name, val)
            else if (!MOPUtils.getProperty(traitThis, name)) {
//                            (ann.constructor() as Class).constructors.each {
//                                println it.parameterTypes
//                            }
//                        assert Closure.isAssignableFrom(ann.constructor() as Class)
                def thisObj = [:]
                Closure constr = ann.constructor().newInstance(this, thisObj)
                traitThis.metaClass.setProperty(
                    traitThis,
                    name,
                    constr.call(
                        resolver.propertyType(traitThis.class, name)
                    )
                )
            }
        }
        def newVal = MOPUtils.getProperty(traitThis, name)

        if (!ann.withMapping())
            assert !kwargs
        if (kwargs)
            kwargs.each { k, v ->
                MOPUtils.setProperty(newVal, k, v)
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

    static def preprocessArgs(Object... args){
        assert args && args.size()<4
        if (args.size()==1){
            switch (args[0]) {
                case Closure: return [[:], null, args[0]]
                case Map: return [args[0], null, {}]
                default: assert false
            }
        } else if (args.size()==2){
            assert args[1] instanceof Closure
            if (args[0] instanceof Map)
                return [args[0], null, args[1]]
            else
                return [[:], args[0], args[1]]
        } else {
            assert args[0] instanceof Map
            assert args[2] instanceof Closure
            return [args[0], args[1], args[2]]
        }
    }
}
