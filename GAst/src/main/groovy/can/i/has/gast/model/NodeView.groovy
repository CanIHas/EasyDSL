package can.i.has.gast.model

import org.codehaus.groovy.ast.ASTNode


interface NodeView<A extends ASTNode> {
    A getNode()
}
