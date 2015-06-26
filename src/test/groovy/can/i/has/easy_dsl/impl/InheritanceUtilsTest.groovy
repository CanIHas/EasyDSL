package can.i.has.easy_dsl.impl

import can.i.has.easy_dsl.impl.old.InheritanceUtils


class InheritanceUtilsTest extends GroovyTestCase {
    static class A{
        int x
        def foo(){}
        def bar(){}
    }

    static class B extends A{
        def foo(int a){}
    }

    static class C {
        int x
        def foo(){}
    }

    void testIsInherited() {
        assert InheritanceUtils.isInherited(B, B.metaClass.properties.find { it.name == "x" })
        assert !InheritanceUtils.isInherited(A, A.metaClass.properties.find { it.name == "x" })
        assert !InheritanceUtils.isInherited(C, C.metaClass.properties.find { it.name == "x" })

        assert !InheritanceUtils.isInherited(B, B.metaClass.methods.find { it.name == "foo" })
        assert InheritanceUtils.isInherited(B, B.metaClass.methods.find { it.name == "bar" })
        assert !InheritanceUtils.isInherited(A, A.metaClass.methods.find { it.name == "foo" })
        assert !InheritanceUtils.isInherited(C, C.metaClass.methods.find { it.name == "foo" })
    }
}
