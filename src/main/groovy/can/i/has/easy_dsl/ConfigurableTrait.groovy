package can.i.has.easy_dsl

trait ConfigurableTrait {
    @Lazy Configurator configurator = Utils.getConfigurator(this)

    void configure(Closure c){
        Utils.configure(this, c)
    }
}
