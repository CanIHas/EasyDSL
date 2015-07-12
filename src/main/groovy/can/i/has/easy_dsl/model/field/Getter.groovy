package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.model.ConfiguratorState
import can.i.has.easy_dsl.model.DslMethod

import groovy.transform.Canonical

@Canonical
class Getter implements DslMethod {
    final String fieldName

    @Lazy
    String name = "get${fieldName.capitalize()}"

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        return !args
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        state.getProp(fieldName)
    }
}
