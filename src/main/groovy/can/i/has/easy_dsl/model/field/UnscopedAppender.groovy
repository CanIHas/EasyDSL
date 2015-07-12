package can.i.has.easy_dsl.model.field

import can.i.has.easy_dsl.model.ConfiguratorState

import groovy.transform.Canonical
import groovy.transform.InheritConstructors

@Canonical
class UnscopedAppender extends Appender {
    UnscopedAppender(Obtainer obtainer, String target) {
        super(obtainer)
        this.target = target
    }
    String target

//    String getTargetName(ConfiguratorState state){
//        target
//    }

    @Override
    def invoke(ConfiguratorState state, Object[] args) {
        try {
            state.scopeStack.push(target)
            return super.invoke(state, args)
        } finally {
            state.scopeStack.pop()
        }
    }
}
