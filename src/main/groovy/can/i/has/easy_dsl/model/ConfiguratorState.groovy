package can.i.has.easy_dsl.model

import can.i.has.easy_dsl.Configurator
import can.i.has.easy_dsl.impl.ConfiguratorRepository
import can.i.has.easy_dsl.impl.utils.MOPUtils

import groovy.transform.Canonical

@Canonical
class ConfiguratorState {
    Configurator configurator
    def configured

    List<String> scopeStack = []

    def getScope() {
        if (!scopeStack)
            return null
        scopeStack.last()
    }

    def getTarget() {
        if (scope == null)
            return null
        def targetPerScope = ConfiguratorRepository.instance.scopeRegistry[configured.class]
        assert targetPerScope?.containsKey(scope)
        return configured.metaClass.getProperty(configured, targetPerScope[scope])
    }

    def getProp(String name) {
        MOPUtils.getProperty(configured, name)
    }

    void setProp(String name, Object val) {
        MOPUtils.setProperty(configured, name, val)
    }

    def invoke(String name, Object[] args) {
        MOPUtils.invoke(configured, name, args)
    }

    boolean canInvoke(String name, Object[] args) {
        MOPUtils.hasMethod(configured, name, args)
    }
}
