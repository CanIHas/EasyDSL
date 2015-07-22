package can.i.has.gast.model.factory

import can.i.has.gast.CompilationEnvironment
import can.i.has.gast.model.GClass
import can.i.has.gast.utils.SourceUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.SourceUnit

import groovy.transform.Canonical

@Canonical
class GClassFactory {
    protected SourceUnit sourceUnit

    GClassFactory(SourceUnit sourceUnit) {
        this.sourceUnit = sourceUnit
    }

    GClassFactory() {
        sourceUnit = CompilationEnvironment.sourceUnit
        assert sourceUnit
    }

    GClass getGClass(Class c){
        new GClass(new ClassNode(c))
    }

    /**
     * Compile new class basing on given source.
     *
     * If <pre>store=true</pre> this class will be kept as result of compilation process.
     * If not, this class is assumed as auxiliary one and its .class file is removed at the end of the
     * compilation process.
     *
     * Whatever the value of <pre>store</pre>, compiled class will be available in runtime, so you can evaluate code
     * live.
     * todo: add throws
     * @param store Whether this class is auxiliary (<pre>store=false</pre>) and removed after compiling everything else.
     * @param source Source of new class.
     * @return GClass representing that class
     */
    GClass getGClass(boolean store = true, String source){
        def compilationUnit = CompilationEnvironment.newCompilationUnit(store, sourceUnit)
        def name = SourceUtils.getTypeName(source)
        def pkg = SourceUtils.getPackage(source)
        def nodes = new AstBuilder().buildFromString(source)
        def classNode = nodes.find { it instanceof ClassNode }
        if (store) {
            compilationUnit.addClassNode(classNode)
            compilationUnit.compile()
        }
        new GClass(classNode)
    }


}
