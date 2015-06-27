package can.i.has.easy_dsl.impl

import can.i.has.easy_dsl.ConfigurableTrait
import can.i.has.easy_dsl.api.field.Access
import can.i.has.easy_dsl.api.field.Build
import can.i.has.easy_dsl.api.field.Configure
import can.i.has.easy_dsl.api.field.FieldConfigurationStrategy
import can.i.has.easy_dsl.api.field.Getter
import can.i.has.easy_dsl.api.field.InternalField
import can.i.has.easy_dsl.api.field.MethodSetter
import can.i.has.easy_dsl.api.field.Setter
import can.i.has.easy_dsl.api.field.WithMethod
import can.i.has.easy_dsl.api.method.InternalMethod

import groovy.transform.Memoized

import java.lang.annotation.Annotation
import java.lang.reflect.AnnotatedElement

@Singleton
class ConfigurationSetupResolver {
    static List<Class<? extends Annotation>> methodAnnotations = [
        InternalMethod,
        Delegate
    ]

    static List<Class<? extends Annotation>> fieldAnnotations = [
        InternalField,

        Access,
        Getter, //Access(getter=true, setter=false)
        Setter, //Access(getter=false, setter=true)

        WithMethod,
        MethodSetter, //WithMethod(FieldConfigurationStrategy.NONE)
        Configure, //WithMethod(FieldConfigurationStrategy.CONFIGURE)
        Build //WithMethod(FieldConfigurationStrategy.BUILD)
    ]

    Annotation getInternalMethod(AnnotatedElement element){
        assert element.annotations.every {
            it instanceof InternalMethod ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        element.getAnnotation(InternalMethod)
    }

    Annotation getDelegate(AnnotatedElement element){
        assert element.annotations.every {
            it instanceof Delegate ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        element.getAnnotation(Delegate)
    }

    Annotation getInternalField(AnnotatedElement element){
        assert element.annotations.every {
            it instanceof InternalField ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        element.getAnnotation(InternalField)
    }

    Annotation getGetter(AnnotatedElement element){
        def found = element.annotations.findAll {
            it instanceof Getter ||
                (it instanceof Access && it.getter())
        }
        assert found.size() < 2
        assert element.annotations.every {
            found.contains(it) ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        def result = found ? found[0] : null
        return result instanceof Access ?
            new Getter() { @Override Class<? extends Annotation> annotationType() { Getter } } :
                result
    }

    Annotation getSetter(AnnotatedElement element){
        def found = element.annotations.findAll {
            it instanceof Setter ||
                (it instanceof Access && it.setter())
        }
        assert found.size() < 2
        assert element.annotations.every {
            found.contains(it) ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        def result = found ? found[0] : null
        return result instanceof Access ?
            new Setter() { @Override Class<? extends Annotation> annotationType() { Setter } } :
            result
    }

    Annotation getWithMethod(AnnotatedElement element){
        def found = element.annotations.findAll {
            [WithMethod, MethodSetter, Configure, Build].any { a ->
                a.isInstance(it)
            }
        }
        assert found.size() < 2
        assert element.annotations.every {
            found.contains(it) ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        def result = found ? found[0] : null
        if (result && !(result instanceof WithMethod))
            return new WithMethod(){
                @Override
                FieldConfigurationStrategy value() {
                    if (result instanceof MethodSetter)
                        return FieldConfigurationStrategy.NONE
                    else if (result instanceof Configure)
                        return FieldConfigurationStrategy.CONFIGURE
                    else if (result instanceof Build)
                        return FieldConfigurationStrategy.BUILD
                    assert false //todo: should not happen!
//                    [
//                        (MethodSetter): FieldConfigurationStrategy.NONE,
//                        (Configure):    FieldConfigurationStrategy.CONFIGURE,
//                        (Build):        FieldConfigurationStrategy.BUILD
//                    ][result.class]
                }

                @Override
                boolean withSetter() {
                    result.withSetter()
                }

                @Override
                boolean allowOverwrite() {
                    result.allowOverwrite()
                }

                @Override
                boolean withMapping() {
                    result.withMapping()
                }

                @Override
                Class constructor() {
                    result.constructor()
                }

                @Override
                Class<? extends Annotation> annotationType() {
                    WithMethod
                }
            }
        return result
    }

    @Memoized
    Closure resolveGetter(Class clazz, String name){
        def f = clazz.declaredFields.find {
            it.name == name &&
                getGetter(it) || // explicit field annotation
                ( //class annotation
                    !fieldAnnotations.any { a -> it.isAnnotationPresent(a) } &&
                        getGetter(clazz)
                )
        }
        return f ? { that -> that.metaClass.getProperty(that, name) } : null
    }


    @Memoized
    Closure resolveSetter(Class clazz, String name){
        def f = clazz.declaredFields.find {
            it.name == name &&
                getSetter(it) || // explicit field annotation
                ( //class annotation
                    !fieldAnnotations.any { a -> it.isAnnotationPresent(a) } &&
                        getSetter(clazz)
                )
        }
        return f ? { that, val -> that.metaClass.setProperty(that, name, val) } : null
    }

    def preprocessArgs(Object... args){
        assert args && args.size()<4
        if (args.size()==1){
            assert args[0] instanceof Closure
            return [[:], null, args[0]]
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

    Class propertyType(Class objClass, String propName){
        objClass.declaredFields.find { it.name == propName }?.type ?:
            objClass.declaredMethods.find { it.name == "set${propName.capitalize()}" }?.returnType
    }

    Closure resolveMethod(Class clazz, String name){
        //todo: just implement this
        def f = clazz.declaredFields.find {
            it.name == name &&
                getWithMethod(it) || // explicit field annotation
                ( //class annotation
                    !fieldAnnotations.any { a -> it.isAnnotationPresent(a) } &&
                        getWithMethod(clazz)
                )
        }
        if (f) {
            WithMethod ann = getWithMethod(f) ?: getWithMethod(clazz)
            return { that, Object... args ->
                def preprocessed = doArgs(args)
                def kwargs = preprocessed.kwargs
                def val = preprocessed.val
                def closure = preprocessed.closure
                if (ann.withSetter()) {
                    if (!ann.allowOverwrite() && that.metaClass.getProperty(that, name))
                        assert !val
                    if (val)
                        that.metaClass.setProperty(that, name, val)
                    else if (!that.metaClass.getProperty(that, name)) {
//                            (ann.constructor() as Class).constructors.each {
//                                println it.parameterTypes
//                            }
//                        assert Closure.isAssignableFrom(ann.constructor() as Class)
                        def thisObj = [:]
                        Closure constr = ann.constructor().newInstance(this, thisObj)
                        that.metaClass.setProperty(
                            that,
                            name,
                            constr.call(
                                propertyType(that.class, name)
                            )
                        )
                    }
                }
                def newVal = that.metaClass.getProperty(that, name)

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
        } else {
            //todo: method delegation
        }

    }

}

