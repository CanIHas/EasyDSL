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
import can.i.has.easy_dsl.api.method.Delegate
import can.i.has.easy_dsl.api.method.InternalMethod

import java.lang.annotation.Annotation
import java.lang.reflect.AnnotatedElement

@Singleton
class ConfigurationResolver {
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
        return element.annotations.find { it instanceof InternalMethod }
    }

    Annotation getDelegate(AnnotatedElement element){
        assert element.annotations.every {
            it instanceof Delegate ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        return element.annotations.find { it instanceof Delegate }
    }

    Annotation getInternalField(AnnotatedElement element){
        assert element.annotations.every {
            it instanceof InternalField ||
                !methodAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        return element.annotations.find { it instanceof InternalField }
    }

    Annotation getGetter(AnnotatedElement element){
        def found = element.annotations.findAll {
            it instanceof Getter ||
                (it instanceof Access && it.getter())
        }
        assert found.size() < 2
        assert element.annotations.every {
            found.contains(it) ||
                [WithMethod, MethodSetter, Configure, Build].any { a ->
                    a.isInstance(it)
                } ||
                !fieldAnnotations.any { a ->
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
                [WithMethod, MethodSetter, Configure, Build].any { a ->
                    a.isInstance(it)
                } ||
                !fieldAnnotations.any { a ->
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
                [Access, Getter, Setter].any { a ->
                    a.isInstance(it)
                } ||
                !fieldAnnotations.any { a ->
                    a.isInstance(it)
                }
        }
        def result = found ? found[0] : null
        if (result && !(result instanceof WithMethod && result.constructor()!=null))
            return new WithMethod(){
                @Override
                FieldConfigurationStrategy value() {
                    if (result instanceof MethodSetter)
                        return FieldConfigurationStrategy.NONE
                    else if (result instanceof Configure)
                        return FieldConfigurationStrategy.CONFIGURE
                    else if (result instanceof Build)
                        return FieldConfigurationStrategy.BUILD
                    result.value()
                }

                @Override
                boolean withSetter() {
                    result instanceof MethodSetter || result.withSetter()
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

    Class propertyType(Class objClass, String propName){
        objClass.declaredFields.find { it.name == propName }?.type ?:
            objClass.declaredMethods.find { it.name == "set${propName.capitalize()}" }?.returnType
    }
}

