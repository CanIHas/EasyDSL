package can.i.has.easy_dsl

import can.i.has.easy_dsl.api.field.Access
import can.i.has.easy_dsl.api.field.Build
import can.i.has.easy_dsl.api.field.Configure
import can.i.has.easy_dsl.api.field.Getter
import can.i.has.easy_dsl.api.field.InternalField
import can.i.has.easy_dsl.api.method.Delegate
import can.i.has.easy_dsl.api.method.InternalMethod

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
