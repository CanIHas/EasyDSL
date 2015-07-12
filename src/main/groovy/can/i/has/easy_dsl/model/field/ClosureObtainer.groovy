package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.model.ConfiguratorState

import groovy.transform.Canonical

@Canonical
class ClosureObtainer implements Obtainer {
    String name

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        return args?.size() == 1 && args[0] instanceof Closure
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        args[0]
    }
}
