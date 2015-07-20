package can.i.has.gast.model.factory

import can.i.has.gast.CompilationEnvironment
import can.i.has.gast.model.GClass
import can.i.has.gast.model.GMethod
import can.i.has.gast.utils.CompilationLogger
import can.i.has.gast.utils.InheritanceUtils
import can.i.has.gast.utils.SourceUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.SourceUnit

import groovy.transform.Canonical

@Canonical
class GMethodFactory {
    protected SourceUnit sourceUnit

    GMethodFactory(SourceUnit sourceUnit) {
        this.sourceUnit = sourceUnit
    }

    GMethodFactory() {
        sourceUnit = CompilationEnvironment.sourceUnit
        assert sourceUnit //todo: exception
    }

    GMethod getGMethod(GMethod c){
        new GClass(new ClassNode(c))
    }

    GMethod getGMethod(ClassNode classNode, String source){
        def classSource = """package ${classNode.packageName}

class ${classNode.nameWithoutPackage}Whatever {
$source
}
"""
        //todo: will this work if method uses super?
        GClass gClass = GClass.compile(false, classSource)
        def out = gClass.node.methods.find {
            it.name == SourceUtils.getMethodName(source)
        }
        out.declaringClass = new ClassNode(classNode)
        new GMethod(out)
    }
}
