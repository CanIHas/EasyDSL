package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.impl.utils.MOPUtils
import can.i.has.easy_dsl.model.ConfiguratorState
import can.i.has.easy_dsl.model.DslMethod

import groovy.transform.Canonical

@Canonical
class Appender implements DslMethod {
    Obtainer obtainer

    @Override
    String getName() {
        obtainer.name
    }

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        obtainer.canBeInvoked(state, args)
    }

    String getTargetName(ConfiguratorState state){
        state.scope
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        def val = obtainer.invoke(state, args)
        def localTarget = MOPUtils.getProperty(state.configured, getTargetName(state))
        if (localTarget == null) {
            localTarget = []
            MOPUtils.setProperty(state.configured, getTargetName(state), [])
        }
        localTarget.add(val)
//        return localTarget
        return val
    }
}
