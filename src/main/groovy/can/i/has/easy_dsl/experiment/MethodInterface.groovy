package can.i.has.easy_dsl.experiment


interface MethodInterface {
    String getName()

    def invoke(Configurator configurator, Object[] args)
}
