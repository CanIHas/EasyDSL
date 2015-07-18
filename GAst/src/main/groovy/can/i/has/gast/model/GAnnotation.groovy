package can.i.has.gast.model

import can.i.has.gast.CompilationEnvironment
import can.i.has.gast.utils.InheritanceUtils
import can.i.has.gast.utils.NodeEqualityUtils
import jdk.internal.org.objectweb.asm.Opcodes
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression

import groovy.transform.Canonical
import groovy.util.logging.Slf4j

import java.lang.reflect.Method

@Canonical
@Slf4j
class GAnnotation implements NodeView<AnnotationNode>{
    AnnotationNode annotationNode
    Class annotationClass
    Map<String, Object> members

    GAnnotation(AnnotationNode annotationNode, ClassNode classWithClosures=null) {
        this.annotationNode = annotationNode
        init(classWithClosures)
    }

    GAnnotation(AnnotationNode annotationNode, MethodNode methodNode) {
        this.annotationNode = annotationNode
        init(methodNode.declaringClass)
    }

    GAnnotation(AnnotationNode annotationNode, FieldNode fieldNode) {
        this.annotationNode = annotationNode
        init(fieldNode.declaringClass)
    }

    private void init(ClassNode classWithClosures){
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
//                log.info "inner classes: ${classWithClosures.innerClasses}"
//                ClassNode c = classWithClosures.innerClasses.find { ClassNode inner ->
//                    inner.methods.any { MethodNode mn ->
//                        mn.name == "doCall" && NodeEqualityUtils.equals(
//                            ((ClosureExpression)members[name]).code,
//                            mn.code)
//                    }
//                }
//                log.info c
//                log.info c.name
//                //todo: figure out if it is possible to remove inner class, as it becomes static
//                c.modifiers = (c.modifiers | Opcodes.ACC_STATIC)
//                def cu = CompilationEnvironment.newCompilationUnit(false)
//                cu.addClassNode(c)
//                cu.compile()
//                def closureClass = Class.forName(c.name)
//                println closureClass
//                members[name] = closureClass.newInstance(this, null)

            } else
                members[name] = members[name].value
        }

    }

    @Override
    AnnotationNode getNode() {
        annotationNode
    }
}
