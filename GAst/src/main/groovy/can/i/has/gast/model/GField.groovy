package can.i.has.gast.model

import org.codehaus.groovy.ast.FieldNode

import groovy.transform.Canonical

@Canonical
class GField implements NodeView<FieldNode>{
    protected FieldNode fieldNode

    GField(FieldNode fieldNode) {
        this.fieldNode = fieldNode
    }

    FieldNode getNode() {
        return fieldNode
    }
}
