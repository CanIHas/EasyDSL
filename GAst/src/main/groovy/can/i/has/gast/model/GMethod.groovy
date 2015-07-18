package can.i.has.gast.model

import org.codehaus.groovy.ast.MethodNode

import groovy.transform.Canonical

@Canonical
class GMethod implements NodeView<MethodNode>{
    protected MethodNode methodNode

    GMethod(MethodNode methodNode) {
        this.methodNode = methodNode
    }

    MethodNode getNode() {
        return methodNode
    }
}
