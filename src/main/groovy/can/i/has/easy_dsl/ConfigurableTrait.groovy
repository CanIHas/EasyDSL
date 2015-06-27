package can.i.has.easy_dsl

import can.i.has.easy_dsl.impl.DelegationUtils

trait ConfigurableTrait {

//    @Lazy Configurator configurator = new Configurator(this)
    @Lazy Configurator configurator = new Configurator(this)


    void configure(Closure c){
        DelegationUtils.callWithDelegate(configurator, c)
    }
}
