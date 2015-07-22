package can.i.has.gast

import can.i.has.gast.utils.CompilationLogger
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.GroovyASTTransformation


@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class ExampleTransform extends GAstTransformation{
    static CompilationLogger log = new CompilationLogger(ExampleTransform)

    @Override
    void transform() {
        gClass.addField("int i = ${gAnnotation.members.i}")
        def m = gClass.addMethod """String showYourPossibilities(String x){
    return ('${gAnnotation.members.str}'+x)*i
}"""
        def m2 = gClass.addMethod("""void testBuiltinAssertions(){
    assertEquals("Expected value was 42, got ${gAnnotation.members.closure.call()}", 42, ${gAnnotation.members.closure.call()})
}""")
    }
}
