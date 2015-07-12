package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.Utils
import can.i.has.easy_dsl.api.field.FieldConfigurationStrategy
import can.i.has.easy_dsl.impl.utils.MOPUtils
import can.i.has.easy_dsl.model.ConfiguratorState

import groovy.transform.Canonical

import java.util.concurrent.Callable

@Canonical
class RegularObtainer implements Obtainer {
    final String name
    final String fieldName
    final Class type
    final FieldConfigurationStrategy strategy
    final boolean withSetter
    final boolean allowOverwrite
    final boolean withMapping
    final Callable constructor

    @Override
    boolean canBeInvoked(ConfiguratorState state, Object[] args) {
        //todo: consider parameters like withSetter, etc
        if (!(args && args.size() < 4))
            return false
        if (args.size() == 1)
            return args[0] instanceof Closure || args[0] instanceof Map || type.isInstance(args[0])
        if (args.size() == 2) {
            return args[1] instanceof Closure
        } else if (args.size() == 3) {
            return args[0] instanceof Map && args[2] instanceof Closure
        }
        return true
    }

    List preprocess(Object[] args) {
        if (args.size() == 1) {
            switch (args[0]) {
                case Closure: return [[:], null, args[0]]
                case Map: return [args[0], null, {}]
                case type: return [[:], args[0], {}]
                default: assert false //should not happen at all
            }
        } else if (args.size() == 2) {
            if (args[0] instanceof Map)
                return [args[0], null, args[1]]
            else
                return [[:], args[0], args[1]]
        } else {
            return [args[0], args[1], args[2]]
        }
    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        List preprocessed = preprocess(args)
        Map kwargs = preprocessed[0]
        Object val = preprocessed[1]
        Closure closure = preprocessed[2]
        def resultVal = state.getProp(fieldName)

        if (withSetter) {
            if (!allowOverwrite && resultVal)
                assert !val
            else if (!resultVal && !val) {
                def newVal = constructor.call(type)
                assert type.isInstance(newVal)
                resultVal = newVal
            } else if (val)
                resultVal = val
        }
        //fixme: can we be sure that resultVal!=null ? if not, kwargs may fail
        if (!withMapping)
            assert !kwargs
        if (kwargs)
            kwargs.each { k, v ->
//                state.setProp(k, v)
                MOPUtils.setProperty(resultVal, k, v)
            }

        switch (strategy) {
            case FieldConfigurationStrategy.NONE: break
            case FieldConfigurationStrategy.BUILD:
                resultVal.with closure
                break
            case FieldConfigurationStrategy.CONFIGURE:
                Utils.configure(resultVal, closure)
                break
        }

        return resultVal
    }
}
