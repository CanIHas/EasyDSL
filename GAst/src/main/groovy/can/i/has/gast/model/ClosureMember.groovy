package can.i.has.gast.model

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * If some gAnnotation member should be Closure, it will be declared as Class.
 * If this gAnnotation is present on such member, GAnnotation will automatically
 * instantiate that Closure, with GAnnotation as closures owner.
 * If this is used on member of any other type - it is ignored.
 *
 * todo: provide default as a closure
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])
@interface ClosureMember {}