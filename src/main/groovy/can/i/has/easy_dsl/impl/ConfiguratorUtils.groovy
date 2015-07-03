package can.i.has.easy_dsl.impl

import can.i.has.easy_dsl.ConfigurableTrait
import can.i.has.easy_dsl.Utils
import can.i.has.easy_dsl.api.modifiers.Collect
import can.i.has.easy_dsl.api.field.FieldConfigurationStrategy
import can.i.has.easy_dsl.api.field.WithMethod

import java.lang.reflect.AnnotatedElement


class ConfiguratorUtils {
    private static ConfigurationResolver resolver = ConfigurationResolver.instance

    static boolean isFieldWithGetter(Object traitThis, String name){
        def f = traitThis.class.declaredFields.find {
            it.name == name
        }
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
    }

    static def doWithMethod(Object traitThis, WithMethod ann, String collectTarget, String name, Map kwargs, Object val, Closure closure){
        def resultVal = MOPUtils.hasProperty(name) ? MOPUtils.getProperty(traitThis, name): null

        if (ann.withSetter()) {
            if (!ann.allowOverwrite() && resultVal)
                assert !val
            else if (!resultVal && !val) {
                def thisObj = [:]
                Closure constr = ann.constructor().newInstance(this, thisObj)
                resultVal = constr.call(
                        resolver.propertyType(traitThis.class, collectTarget ?: name)
                    )
            } else  if (val)
                resultVal = val
        }

        if (!ann.withMapping())
            assert !kwargs
        if (kwargs)
            kwargs.each { k, v ->
                MOPUtils.setProperty(resultVal, k, v)
            }

        switch (ann.value()) {
            case FieldConfigurationStrategy.NONE: break
            case FieldConfigurationStrategy.BUILD:
                resultVal.with closure
                break
            case FieldConfigurationStrategy.CONFIGURE:
                assert resultVal instanceof ConfigurableTrait
                Utils.configure(resultVal, closure)
                break
        }
        if (collectTarget!=null) {
            MOPUtils.getProperty(traitThis, collectTarget).add(resultVal)
            return
        }
        else {
            MOPUtils.setProperty(traitThis, name, resultVal)
            return resultVal
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

    static Collect getCollect(AnnotatedElement ae){
        def out = ae.annotations.findAll { it instanceof Collect }
        assert out.size()<2
        out ? out[0] : null
    }
}
