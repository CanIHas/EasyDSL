package can.i.has.easy_dsl.api.field

import can.i.has.easy_dsl.impl.utils.DefaultConstructor

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.FIELD])
@interface MethodSetter {
    boolean allowOverwrite() default true;

    boolean withMapping() default true;

    Class constructor() default DefaultConstructor
}
