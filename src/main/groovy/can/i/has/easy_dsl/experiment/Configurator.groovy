package can.i.has.easy_dsl.experiment


class Configurator {
    List<MethodInterface> methods

    def methodMissing(String name, args) {
        def m = this.@methods.find {
            it.name == name
        }
        if (m)
            m.invoke(this, args)
        else
            throw new MissingMethodException(name, Configurator, args)
    }
}
