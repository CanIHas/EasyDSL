package can.i.has.gast

import can.i.has.gast.model.ClosureMember
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(['can.i.has.gast.ExampleTransform'])
@interface ExampleAnn {
    String str() default "ABC"
    int i()
    @ClosureMember
    Class closure()
}
