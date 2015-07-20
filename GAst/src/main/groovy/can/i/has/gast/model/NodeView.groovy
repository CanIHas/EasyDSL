package can.i.has.gast.model

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.runtime.typehandling.GroovyCastException


trait NodeView<A extends ASTNode> {
    abstract A getNode()

    A asClass(Class clazz){
        def node =getNode()
        if (clazz.isInstance(node))
            return node
        throw new GroovyCastException(this, clazz)
    }
}
