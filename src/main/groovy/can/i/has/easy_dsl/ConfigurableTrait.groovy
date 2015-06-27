package can.i.has.easy_dsl

import can.i.has.easy_dsl.impl.DelegationUtils

trait ConfigurableTrait {

//    @Lazy Configurator configurator = new Configurator(this)
    @Lazy Conf configurator = new Conf(this)


    void configure(Closure c){
        DelegationUtils.callWithDelegate(configurator, c)
    }
}
