package can.i.has.easy_dsl.api.field

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.FIELD])
@interface WithMethod {
    FieldConfigurationStrategy value() default FieldConfigurationStrategy.CONFIGURE;
    boolean withSetter() default true;
    boolean allowOverwrite() default true;
    boolean withMapping() default true;
    Class constructor() default { Class clazz -> clazz.newInstance() }
}
