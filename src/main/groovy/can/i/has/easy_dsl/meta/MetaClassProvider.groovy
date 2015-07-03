package can.i.has.easy_dsl.meta

import can.i.has.easy_dsl.Configurator
import can.i.has.easy_dsl.Utils
import can.i.has.easy_dsl.api.field.FieldConfigurationStrategy
import can.i.has.easy_dsl.api.field.InternalField
import can.i.has.easy_dsl.api.field.WithMethod
import can.i.has.easy_dsl.api.method.InternalMethod
import can.i.has.easy_dsl.api.modifiers.Collect
import can.i.has.easy_dsl.api.modifiers.CollectScope
import can.i.has.easy_dsl.api.modifiers.Inherited
import can.i.has.easy_dsl.impl.ConfigurationResolver
import can.i.has.easy_dsl.impl.DelegationUtils
import can.i.has.easy_dsl.impl.MOPUtils

import groovy.transform.Memoized

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * fixme: update this
 * When we use any kind of WithMethod, possibly with Collect or CollectScope, there are some parts of DSL
 * that we call <b>method setters</b>, <b>appenders</b>, <b>obtainers</b> and <b>scopes</b>.
 * <br><br>
 *
 * If we have some field:
 * <pre>@MethodSetter int x</pre>
 * then we can use DSL as such:
 * <pre>   ...
 * x(5)
 * ...</pre>
 * closure that is executed for x(5) and returns 5 (as new value  is called <b>obtainer</b> - its variations
 * with other annotations, like closure called for someObject(1, a:2) { c=3 } are obtainers too.
 * <br><br>
 *
 * There is also closure that wraps obtainer for wanted action, for exaple for @MethodSetter it will be <b>method
 * setter</b>, that works like this:
 * <pre>methodSetter = { args ->
 *     ...
 *     configuredObject.x = obtainerClosure.call(args)
 * }</pre>
 * <br><br>
 *
 * There is another wrapper, appender, used with Collect and CollectScope: <b>appender</b>,
 * which works like this:
 * <pre>appender = { args ->
 *     ...
 *     target = configuredObject."${currentTarget}"
 *     target,add(obtainerClosure.call(args))
 * }</pre>
 * Target resolving is described in following paragraphs.
 *
 * <br><br>
 *
 * Last wrapper-like element is scope. It defines local target for appenders.
 * For example, with definition:
 * <pre>@MethodSetter
 * @CollectScope(elementName = "number", type = Integer)
 * List<Integer> intList = []</pre>
 * we can use DSL:
 * <pre>intList {
 *     number 1
 *     number 2
 * }</pre>
 * This will be handled as such:
 * <pre>number(...)</pre> will trigger proper appender, which will take argument and append it to current target.
 * <pre>intList(Closure)</pre> will start with saving current target for future usage. Then it will set it to "intList".
 * Next, it will call its argument, delegating to Configurator which it uses itself. Then, it will restore previous
 * target and return intList field.
 *
 * <br><br>
 * When using standard @Collect appender, target will be resolved by wrapping appender into simple closure that stores
 * current target, sets it to current obtainer name, calls appender, restores target and returns appenders result.
 *
 * <br><br>
 * Configurator has 2 internal, private variables: <i>Object traitThis</i>, and <i>String currentScope</i>.
 * First one is used for inspecting and delegating to configured object.
 * Last variable stores current scope name.
 * It is modified by appenders, scopes, etc, and is used to get target instance.
 * Initially it is set to null.
 * If we'd keep target list instead of field name, if field value would change between appender calls, second call would
 * append to wrong list.
 *
 * <br><br>
 * Configurator has 2 additional methods: <i>List getTarget()</i> and <i>Map<String, String> getTargetPerScope()</i>.
 * <i>List getTarget()</i> deduces target instance and returns it. Remember that it returns
 * field value, not reference to field!
 * It uses <i>Map<String, String> getTargetPerScope()</i> to figure it out.
 * That method calls ScopeRegistry, that contains mapping between configured class and another mapping.
 * That inner mapping connects scope name to field name.
 * At this moment it is identity mapping, but in future it may be useful to create custom scopes.
 *
 * //todo: @Obtainer, @CustomScope
 */
@Singleton
class MetaClassProvider {
    ConfigurationResolver resolver = ConfigurationResolver.instance

    @Memoized
    MetaClass enhanceMetaClass(Configurator configurator){
        MetaClass metaClass = configurator.metaClass
        enhanceWithFields(metaClass, configurator.traitThis.class)
        enhanceWithMethods(metaClass, configurator.traitThis.class)
//        out.initialize()
        return metaClass
    }

    boolean isStandardMethod(Class clazz, Method method){
        def classMethod = clazz.declaredMethods.find {
            it.name == method.name && it.parameterTypes == method.parameterTypes && it.returnType == method.returnType
        }
        def superClassMethod = clazz.superclass.declaredMethods.find {
            it.name == method.name && it.parameterTypes == method.parameterTypes && it.returnType == method.returnType
        }
        return classMethod!=null && (method.isAnnotationPresent(Inherited) || superClassMethod==null)
    }

    static final List<String> ignoredFields = [
        "metaClass",
        "class",
        "getAt",
        "putAt",
        "getProperty",
        "setProperty",
        "getAttribute",
        "setAttribute",
        "invokeMethod",
        "propertyMissing",
        "methodMissing"
    ]

    boolean isStandardField(Class clazz, Field field){
        if (ignoredFields.contains(field.name) || field.name.contains('$') || field.name.startsWith("_"))
            return false
        def classField = clazz.declaredFields.find {
            it.name == field.name && it.type == field.type
        }
        def superClassField = clazz.superclass.declaredFields.find {
            it.name == field.name && it.type == field.type
        }
        return classField!=null && (field.isAnnotationPresent(Inherited) || superClassField==null)
    }

    void enhanceWithMethods(MetaClass metaClass, Class clazz){
        for (Method method: clazz.declaredMethods){
            if (isStandardMethod(clazz, method)) {
                def methodAnnotations = resolver.getMethodAnnotations(method)

//            dont ignore this method
                if (!methodAnnotations?.any { it instanceof InternalMethod }) {
                    enhanceWithDelegate(metaClass, clazz, method)
                }
            }
        }
    }

    void enhanceWithDelegate(MetaClass metaClass, Class clazz, Method method){
        if (resolver.getDelegate(method) || resolver.getDelegate(clazz)) {
            metaClass."${method.name}" << getDelegateClosure(method.name)
        }
    }

    Closure getDelegateClosure(String name){
        return { Object[] args ->
            traitThis.metaClass.invokeMethod(traitThis, name, args)
        }
    }

    void enhanceWithFields(MetaClass metaClass, Class clazz){
        for (Field field: clazz.declaredFields) {
            if (isStandardField(clazz, field)) {
                def fieldAnnotations = resolver.getFieldAnnotations(field)

                if (!fieldAnnotations?.any { it instanceof InternalField }) {
                    enhanceWithGetter(metaClass, clazz, field)
                    enhanceWithSetter(metaClass, clazz, field)
                    enhanceWithWithMethod(metaClass, clazz, field)
                }
            }
        }
    }

    void enhanceWithGetter(MetaClass metaClass, Class clazz, Field field){
        def f = resolver.getGetter(field)
        def c = resolver.getGetter(clazz)
        if (f || c) {
            metaClass."get${field.name.capitalize()}" << getGetterClosure(field.name)
        }
    }

    void enhanceWithSetter(MetaClass metaClass, Class clazz, Field field){
        if (resolver.getSetter(field) || resolver.getSetter(clazz)) {
            metaClass."set${field.name.capitalize()}" << getSetterClosure(field.name)
        }
    }

    void enhanceWithWithMethod(MetaClass metaClass, Class clazz, Field field){
        WithMethod withMethod = resolver.getWithMethod(field) ?: resolver.getWithMethod(clazz)
        if (withMethod) {
            def collectScope = field.annotations.find { it instanceof CollectScope}
            def collect = field.annotations.find { it instanceof Collect }
            assert collectScope == null || collect == null //todo: this can be solved
            def obtainerName = (collectScope ?: collect)?.elementName() ?: field.name
            def scopeName = (collectScope ?: collect)==null ? null : field.name
            def obtainedType = (collectScope ?: collect)?.type() ?: field.type

            Closure obtainer = (
                field.type == Closure ?
                    getClosureObtainer(field.name) :
                    getWithMethodObtainer(field.name, withMethod, obtainedType)
            )
            if (collectScope != null) {
                metaClass."${scopeName}" << { Closure c ->
                    try {
                        ((Configurator)delegate).scopeStack.push(scopeName)
                        DelegationUtils.callWithDelegate(((Configurator)delegate), c)
                        return ((Configurator)delegate).getTarget()
                    } finally {
                        ((Configurator)delegate).scopeStack.pop()
                    }
                }
                ScopeRegistry.getScopeMapping(clazz)[scopeName] = field.name
            }

            def newMethod = (collectScope ?: collect) ?
                        getAppender(clazz, field, collect ? scopeName : null, obtainer) :
                        getMethodSetter(field, obtainer)
            metaClass."${obtainerName}" << newMethod
        }

    }

    Closure getGetterClosure(String name){
        return { ->
            return traitThis.metaClass.invokeMethod(traitThis, "get${name.capitalize()}", new Object[0])
        }
    }

    Closure getSetterClosure(String name){
        return { val ->
            return traitThis.metaClass.invokeMethod(traitThis, "set${name.capitalize()}", val)
        }
    }

    Closure getClosureObtainer(String name){
        return { Object... args ->
            assert args.size()==1 && args[0] instanceof Closure
            return args[0]
        }
    }

    def preprocessArgs(Object... args){
        assert args && args.size()<4
        if (args.size()==1){
            switch (args[0]) {
                case Closure: return [[:], null, args[0]]
                case Map: return [args[0], null, {}]
                default: return [[:], args[0], {} ]
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

    Closure getWithMethodObtainer(String name, WithMethod withMethod, Class type) {
        return { Object[] args ->
            List preprocessed = preprocessArgs(args)
            Map kwargs = preprocessed[0]
            Object val = preprocessed[1]
            Closure closure = preprocessed[2]
            def resultVal = MOPUtils.hasProperty(name) ? MOPUtils.getProperty(((Configurator)delegate).traitThis, name): null

            if (withMethod.withSetter()) {
                if (!withMethod.allowOverwrite() && resultVal)
                    assert !val
                else if (!resultVal && !val) {
                    def thisObj = [:]
                    Closure constr = withMethod.constructor().newInstance(this, thisObj)
                    resultVal = constr.call(
                        type
                    )
                } else  if (val)
                    resultVal = val
            }

            if (!withMethod.withMapping())
                assert !kwargs
            if (kwargs)
                kwargs.each { k, v ->
                    MOPUtils.setProperty(resultVal, k, v)
                }

            switch (withMethod.value()) {
                case FieldConfigurationStrategy.NONE: break
                case FieldConfigurationStrategy.BUILD:
                    resultVal.with closure
                    break
                case FieldConfigurationStrategy.CONFIGURE:
                    Utils.configure(resultVal, closure)
                    break
            }

            return resultVal
        }
    }


    Closure getAppender(final Class clazz, Field field, final String staticScope, Closure obtainer){
        if (staticScope)
            ScopeRegistry.getScopeMapping(clazz)[staticScope] = field.name
        return { Object... args ->
            try {
                if (staticScope)
                    ((Configurator)delegate).scopeStack.push(staticScope)
                def result = obtainer.call(args)
                ((Configurator)delegate).target.add(result)
                return result
            } finally {
                if (staticScope)
                    ((Configurator)delegate).scopeStack.pop()
            }

        }
    }

    Closure getMethodSetter(Field field, Closure obtainer){
        return { Object... args ->
            def result = obtainer.call(args)
            ((Configurator)delegate).traitThis.metaClass.setProperty(((Configurator)delegate).traitThis, field.name, result)
            return result
        }
    }
}
