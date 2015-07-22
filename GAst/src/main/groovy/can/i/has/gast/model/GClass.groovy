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

    /**
     * Compile and add new method.
     * todo: add throws
     * @param source source code of new method, containing annotations (optional), modifiers (optional), method name,
     * parameters (optional) and body
     * @return New GMethod object representing method that was just added
     */
    GMethod addMethod(String source) {
        def method = GMethod.compile(this.node, source)
        classNode.addMethod(method.node)
        return method
    }

    /**
     * Get methods defined in this class, without those inherited
     * @return list (possibly empty) of GMethods defined in this class
     */
    List<GMethod> getMethods(){
        classNode.methods.collect { new GMethod(it) }
    }

    /**
     * Get all methods of this class, with those inherited
     * @return list of all GMethods of this class
     */
    List<GMethod> getAllMethods(){
        classNode.allDeclaredMethods.collect { new GMethod(it) }
    }

    /**
     * Get all abstract methods of this class
     * @return list (possibly empty) of abtsract GMethods of this class
     */
    List<GMethod> getAbstractMethods(){
        classNode.abstractMethods?.collect { new GMethod(it) } ?: []
    }

//    void setMethods(Iterable<GMethod> methods){
//        methods.each { GMethod newMethod ->
//            if (this.methods.contains(newMethod))
//        }
//    }

    /**
     * Compile and add new field.
     * todo: add throws
     * @param source source code of new field, containing annotations (optional), modifiers (optional), name, and initial
     * value (optional)
     * @return New GMethod object representing method that was just added
     */
    GField addField(String source){
        def field = GField.compile(this.node, source)
        classNode.addField(field.node)
        return field
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
