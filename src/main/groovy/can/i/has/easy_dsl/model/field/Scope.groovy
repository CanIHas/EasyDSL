package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.impl.utils.DelegationUtils
import can.i.has.easy_dsl.model.ConfiguratorState
import can.i.has.easy_dsl.model.DslMethod

import groovy.transform.Canonical

@Canonical
class Scope implements DslMethod {
    String name

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        return args?.size() == 1 && args[0] instanceof Closure
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        try {
            state.scopeStack.push(name)
            DelegationUtils.callWithDelegate(state.configurator, args[0])
            return state.target
        } finally {
            state.scopeStack.pop()
        }
    }
}
