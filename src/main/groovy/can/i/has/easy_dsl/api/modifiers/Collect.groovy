package can.i.has.easy_dsl.api.modifiers

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface Collect {
    String elementName()
    Class type()
}