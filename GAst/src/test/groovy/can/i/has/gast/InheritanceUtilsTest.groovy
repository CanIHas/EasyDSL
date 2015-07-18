package can.i.has.gast

import can.i.has.gast.utils.InheritanceUtils

import java.lang.reflect.Method


class InheritanceUtilsTest extends GroovyTestCase {
    static class A {
        int x
        String foo(){}
    }

    static class B extends A{
        String foo(int a){}
    }

    Method fooA
    Method fooBNoArgs
    Method fooBArgs
    Method equalsA

    void setUp(){
        fooA = A.methods.find {
            it.name == "foo"
        }
        fooBNoArgs = B.methods.find {
            it.name == "foo" && it.parameterTypes.size() == 0
        }
        fooBArgs = B.methods.find {
            it.name == "foo" && it.parameterTypes.size() == 1
        }
        equalsA = A.methods.find {
            it.name == "equals"
        }
    }

    void testDeclares() {
        assert InheritanceUtils.declares(A, fooA)
        assert !InheritanceUtils.declares(B, fooBNoArgs)
        assert InheritanceUtils.declares(B, fooBArgs)
        assert !InheritanceUtils.declares(A, equalsA)
    }
}
