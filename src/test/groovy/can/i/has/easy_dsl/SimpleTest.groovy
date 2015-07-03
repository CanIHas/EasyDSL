package can.i.has.easy_dsl


class SimpleTest extends GroovyTestCase{
    void testMe(){
        def instance = new TopClass(1)
        def gathered = []
        instance.configure {
            gathered << x
            gathered << foo()
            z = [1, 2]

            toConfigure {
                a = "a"
                b = 1.0
            }

            text "x"
            gathered << text("y")
            text("z")


            intList {
                gathered << target
                gathered << delegate
                number 1
                gathered << number(2)
                number 3
            }

            gathered << intList {
                number 4
            }
        }
        assert instance.z == [1, 2]
        assert instance.toConfigure == new ToConfigure("a", 1.0)
        assert instance.someList == ["x", "y", "z"]
        assert instance.intList == [1, 2, 3, 4]
        def i = 0
        assert gathered[i++] == 1
        assert gathered[i++] == instance
        assert gathered[i++] ==  "y"
        assert gathered[i++] == instance.intList
        def configurator = gathered[i++]
        assert configurator instanceof Configurator
        assert configurator.traitThis.is(instance)
        assert !configurator.scopeStack
        assert gathered[i++] == 2
        assert gathered[i++] == instance.intList
    }
}
