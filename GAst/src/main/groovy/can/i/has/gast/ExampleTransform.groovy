package can.i.has.gast

import can.i.has.gast.utils.CompilationLogger
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.GroovyASTTransformation


@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class ExampleTransform extends GAstTransformation{
    static CompilationLogger log = new CompilationLogger(ExampleTransform)

    @Override
    void transform() {
        log.info gAnnotation
        log.info gAnnotation.members.closure.call()
    }
}
