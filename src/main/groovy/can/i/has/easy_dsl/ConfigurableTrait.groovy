package can.i.has.easy_dsl

trait ConfigurableTrait {

    @Lazy Configurator configurator = new Configurator(this)


    void configure(Closure c){
        def toCall = c.clone()
        c.delegate = this.configurator
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c.call()
    }
}
