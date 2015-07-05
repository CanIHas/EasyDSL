package can.i.has.easy_dsl

import can.i.has.easy_dsl.impl.utils.DelegationUtils
import can.i.has.easy_dsl.impl.utils.MOPUtils


class Utils {
    static Configurator getConfigurator(Object o){
        if (MOPUtils.hasProperty(o, "configurator")) {
            def val = MOPUtils.getProperty(o, "configurator")
            if (val == null) {
                val = new Configurator(o)
                MOPUtils.setProperty(o, "configurator", val)
                return val
            }
        }
        return new Configurator(o)
    }

    static <T> T configure(T obj, Closure closure){
        DelegationUtils.callWithDelegate(getConfigurator(obj), closure)
        obj
    }
}
