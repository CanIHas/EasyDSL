package can.i.has.gast

/**
 * There is synthetic method "testBiuiltinAssertions", added by AST transformation.
 * @see can.i.has.gast.ExampleTransform
 */
@ExampleAnn(str="X", i=3, closure = {return 42})
class SimpleTest extends GroovyTestCase {

    void testMethodAndField() {
        assert showYourPossibilities("XYZ") == "XXYZXXYZXXYZ"
        i = 1
        assert showYourPossibilities("XYZ") == "XXYZ"
    }
}
