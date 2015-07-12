package can.i.has.easy_dsl.model

interface DslMethod {
    String getName()

    boolean canBeInvoked(ConfiguratorState state, Object[] args)

    def invoke(ConfiguratorState state, Object[] args)
}