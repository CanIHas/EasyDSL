package can.i.has.easy_dsl

import can.i.has.easy_dsl.experiment.MethodInterface
import can.i.has.easy_dsl.impl.ConfiguratorRepository
import can.i.has.easy_dsl.model.ConfiguratorState
import can.i.has.easy_dsl.model.DslMethod
import can.i.has.easy_dsl.model.exceptions.MissingConfiguratorMethodException
import can.i.has.easy_dsl.model.exceptions.MissingConfiguratorPropertyException

class Configurator {
    ConfiguratorState $state = new ConfiguratorState()
    List<DslMethod> $methods

    static final private Object[] EMPTY = [] as Object[]

    Configurator(obj) {
        this.$state.configured = obj
        this.$state.configurator = this
        this.$methods = ConfiguratorRepository.instance.dslForClass[obj.class]
    }

    def methodMissing(String name, args) {
        def m = this.@$methods.find {
            it.name == name
        }
        if (m?.canBeInvoked(this.@$state, args))
            m.invoke(this.@$state, args)
        else
            throw new MissingConfiguratorMethodException(name, Configurator, args, this.$state.configured)
    }

    def propertyMissing(String name) {
        def m = this.@$methods.find {
            it.name == "get${name.capitalize()}"
        }
        if (m?.canBeInvoked(this.@$state, EMPTY))
            m.invoke(this.@$state, EMPTY)
        else
            throw new MissingConfiguratorPropertyException(name, Configurator, this.$state.configured)
    }

    def propertyMissing(String name, val) {
        def m = this.@$methods.find {
            it.name == "set${name.capitalize()}"
        }
        def arg = [val] as Object[]
        if (m?.canBeInvoked(this.@$state, arg))
            m.invoke(this.@$state, arg)
        else
            throw new MissingConfiguratorPropertyException(name, Configurator, this.$state.configured)
    }
}
