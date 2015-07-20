package can.i.has.gast

import can.i.has.gast.utils.CompilationLogger
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.GroovyASTTransformation


@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class ExampleTransform extends GAstTransformation{

    @Override
    void transform() {
        gClass.addField("int i = ${gAnnotation.members.i}")
        gClass.addMethod """String showYourPossibilities(String x){
    return ('${gAnnotation.members.str}'+x)*i
}"""
    }
}
