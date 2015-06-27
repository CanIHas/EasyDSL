package can.i.has.easy_dsl.impl

class DelegationUtils {
    static <T> T callWithDelegate(Object delegate, Object[] args=[].toArray(), Closure<T> closure){
        def toCall = closure.clone()
        toCall.delegate = delegate
        toCall.resolveStrategy = Closure.DELEGATE_FIRST
        return toCall.call(args)
    }
}
