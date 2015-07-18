package can.i.has.gast.transform

import can.i.has.gast.Templatable
import can.i.has.gast.model.GAnnotation
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class CreateAnnotationTemplate implements ASTTransformation{
    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        if (!nodes) return
        if (!nodes[0]) return
        if (!nodes[1]) return
        if (!(nodes[0] instanceof AnnotationNode)) return
        if (nodes[0].classNode?.name != Templatable.class.getName()) return
        if (!(nodes[1] instanceof ClassNode)) return
        println "nodes0"
        println "${nodes[0]}"
        println "${nodes[0].class}"
        println "${nodes[0].classNode}"
        println "nodes1"
        println "${nodes[1]}"
        println "${nodes[1].class}"
        generate(nodes[0], nodes[1], source)
    }

    void generate(AnnotationNode templatable, ClassNode node, SourceUnit unit){
        try {
            println(new GAnnotation(templatable))
//            println templatable.members.name
//            def name = templatable.name() ?: "G" + annotationNode.nameWithoutPackage
//            println "name $name"
//            def pkg = templatable.value() ?: annotationNode.package.name.split("[.]").findAll().join(".")
//            if (templatable.subpackage())
//                pkg += "." + templatable.subpackage()
//            println "pkg $pkg"
//
//            def fields = annotationNode.methods.collect { MethodNode methodNode ->
//                "${methodNode.returnType.name} ${methodNode.name}"
//            }.join("\n\n")
//
//            println "field $fields"
//
//            def constructor = "$name(${annotationNode.name} gAnnotation){\n" << ""
//            annotationNode.methods.each { MethodNode entry ->
//                constructor << "this.${entry.name} = gAnnotation.${entry.name}()\n"
//            }
//            constructor << "}"
//
//            println("constructor $constructor")
//
//            def cast = annotationNode.name << " toAnnotation(){\n"
//            cast << "new ${annotationNode.name} {\n"
//            annotationNode.methods.each { MethodNode entry ->
//                cast << "${entry.returnType.name} ${entry.name}(){ this.${entry.name} }  \n\n"
//            }
//            cast << "@Override\n" +
//                "Class<? extends Annotation> annotationType() {\n" +
//                "return ${annotationNode.name}\n" +
//                "}\n\n"
//            cast << "}"
//
//            println("cast $cast")
//
//            def src = "" << ""
//            if (pkg)
//                src << "package $pkg \n\n"
//            src << """class $name {
//    $fields
//
//    $constructor
//
//    $cast
//    }"""
//
//            def cu = new CompilationUnit()
//            cu.classLoader = unit.classLoader
//            cu.configuration = unit.configuration
//            cu.addSource(name, src)
//            cu.compile()
        } catch (Throwable t){
            t.printStackTrace(System.out)
            throw t
        }
    }
}
