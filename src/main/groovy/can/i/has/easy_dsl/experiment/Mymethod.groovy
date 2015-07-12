package can.i.has.easy_dsl.experiment

import groovy.transform.Canonical

@Canonical
class Mymethod implements MethodInterface {

    String name

    @Override
    def invoke(Configurator configurator, Object[] args) {
        println "Calling $name with $args"
    }
}
