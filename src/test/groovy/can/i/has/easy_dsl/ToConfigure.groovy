package can.i.has.easy_dsl

import can.i.has.easy_dsl.api.field.Access
import can.i.has.easy_dsl.api.field.Configure

import groovy.transform.Canonical

@Canonical
@Access
class ToConfigure implements ConfigurableTrait{
    String a
    float b

    @Configure
    ToConfigure toConfigure
}
