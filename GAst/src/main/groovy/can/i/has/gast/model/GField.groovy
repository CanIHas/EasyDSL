package can.i.has.gast.model

import can.i.has.gast.model.factory.GFieldFactory
import org.codehaus.groovy.ast.ClassNode
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

    static GField compile(ClassNode classNode, String source){
        new GFieldFactory().getGField(classNode, source)
    }

    static GField compile(Class clazz, String source){
        compile(new ClassNode(clazz), source)
    }

    static GField compile(String pkg, String className, String source){
        pkg = pkg.split("[.]").findAll().join(".")
        def qualified = pkg ? "${pkg}.${className}" : className
        compile(Class.forName(qualified), source)
    }
}
