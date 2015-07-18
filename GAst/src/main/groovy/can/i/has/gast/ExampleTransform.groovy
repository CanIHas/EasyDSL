package can.i.has.gast

import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.GroovyASTTransformation

import groovy.util.logging.Slf4j

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
@Slf4j
class ExampleTransform extends GAstTransformation{

//    @Override
//    boolean handleClasses() {
//        false
//    }

    @Override
    void transform() {
        println gAnnotation
        println gAnnotation.members.closure.call()
    }
}
