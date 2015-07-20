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

    //todo: inner classes

//    List<GMethod> getMethods(){
//        new AbstractList<GMethod>() {
//
////            boolean add(String source) {
////                add(GMethod.compile(packageName, className, source))
////            }
//
//            @Override
//            boolean add(GMethod element) {
//                classNode.addMethod(element.methodNode)
//            }
//
//            @Override
//            GMethod remove(int index){
//                classNode.removeMethod(get(index).node)
//            }
//
//            @Override
//            GMethod get(int index) {
//                new GMethod(classNode.methods[index])
//            }
//
//            @Override
//            int size() {
////                assert false && classNode
////                log.info classNode
//                classNode.methods.size()
//            }
//        }
//    }
//
//    List<GField> getFields(){
//        new AbstractList<GField>() {
//            @Override
//            boolean add(GField element) {
//                classNode.addMethod(element.fieldNode)
//            }
//
//            @Override
//            GField remove(int index){
//                classNode.removeMethod(get(index).node)
//            }
//
//            @Override
//            GField get(int index) {
//                return null
//            }
//
//            @Override
//            int size() {
//                return 0
//            }
//        }
//    }

    ClassNode getNode(){
        classNode
    }

    static GClass compile(boolean store=true, String source){
        new GClassFactory(CompilationEnvironment.sourceUnit).getGClass(store, source)
    }
}
