package can.i.has.gast.utils

import java.lang.annotation.Annotation
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method


class InheritanceUtils {
    static boolean declares(Class clazz, Method method){
        assert method
        return !(clazz.superclass?: topSuperClass(clazz)).methods.any {
            it.name == method.name &&
                it.returnType == method.returnType &&
                it.parameterTypes == method.parameterTypes
        }
    }

    static boolean declares(Class clazz, Constructor constructor){
        assert clazz && constructor
        return !(clazz.superclass?: topSuperClass(clazz)).constructors.any {
            it.parameterTypes == constructor.parameterTypes
        }
    }

    static Class topSuperClass(Class clazz){
        clazz.isAnnotation() ? Annotation : Object
    }
}
