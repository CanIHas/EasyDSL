package can.i.has.gast.model

import can.i.has.gast.CompilationEnvironment
import can.i.has.gast.model.factory.GMethodFactory
import org.codehaus.groovy.ast.ClassNode
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

    static GMethod compile(ClassNode classNode, String source){
        new GMethodFactory().getGMethod(classNode, source)
    }

    static GMethod compile(Class clazz, String source){
        compile(new ClassNode(clazz), source)
    }

    static GMethod compile(String pkg, String className, String source){
        pkg = pkg.split("[.]").findAll().join(".")
        def qualified = pkg ? "${pkg}.${className}" : className
        compile(Class.forName(qualified), source)
    }
}
