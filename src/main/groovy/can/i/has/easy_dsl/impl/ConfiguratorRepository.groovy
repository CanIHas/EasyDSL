package can.i.has.easy_dsl.impl

import can.i.has.easy_dsl.api.field.InternalField
import can.i.has.easy_dsl.api.field.WithMethod
import can.i.has.easy_dsl.api.method.InternalMethod
import can.i.has.easy_dsl.api.modifiers.Collect
import can.i.has.easy_dsl.api.modifiers.CollectScope
import can.i.has.easy_dsl.api.modifiers.Inherited
import can.i.has.easy_dsl.impl.utils.ReflectionUtils
import can.i.has.easy_dsl.model.DslMethod
import can.i.has.easy_dsl.model.field.*
import can.i.has.easy_dsl.model.method.DelegateMethod

import java.lang.reflect.Field
import java.lang.reflect.Method

@Singleton
class ConfiguratorRepository {
    protected ConfigurationResolver resolver = ConfigurationResolver.instance

    protected Map<Class, List> dslForClass = [:].withDefault {
        loadClassMethods(it)
    }

    //todo: ensure no external modification
    Map<Class, Map<String, String>> scopeRegistry = [:].withDefault { [:] }

    Map<Class, List> getDslForClass() {
        this.@dslForClass.asImmutable()
    }


    protected List<DslMethod> loadClassMethods(Class clazz) {
        loadFromMethods(clazz) + loadFromFields(clazz)
    }

    boolean methodEquals(Method m1, Method m2) {
        m1.name == m2.name &&
            m1.parameterTypes == m2.parameterTypes &&
            m1.returnType == m2.returnType
    }

    boolean fieldEquals(Field f1, Field f2) {
        f1.name == f2.name &&
            f1.type == f2.type
    }


    protected boolean isStandardMethod(Class clazz, Method method) {
        def classMethod = clazz.declaredMethods.find { methodEquals(method, it) }
        def superClassMethod = clazz.superclass.declaredMethods.find { methodEquals(method, it) }
        return classMethod != null && (method.isAnnotationPresent(Inherited) || superClassMethod == null)
    }

    protected boolean isStandardField(Class clazz, Field field) {
        if (ReflectionUtils.isSpecial(field.name) || field.name.contains('$') || field.name.startsWith("_"))
            return false
        def classField = clazz.declaredFields.find { fieldEquals(field, it) }
        def superClassField = clazz.superclass.declaredFields.find { fieldEquals(field, it) }
        return classField != null && (field.isAnnotationPresent(Inherited) || superClassField == null)
    }

    List<DslMethod> loadFromMethods(Class clazz) {
        def out = []
        for (Method method : clazz.declaredMethods) {
            if (isStandardMethod(clazz, method)) {
                def methodAnnotations = resolver.getMethodAnnotations(method)

                //dont ignore this method
                if (!methodAnnotations?.any { it instanceof InternalMethod }) {
                    out.add tryGettingDelegate(clazz, method)
                }
            }
        }
        out.findAll()
    }

    DslMethod tryGettingDelegate(Class clazz, Method method) {
        if (resolver.getDelegate(method) || resolver.getDelegate(clazz)) {
            return new DelegateMethod(method.name)
        }
    }

    List<DslMethod> loadFromFields(Class clazz) {
        def out = []
        for (Field field : clazz.declaredFields) {
            if (isStandardField(clazz, field)) {
                def fieldAnnotations = resolver.getFieldAnnotations(field)
                //dont ignore this field
                if (!fieldAnnotations?.any { it instanceof InternalField }) {
                    out.add tryGettingGetter(clazz, field)
                    out.add tryGettingSetter(clazz, field)
                    out.addAll tryGettingWithMethod(clazz, field)
                }
            }
        }
        return out.findAll()
    }

    DslMethod tryGettingGetter(Class clazz, Field field) {
        if (resolver.getGetter(field) || resolver.getGetter(clazz)) {
            return new Getter(field.name)
        }
    }

    DslMethod tryGettingSetter(Class clazz, Field field) {
        if (resolver.getSetter(field) || resolver.getSetter(clazz)) {
            return new Setter(field.name, field.type)
        }
    }

    List<DslMethod> tryGettingWithMethod(Class clazz, Field field) {
        WithMethod withMethod = resolver.getWithMethod(field) ?: resolver.getWithMethod(clazz)
        def out = []
        if (withMethod) {
            def collectScope = field.annotations.find { it instanceof CollectScope }
            def collect = field.annotations.find { it instanceof Collect }
            assert collectScope == null || collect == null //todo: this can be solved
            def obtainerName = (collectScope ?: collect)?.elementName() ?: field.name
            def scopeName = (collectScope ?: collect) == null ? null : field.name
            def obtainedType = (collectScope ?: collect)?.type() ?: field.type

            Obtainer obtainer = (
                field.type == Closure ?
                    new ClosureObtainer(obtainerName) :
                    new RegularObtainer(obtainerName,
                        field.name,
                        obtainedType,
                        withMethod.value(),
                        withMethod.withSetter(),
                        withMethod.allowOverwrite(),
                        withMethod.withMapping(),
                        withMethod.constructor().newInstance([this] as Object[])
                    )
            )
            if (collectScope != null) {
                out.add new Scope(scopeName)
                out.add new Appender(obtainer)
                scopeRegistry[clazz][scopeName] = field.name
            } else if (collect != null) {
                scopeRegistry[clazz][scopeName] = field.name
                out.add new UnscopedAppender(obtainer, scopeName)
            } else {
                out.add new PropertySetter(obtainer)
            }
        }
        return out;

    }
}
