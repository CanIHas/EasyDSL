package can.i.has.easy_dsl.impl

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
        if (result && !result instanceof WithMethod)
            return new WithMethod(){
                @Override
                FieldConfigurationStrategy value() {
                    [
                        (MethodSetter): FieldConfigurationStrategy.NONE,
                        (Configure):    FieldConfigurationStrategy.CONFIGURE,
                        (Build):        FieldConfigurationStrategy.BUILD
                    ]
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

    Closure resolveMethod(Class clazz, String name, Object[] args){
        //todo: just implement this
//        def f = clazz.declaredFields.find {
//            it.name == name &&
//                getWithMethod(it) || // explicit field annotation
//                ( //class annotation
//                    !fieldAnnotations.any { a -> it.isAnnotationPresent(a) } &&
//                        getWithMethod(clazz)
//                )
//        }
//        if (f) {
//            def ann = getWithMethod(f) ?: getWithMethod(clazz)
//            return { Map kwargs=[:], Object val, Closure closure ->
//
//            }
//        } else {
//            //method delegation
//        }

    }

}
