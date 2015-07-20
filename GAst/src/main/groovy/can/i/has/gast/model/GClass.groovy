package can.i.has.gast.model

import can.i.has.gast.CompilationEnvironment
import can.i.has.gast.model.factory.GClassFactory
import can.i.has.gast.utils.CompilationLogger
import org.codehaus.groovy.ast.ClassNode

import groovy.transform.Canonical

@Canonical
class GClass implements NodeView<ClassNode>{
    static CompilationLogger log = new CompilationLogger(GClass)
    protected ClassNode classNode

    GClass(ClassNode classNode) {
        this.classNode = classNode
    }

    String getPackageName(){
        classNode.package.name
    }

    String getClassName(){
        classNode.nameWithoutPackage
    }

    void addMethod(String source) {
        classNode.addMethod(GMethod.compile(this.node, source).node)
    }

    void addField(String source){
        classNode.addField(GField.compile(this.node, source).node)
    }

    //todo: get/setMethods(), -Fields()
    //todo: inner classes

    ClassNode getNode(){
        classNode
    }

    static GClass compile(boolean store=true, String source){
        new GClassFactory(CompilationEnvironment.sourceUnit).getGClass(store, source)
    }
}
