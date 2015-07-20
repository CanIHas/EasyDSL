package can.i.has.gast.model.factory

import can.i.has.gast.CompilationEnvironment
import can.i.has.gast.model.GClass
import can.i.has.gast.model.GField
import can.i.has.gast.utils.SourceUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.control.SourceUnit


class GFieldFactory {
    protected SourceUnit sourceUnit

    GFieldFactory(SourceUnit sourceUnit) {
        this.sourceUnit = sourceUnit
    }

    GFieldFactory() {
        sourceUnit = CompilationEnvironment.sourceUnit
        assert sourceUnit
    }

    GField getGField(FieldNode fieldNode){
        new GField(fieldNode)
    }

    GField getGField(ClassNode classNode, String source){
        def classSource = """package ${classNode.packageName}

class ${classNode.nameWithoutPackage}Whatever {
$source
}
"""
        //todo: will this work if method uses super?
        GClass gClass = GClass.compile(false, classSource)
        def out = gClass.node.fields.find {
            it.name == SourceUtils.getFieldName(source)
        }
        out.declaringClass = new ClassNode(classNode)
        new GField(out)
    }
}
