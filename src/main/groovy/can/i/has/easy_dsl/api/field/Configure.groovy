package can.i.has.easy_dsl.api.field

import can.i.has.easy_dsl.Configurator
import can.i.has.easy_dsl.impl.DefaultConstructor

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.FIELD])
@interface Configure {
    boolean withSetter() default true;
    boolean allowOverwrite() default true;
    boolean withMapping() default true;
    Class constructor() default DefaultConstructor
}