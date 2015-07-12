package can.i.has.easy_dsl

import groovyjarjarasm.asm.Opcodes
import javassist.bytecode.Opcode
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ArrayExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import groovy.json.JsonBuilder

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class AnnImpl implements ASTTransformation {
    
    @Override
    void visit(ASTNode[] astNodes, SourceUnit source) {
        println "visit $astNodes $source"
        
        if (!astNodes) return
        
        if (!astNodes[0]) return
        
        if (!astNodes[1]) return
        
        if (!(astNodes[0] instanceof AnnotationNode)) return
        
        if (astNodes[0].classNode?.name != Ann.class.getName()) return
        
        if (!(astNodes[1] instanceof ClassNode)) return
        println "no return"
        println "target ${source.configuration.targetDirectory}"
        compileNew(astNodes[1], source)
        println "compiled (outer)"
        addMethod(astNodes[1])
        println "added"
    }

    void compileNew(ClassNode classNode, SourceUnit unit){
        println "compiling"
        def pkg = classNode.package.name.split("[.]").findAll().join(".")
        println "pkg $pkg"
        def name = classNode.nameWithoutPackage
        println "name $name"
        def src = """package $pkg;

class ${name}Extender implements can.i.has.easy_dsl.Extender {
    String message(){
        "Ive been made for $name"
    }
}
"""
        println "src\n$src"
        def compileUnit = new CompilationUnit(unit.configuration)
        compileUnit.setClassLoader(unit.classLoader)
        println "compileUnit $compileUnit"
        compileUnit.addSource("${pkg}.${name}", src)
        println "added source"
        try {
            compileUnit.compile()
            println "compiled"
        } catch (CompilationFailedException cfe){
            cfe.printStackTrace(System.out)
            throw cfe
        }
    }

    void addMethod(ClassNode classNode){
//        new AstBuilder().buildFromSpec {
//            method('getExtender', Opcodes.ACC_PUBLIC, Void.TYPE) {
//                parameters {
//                    parameter 'args': String[].class
//                }
//                exceptions {}
//                block {
//                    returnStatement {
//                        expression {
//                            constructorCall(Class.forName())
//                        }
//                    }
//                }
//            }
//        }
        println "new method"
        def pkg = classNode.package.name.split("[.]").findAll().join(".")
        println "pkg $pkg"
        def name = classNode.nameWithoutPackage
        println "name $name"
        try {
//            BlockStatement m = new AstBuilder().buildFromString(
//                "def getExtender() { return Class.forName('${pkg}.${name}Extender').newInstance() }"
//            ).first()
//            println "m $m"
//            m.statements.eachWithIndex { a, i ->
//                println "stmnt $i -> $a"
//            }
            MethodNode m = new MethodNode(
                "getExtender",
                Opcodes.ACC_PUBLIC,
                new ClassNode(Extender),
                new Parameter[0],
                new ClassNode[0],
                new ReturnStatement(
                    new ConstructorCallExpression(
                        new ClassNode(Class.forName("${pkg}.${name}Extender")),
                        new TupleExpression()
                    )
                )
            )
            classNode.addMethod m
            println "added and fuck you"
        } catch (Throwable t){
            t.printStackTrace(System.out)
            throw t
        }
    }
}
