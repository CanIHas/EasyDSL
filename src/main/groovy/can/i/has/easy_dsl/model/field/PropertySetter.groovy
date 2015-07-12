package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.model.ConfiguratorState
import can.i.has.easy_dsl.model.DslMethod

import groovy.transform.Canonical

@Canonical
class PropertySetter implements DslMethod {
    Obtainer obtainer

    @Override
    String getName() {
        obtainer.name
    }

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        obtainer.canBeInvoked(state, args)
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        def val = obtainer.invoke(state, args)
        state.setProp(name, val)
        return val
    }
}
