package can.i.has.easy_dsl.impl

import can.i.has.easy_dsl.Configurator
import can.i.has.easy_dsl.api.field.*

import java.lang.annotation.Annotation


class ConfigurationNormalizer {

    static Annotation normalizeSetter(Setter setter){
        return setter
    }

    static Annotation normalizeSetter(Access access){
        access.setter() ? new Setter() {
            @Override
            Class<? extends Annotation> annotationType() {
                Setter
            }
        } : null
    }

    static Annotation normalizeGetter(Getter getter){
        return getter
    }

    static Annotation normalizeGetter(Access access){
        access.getter() ? new Getter() {
            @Override
            Class<? extends Annotation> annotationType() {
                Getter
            }
        } : null
    }

    protected static Annotation normalizeWithMethod(Annotation original){
        new WithMethod() {
            @Override
            FieldConfigurationStrategy value() {
                if (original instanceof MethodSetter)
                    return FieldConfigurationStrategy.NONE
                else if (original instanceof Configure)
                    return FieldConfigurationStrategy.CONFIGURE
                else if (original instanceof Build)
                    return FieldConfigurationStrategy.BUILD
                original.value()
            }

            @Override
            boolean withSetter() {
                return original instanceof MethodSetter || original.withSetter()
            }

            @Override
            boolean allowOverwrite() {
                return original.allowOverwrite()
            }

            @Override
            boolean withMapping() {
                return original.withMapping()
            }

            @Override
            Class constructor() {
                return original.constructor() ?: Configurator.defaultConstructor.class
            }

            @Override
            Class<? extends Annotation> annotationType() {
                return WithMethod
            }
        }
    }

    static List<Annotation> normalize(List<Annotation> annotations){
        def out = []
        annotations.each {
            if ([MethodSetter, Configure, Build].any { a -> a.isInstance(it)})
                out.add normalizeWithMethod(it)
            else if (it instanceof Access){
                out.add normalizeGetter(it)
                out.add normalizeSetter(it)
            } else
                out.add it
        }
        return out
    }
}
