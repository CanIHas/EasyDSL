package can.i.has.easy_dsl

import can.i.has.easy_dsl.impl.DelegationUtils


class Utils {
    static Configurator getConfigurator(Object o){
        new Configurator(o)
    }

    static <T> T configure(T obj, Closure closure){
        DelegationUtils.callWithDelegate(getConfigurator(obj), closure)
        obj
    }
}
