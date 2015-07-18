package can.i.has.gast

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.ANNOTATION_TYPE])
@GroovyASTTransformationClass(['can.i.has.gast.transform.CreateAnnotationTemplate'])
@interface Templatable {
    /**
     * Package
     */
    String value() default ""
    String name() default ""
    String subpackage() default ""
}