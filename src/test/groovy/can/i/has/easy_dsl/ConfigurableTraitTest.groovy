package can.i.has.easy_dsl


class ConfigurableTraitTest extends GroovyTestCase {

    TopClass instance

    void setUp(){
        instance = new TopClass(1, "a")
    }

    static final List chosenList = [1, 2]

    void testAccess(){
        def xCopy;
        def fooResult;
        instance.configure {
            xCopy = x
            z = chosenList
//            fooResult  = foo()
             toBuild {
                 x= 1
                 y= 2
             }

            toConfigure {
                a = "a1"
                b = 1
                toConfigure {
                    a = "a2"
                    b = 2
                }
            }
        }
        assert xCopy == 1
        assert instance.z == chosenList
        assert instance.toBuild == new ToBuild(1, 2)
        assert instance.toConfigure == new ToConfigure("a1", 1, new ToConfigure("a2", 2, null))
//        assert fooResult == instance
    }

}
