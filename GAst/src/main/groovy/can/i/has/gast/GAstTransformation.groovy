package can.i.has.gast

import can.i.has.gast.model.GAnnotation
import can.i.has.gast.model.GClass
import can.i.has.gast.model.GField
import can.i.has.gast.model.GMethod

import groovy.util.logging.Slf4j

import static can.i.has.gast.CompilationEnvironment.*
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation

import java.lang.annotation.Annotation

@Slf4j
abstract class GAstTransformation implements ASTTransformation{
    protected GAnnotation gAnnotation
    protected ASTNode annotated

    @Lazy protected GClass gClass = new GClass(annotated)
    @Lazy protected GMethod gMethod = new GMethod(annotated)
    @Lazy protected GField gField = new GField(annotated)

    GAnnotation getgAnnotation() {
        return gAnnotation
    }

    GClass getgClass() {
        return gClass
    }

    GMethod getgMethod() {
        return gMethod
    }

    GField getgField() {
        return gField
    }

    boolean handleGlobal(){
        return false
    }

    /**
     * If this returns false value (empty list or null), then transformation will happen only
     * if type of gAnnotation is one of returned.
     * No gAnnotation will be ignored by this, and handled by handleGlobal().
     * Default gAnnotation for global transforms will not be checked with result of this method.
     * @return
     */
    List<Annotation> handledTypes(){
        return null
    }

    /**
     * @return null if global transformations are disabled, else - default gAnnotation
     */
    GAnnotation annotationForGlobal(){
        null
    }

    boolean handleClasses(){
        true
    }

    boolean handleMethods(){
        true
    }

    boolean handleFields(){
        true
    }



    private void warnOfWrongType(Class nodeClass){
        log.warn "Cannot handle $nodeClass. Only classes, methods and fields are supported."
    }

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        try {
            println "source $source"
            withSourceUnit(source) {
                println "unit ${CompilationEnvironment.sourceUnit}"
                if (!nodes) return
                assert nodes.size() < 3
                if (nodes.size() == 1) {
                    if (handleGlobal())
                        gAnnotation = annotationForGlobal()
                    else {
                        log.warn("Cannot handle global transformations!")
                        return
                    }
                    annotated = nodes[0]
                } else {
                    if (handledTypes())
                        if (!handledTypes().contains(nodes[0])) {
                            log.warn("Cannot handle annotation of type ${gAnnotation.node.class}")
                            return
                        }
                    annotated = nodes[1]
                    switch (annotated) {
                        case ClassNode:
                            if (!handleClasses()) {
                                log.warn("Cannot handle classes!")
                                return;
                            }
                            break;
                        case MethodNode:
                            if (!handleMethods()) {
                                log.warn("Cannot handle methods!")
                                return;
                            }
                            break;
                        case FieldNode:
                            if (!handleFields()) {
                                log.warn("Cannot handle fields!")
                                return;
                            }
                            break;
                        default:
                            warnOfWrongType(annotated.class);
                            return
                    }
                    gAnnotation = new GAnnotation(nodes[0], nodes[1])
                }
                transform()
            }
        } catch (Exception e){
//            e.printStackTrace()
            source.addException(e)
        } finally {
            CompilationEnvironment.deleteRedundantClasses()
        }

    }

    abstract void transform()

}
