package can.i.has.easy_dsl.addressBook

import can.i.has.easy_dsl.api.field.Access
import can.i.has.easy_dsl.api.field.MethodSetter
import can.i.has.easy_dsl.api.modifiers.Collect

import groovy.transform.Canonical

@Canonical
class Person {
    @Access
    @MethodSetter
    String name

    @Access
    @MethodSetter
    String surname

    @Access
    @MethodSetter
    String email

    @Collect(elementName = "nick", type = String)
    @MethodSetter
    List<String> nicks
}