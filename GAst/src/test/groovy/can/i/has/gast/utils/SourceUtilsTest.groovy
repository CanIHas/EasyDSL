package can.i.has.gast.utils


class SourceUtilsTest extends GroovyTestCase {
    static String class1 = "class A1 extends x.y.B{}"
    static String class2 = "enum A2 {}"
    static String class3 = "interface A3 implements x.y.B, a.b.C   {}"

    static String method1 = "static void doMe (){}"
    static String method2 = "static void doMe( ){}"
    static String method3 = "foo(int x ) {}"
    static String method4 = "public abstract bar( Map m, java.util.List l, x) {}"

    void testTypeNames(){
        assert SourceUtils.getTypeName(class1) == "A1"
        assert SourceUtils.getTypeName(class2) == "A2"
        assert SourceUtils.getTypeName(class3) == "A3"
    }

    void testMethodNames(){
        assert SourceUtils.getMethodName(method1) == "doMe"
        assert SourceUtils.getMethodName(method2) == "doMe"
        assert SourceUtils.getMethodName(method3) == "foo"
        assert SourceUtils.getMethodName(method4) == "bar"
    }
}
