package can.i.has.easy_dsl.experiment

def c = new Configurator()

c.methods = [
    new Mymethod("foo"),
    new Mymethod("bar")
]

c.foo()
c.foo("a")
c.foo("a", 1)
c.bar("x") {
    return 0
}

