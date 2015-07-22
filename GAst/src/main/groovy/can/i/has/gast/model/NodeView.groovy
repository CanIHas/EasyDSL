package can.i.has.gast.model

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

/**
 * Most basic interface of GAst, marking entity classes representing AST parts.
 * @param < A > Underlying ASTNode class
 */
trait NodeView<A extends ASTNode> {
    /**
     *
     * @return Underlying ASTNode
     */
    abstract A getNode()

    A asClass(Class clazz){
        def node =getNode()
        if (clazz.isInstance(node))
            return node
        throw new GroovyCastException(this, clazz)
    }
}
