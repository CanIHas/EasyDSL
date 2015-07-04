package can.i.has.easy_dsl.addressBook

import can.i.has.easy_dsl.api.field.Access
import can.i.has.easy_dsl.api.field.Configure
import can.i.has.easy_dsl.api.modifiers.CollectScope

import groovy.transform.Canonical


@Canonical
class AddressBook {
    @Access
    String name

    @Access
    boolean personal

    @Configure
    Person owner

    @CollectScope(elementName = "person", type = Person)
    @Configure
    List<Person> contacts
}
