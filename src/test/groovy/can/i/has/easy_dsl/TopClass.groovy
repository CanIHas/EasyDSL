package can.i.has.easy_dsl

import can.i.has.easy_dsl.api.field.Access
import can.i.has.easy_dsl.api.field.Build
import can.i.has.easy_dsl.api.modifiers.Collect
import can.i.has.easy_dsl.api.field.Configure
import can.i.has.easy_dsl.api.field.Getter
import can.i.has.easy_dsl.api.field.InternalField
import can.i.has.easy_dsl.api.field.MethodSetter
import can.i.has.easy_dsl.api.method.Delegate
import can.i.has.easy_dsl.api.method.InternalMethod
import can.i.has.easy_dsl.api.modifiers.CollectScope

import groovy.transform.Canonical

@Canonical
@Getter
class TopClass implements ConfigurableTrait{
    int x
    String y
    @Access
    List z

    @InternalField
    String ignored

    @Configure
    ToConfigure toConfigure

    @Build
    ToBuild toBuild

    @Access
    Closure someClosure

    @MethodSetter
    Closure otherClosure

    @MethodSetter
    Map someMap

    @MethodSetter
    @Collect(elementName = "text", type = String)
    List<String> someList = []

    @MethodSetter
    @CollectScope(elementName = "number", type = Integer)
    List<Integer> intList = []

    @Delegate
    def foo(){
        return this
    }

    @InternalMethod
    def bar(){
        throw new TestException()
    }

    static class TestException extends RuntimeException {}
}
