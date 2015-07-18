package can.i.has.gast.utils

import jdk.internal.org.objectweb.asm.Opcodes
import org.codehaus.groovy.ast.ASTNode

import java.lang.reflect.Field


class NodeEqualityUtils {
    static CompilationLogger log = new CompilationLogger(NodeEqualityUtils)

    static boolean equals(ASTNode node1, ASTNode node2, List<ASTNode> checked=[]){
        log.info "Comparing:"
        log.info node1
        log.info node2
        if (node1.class != node2.class)
            return false
        log.info "classes match"
        return node1.class.declaredFields.every { Field field ->
            log.info "field $field"
            if (!(field.modifiers & Opcodes.ACC_PUBLIC))
                return true //ignore private, etc
            log.info "public, go on"
            def v1 = field.get(node1)
            def v2 = field.get(node2)
            log.info "v1: $v1"
            log.info "v2: $v2"
            if (!(
                ASTNode.isAssignableFrom(field.type) ||
                    Map.isAssignableFrom(field.type) ||
                    Collection.isAssignableFrom(field.type)
            ))
                return use (NodeEqualityUtils) {
                    log.info "Simple equals"
                    //check non-ast fields with simple equals(), use category for inner element comparing
                    return v1.equals(v2)
                }
            if (checked.contains(v1) || checked.contains(v2))
                return true // dont go into infinite recursion if AST points to some kind of parent
            checked << v1 << v2
            log.info "compare values"
            return equals(v1, v2, checked)
        }
    }
}
