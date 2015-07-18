package can.i.has.gast.model

import can.i.has.gast.utils.SourceUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.SourceUnit

import groovy.transform.Canonical

@Canonical
class GClassFactory {
    protected SourceUnit sourceUnit

    GClass getGClass(Class c){
        new GClass(new ClassNode(c))
    }

    GClass getGClass(String source){
        def compilationUnit = new CompilationUnit()
        def name = SourceUtils.getTypeName(source)
        def pkg = SourceUtils.getPackage(source)
        def qualified = pkg ? "${pkg}.${name}" : name
        compilationUnit.classLoader = sourceUnit.classLoader
        compilationUnit.configuration = sourceUnit.configuration
        compilationUnit.addSource(qualified, source)
        compilationUnit.compile()
        getGClass(Class.forName(qualified))
    }


}
