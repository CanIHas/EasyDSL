package can.i.has.easy_dsl.model.exceptions

import org.codehaus.groovy.runtime.MethodRankHelper


class MissingConfiguratorPropertyException extends MissingPropertyException{
    Object configured

    MissingConfiguratorPropertyException(String property, Class type, configured) {
        super(property, type)
        this.configured = configured
    }

    public String getMessageWithoutLocationText() {
        final Throwable cause = getCause();
        if (cause == null) {
            if (super.getMessageWithoutLocationText() != null) {
                return super.getMessageWithoutLocationText();
            }
            return "No such property: " + property + " for class: " + type.getName() + ". Configured type is ${configured.class}" +
                MethodRankHelper.getPropertySuggestionString(property, type);
        }
        return "No such property: " + property + " for class: " + type.getName() + ". Configured type is ${configured.class}. Reason: " + cause;
    }
}
