package can.i.has.gast

import can.i.has.gast.model.GAnnotation
import can.i.has.gast.model.GClass
import can.i.has.gast.model.GField
import can.i.has.gast.model.GMethod
import can.i.has.gast.utils.CompilationLogger


import static can.i.has.gast.CompilationEnvironment.*
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation

import java.lang.annotation.Annotation

/**
 * Abstract class to be extended by GAst-powered transformations.
 *
 * It provides standard boilerplate that returns from visit(). You can control what kind of elements (classes, methods,
 * fields) are handled by this transformation, and whether it can work with global transformations.
 * Also, it initializes gAnnotation, gClass, gMethod and gField fields providing views for annotation causing
 * transformation (gAnnotation) and transformed node (rest, depending on node type).
 */
abstract class GAstTransformation implements ASTTransformation{
    static CompilationLogger log = new CompilationLogger(GAstTransformation)

    protected GAnnotation gAnnotation
    protected ASTNode annotated

    @Lazy GClass $gClass = new GClass(annotated)
    @Lazy GMethod $gMethod = new GMethod(annotated)
    @Lazy GField $gField = new GField(annotated)

    GAnnotation getgAnnotation() {
        return gAnnotation
    }

    GClass getgClass() {
        return this.$gClass
    }

    GMethod getgMethod() {
        return this.$gMethod
    }

    GField getgField() {
        return this.$gField
    }

    /**
     * Can this class handle global transformations?
     */
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
     * Default gAnnotation for global transformations, if needed.
     * @return null if global transformations are disabled, else - default gAnnotation
     */
    GAnnotation annotationForGlobal(){
        null
    }

    /**
     * Can this class handle transformation of class nodes?
     */
    boolean handleClasses(){
        true
    }

    /**
     * Can this class handle transformation of method nodes?
     */
    boolean handleMethods(){
        true
    }

    /**
     * Can this class handle transformation of field nodes?
     */
    boolean handleFields(){
        true
    }

    private void warnOfWrongType(Class nodeClass){
        log.warn "Cannot handle $nodeClass. Only classes, methods and fields are supported."
    }

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        try {
            withSourceUnit(source) {
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
                    gAnnotation = new GAnnotation(nodes[0])
                }
                transform()
            }
        } catch (Exception e){
            source.addException(e)
        }

    }

    /**
     * Body of your transformation.
     * It will be executed with some sourceUnit in CompilationEnvironment and proper views initialized.
     */
    abstract void transform()

}
