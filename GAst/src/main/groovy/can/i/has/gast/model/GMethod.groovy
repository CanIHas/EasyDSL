package can.i.has.gast.model

import can.i.has.gast.model.factory.GMethodFactory
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode

import groovy.transform.Canonical
import groovy.transform.PackageScope

@Canonical
class GMethod implements NodeView<MethodNode>{
    static {
        MethodNode.declaredFields.find {
            it.name == "name"
        }.accessible = true
    }

    protected MethodNode methodNode

    GMethod(MethodNode methodNode) {
        this.methodNode = methodNode
    }

    MethodNode getNode() {
        return methodNode
    }

    String getName() {
        return methodNode.name
    }

    void setName(String name) {
        methodNode.name = name
    }

    AccesssModifier getAccessModifier(){
        if (methodNode.getAnnotations(new ClassNode(PackageScope)))
            return AccesssModifier.PACKAGE
        if (methodNode.public)
            return AccesssModifier.PUBLIC
        if (methodNode.protected)
            return AccesssModifier.PROTECTED
        if (methodNode.private)
            return AccesssModifier.PRIVATE
        assert "MethodNode should always have some (possibly implicit) access modifier!" && false
    }

    /**
     * Standard JVM signature, containing info about method name and parameter types.
     *
     * It is composed as:
     * <pre>[className].[methodName][JNISignature]</pre>.
     *
     * If declaring class is 'null' or withClass=false, "(?)" is used instead of [className].
     * @return Method signature
     */
    String getSignature(boolean withClass=true){
        def classSignature = withClass ? methodNode.declaringClass.name : "(?)"
        "${classSignature}.${methodNode.name}"
    }

    static String encodeType(Class clazz){
        switch (clazz) {
            case boolean: return "Z"
            case byte: return "B"
            case char: return "C"
            case short: return "S"
            case int: return "I"
            case long: return "J"
            case float: return "F"
            case double: return "D"
        }
        if (clazz.isArray())
            return "["+encodeType(clazz.componentType)
        return "L"+clazz.name.replaceAll("[.]", "/")+";"

    }

    /**
     * JNI signature, useful for checking whether methods have the same return type and signature.
     * @link http://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/types.html
     * @return JNI signature
     */
    String getJNISignature(){
        def params = methodNode.parameters.collect {
            encodeType(it.type.typeClass)
        }.join("")
        def retType = encodeType(methodNode.returnType.typeClass)
        "($params)$retType"
    }

    static GMethod compile(ClassNode classNode, String source){
        new GMethodFactory().getGMethod(classNode, source)
    }

    static GMethod compile(Class clazz, String source){
        compile(new ClassNode(clazz), source)
    }

    static GMethod compile(String pkg, String className, String source){
        pkg = pkg.split("[.]").findAll().join(".")
        def qualified = pkg ? "${pkg}.${className}" : className
        compile(Class.forName(qualified), source)
    }
}
