package can.i.has.gast.model

import can.i.has.gast.CompilationEnvironment
import org.codehaus.groovy.ast.ClassNode

import groovy.transform.Canonical

@Canonical
class GClass implements NodeView<ClassNode>{
    protected ClassNode classNode

    GClass(ClassNode classNode) {
        this.classNode = classNode
    }

    List<GMethod> getMethods(){
        new AbstractList<GMethod>() {

            @Override
            boolean add(GMethod element) {
                classNode.addMethod(element.methodNode)
            }

            @Override
            GMethod remove(int index){
                classNode.removeMethod(get(index).node)
            }

            @Override
            GMethod get(int index) {
                new GMethod(classNode.methods[index])
            }

            @Override
            int size() {
                classNode.methods.size()
            }
        }
    }

    List<GField> getFields(){
        new AbstractList<GField>() {
            @Override
            boolean add(GField element) {
                classNode.addMethod(element.fieldNode)
            }

            @Override
            GField remove(int index){
                classNode.removeMethod(get(index).node)
            }

            @Override
            GField get(int index) {
                return null
            }

            @Override
            int size() {
                return 0
            }
        }
    }

    ClassNode getNode(){
        classNode
    }

    static GClass compile(String source){
        new GClassFactory(CompilationEnvironment.sourceUnit).getGClass(source)
    }
}
