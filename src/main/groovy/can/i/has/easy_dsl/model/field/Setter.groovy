package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.model.ConfiguratorState
import can.i.has.easy_dsl.model.DslMethod

import groovy.transform.Canonical

@Canonical
class Setter implements DslMethod {
    final String fieldName
    final Class type

    @Lazy
    String name = "set${fieldName.capitalize()}"

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        return args && args.size() == 1 && type.isInstance(args.first())
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        state.setProp(fieldName, args[0])
    }
}
