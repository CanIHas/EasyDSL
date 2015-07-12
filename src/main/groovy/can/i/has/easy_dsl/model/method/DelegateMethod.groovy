package can.i.has.easy_dsl.model.method

import can.i.has.easy_dsl.model.ConfiguratorState
import can.i.has.easy_dsl.model.DslMethod

import groovy.transform.Canonical

@Canonical
class DelegateMethod implements DslMethod {
    String name

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        state.canInvoke(name, args)
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        state.invoke(name, args)
    }
}
