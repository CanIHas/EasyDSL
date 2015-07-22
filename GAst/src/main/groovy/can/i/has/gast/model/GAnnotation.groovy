package can.i.has.gast.model

import can.i.has.gast.CompilationEnvironment
import can.i.has.gast.utils.InheritanceUtils
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.ClosureExpression

import groovy.transform.Canonical

import java.lang.reflect.Method

@Canonical
class GAnnotation implements NodeView<AnnotationNode>{

    AnnotationNode annotationNode
    /**
     * Class object for interface of viewed annotation.
     */
    Class annotationClass
    /**
     * Annotation parameters
     */
    Map<String, Object> members

    GAnnotation(AnnotationNode annotationNode) {
        this.annotationNode = annotationNode
        init()
    }

    private void init(){
        annotationClass = annotationNode.classNode.typeClass
        members = [:]
        annotationClass.methods.findAll {
            InheritanceUtils.declares(annotationClass, it)
        }.each { Method method ->
            def name = method.name
            if (annotationNode.members.containsKey(name))
                members[name] = annotationNode.members[name]
            else
                if (method.defaultValue != null)
                    members[name] = method.defaultValue
                else
                    //todo: dedicate
                    throw new RuntimeException("Member ${method.name} has no default value and it wasn't provided! (annotation type: ${annotationClass})")
            if (members[name] instanceof ClosureExpression) {
                members[name] = CompilationEnvironment.compileClosureExpression(members[name])

            } else
                members[name] = members[name].value
        }

    }

    @Override
    AnnotationNode getNode() {
        annotationNode
    }
}
