package can.i.has.gast

import jdk.internal.org.objectweb.asm.Opcodes
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit

final class CompilationEnvironment {
    private static ThreadLocal<SourceUnit> sourceUnit = new ThreadLocal<>()


    private static class TemporalClassLoader extends GroovyClassLoader {
        private SourceUnit su

        TemporalClassLoader(SourceUnit su){
            super(su.classLoader, su.configuration)
            println "su init $su"
            this.su = su
        }

        public Class loadClass(final String name, boolean lookupScriptFiles, boolean preferClassOverScript, boolean resolve)
            throws ClassNotFoundException, CompilationFailedException {
            def out = super.loadClass(name, lookupScriptFiles, preferClassOverScript, resolve)
            println "su $su"
            println "config ${su.configuration}"
            println "dir ${su.configuration.targetDirectory}"
            if (this.su?.configuration?.targetDirectory) {
                redundantClassFiles << new File(
                    this.su.configuration.targetDirectory,
                    name.replaceAll("[.]", "/")+".class"
                )
                redundantClassFiles << new File(
                    this.su.configuration.targetDirectory,
                    name.replaceAll("[.]", "/")+'$_getClosure_closure1.class'
                )
//                f.deleteOnExit()
            }
            return out
        }
    }

    private static List<File> redundantClassFiles = [].asSynchronized()

    static void deleteRedundantClasses(){
        redundantClassFiles.each {
            println it
            it.delete()
        }
    }

    static SourceUnit getSourceUnit() {
        return sourceUnit.get()
    }

    static void setSourceUnit(SourceUnit sourceUnit) {
        this.sourceUnit.set(sourceUnit)
    }

    static CompilationUnit newCompilationUnit(boolean store=true){
        def out = new CompilationUnit()
        out.configuration = new CompilerConfiguration(getSourceUnit().configuration)
        out.classLoader = store ?
            new GroovyClassLoader(getSourceUnit().classLoader, out.configuration) :
            new TemporalClassLoader(getSourceUnit())
        return out
    }

    static <T> T withSourceUnit(SourceUnit sourceUnit, Closure<T> closure){
        def oldUnit = CompilationEnvironment.sourceUnit
        try {
            setSourceUnit(sourceUnit)
            return closure.call()
        } finally {
            setSourceUnit(oldUnit)
        }
    }

    static compileClosureExpression(ClosureExpression closureExpression){
        def name = "\$PrecompiledClosure${closureExpression.hashCode()}"
        def cn = new ClassNode(
            name,
            Opcodes.ACC_PUBLIC,
            new ClassNode(Object)
        )
        def mn = new MethodNode(
            "getClosure",
            Opcodes.ACC_PUBLIC,
            new ClassNode(Closure),
            new Parameter[0],
            new ClassNode[0],
            new BlockStatement(
                [
                    new ReturnStatement(closureExpression)
                ],
                new VariableScope()
            )
        )
        cn.addMethod(mn)
        def cu = newCompilationUnit(false)
        cu.addClassNode(cn)
        cu.compile()
        return cu.classLoader.loadClass(name).newInstance().getClosure()
    }

//    static void log(object){
//        log "$object"
//    }
//
//    static void log(String msg){
//        System.out.println ">> ${this.class.name} :: $msg"
//    }
//
//    static void warn(object){
//        warn "$object"
//    }
//
//    static void warn(String msg){
//        System.err.println ">> ${this.class.name} :: $msg"
//    }
}
