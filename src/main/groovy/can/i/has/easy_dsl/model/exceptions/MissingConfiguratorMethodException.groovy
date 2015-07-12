package can.i.has.easy_dsl.model.exceptions

import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.MethodRankHelper


class MissingConfiguratorMethodException extends MissingMethodException{
    Object configured

    MissingConfiguratorMethodException(String method, Class type, Object[] arguments, configured) {
        super(method, type, arguments)
        this.configured = configured
    }

    public String getMessage() {
        return "No signature of method (used for configuration of ${configured.class}): "
        + (isStatic ? "static " : "")
        + type.getName()
        + "."
        + method
        + "() is applicable for argument types: ("
        + InvokerHelper.toTypeString(arguments)
        + ") values: "
        + InvokerHelper.toArrayString(arguments, 60, true)
        + MethodRankHelper.getMethodSuggestionString(method, type, arguments);
    }
}
