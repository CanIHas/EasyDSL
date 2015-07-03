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

    List<Annotation> getMethodAnnotations(AnnotatedElement element){
        ConfigurationNormalizer.normalize(element.annotations.findAll {
            methodAnnotations.any { a -> a.isInstance(it) }
        })
    }

    List<Annotation> getFieldAnnotations(AnnotatedElement element){
        ConfigurationNormalizer.normalize(element.annotations.findAll {
            fieldAnnotations.any { a -> a.isInstance(it) }
        })
    }

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
//        assert element.annotations.every {
//            found.contains(it) ||
//                [WithMethod, MethodSetter, Configure, Build].any { a ->
//                    a.isInstance(it)
//                } ||
//                !fieldAnnotations.any { a ->
//                    a.isInstance(it)
//                }
//        }
        return found ? ConfigurationNormalizer.normalizeGetter(found[0]) : null
    }

    Annotation getSetter(AnnotatedElement element){
        def found = element.annotations.findAll {
            it instanceof Setter ||
                (it instanceof Access && it.setter())
        }
        assert found.size() < 2
//        assert element.annotations.every {
//            found.contains(it) ||
//                [WithMethod, MethodSetter, Configure, Build].any { a ->
//                    a.isInstance(it)
//                } ||
//                !fieldAnnotations.any { a ->
//                    a.isInstance(it)
//                }
//        }
        return found ? ConfigurationNormalizer.normalizeSetter(found[0]) : null
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
        return found ? ConfigurationNormalizer.normalizeWithMethod(found[0]) : null
        }

    Class propertyType(Class objClass, String propName){
        objClass.declaredFields.find { it.name == propName }?.type ?:
            objClass.declaredMethods.find { it.name == "set${propName.capitalize()}" }?.returnType
    }
}

