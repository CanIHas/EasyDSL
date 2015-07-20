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
        gClass.addMethod """String showYourPossibilities(String x){
//    return "DUPA"
    return ('${gAnnotation.members.str}'+x)*i
}"""
//    return ('${gAnnotation.members.str}'+x)*${gAnnotation.members.i}
//        log.info gAnnotation
//        log.info gAnnotation.members.closure.call()
    }
}
