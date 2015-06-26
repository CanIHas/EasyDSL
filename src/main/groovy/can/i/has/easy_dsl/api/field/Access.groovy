package can.i.has.easy_dsl.api.field

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.FIELD])
@interface Access {
    boolean getter() default true;
    boolean setter() default true;
}
