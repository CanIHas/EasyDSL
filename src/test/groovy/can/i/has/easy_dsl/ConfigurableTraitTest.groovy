package can.i.has.easy_dsl


class ConfigurableTraitTest extends GroovyTestCase {

    TopClass instance

    void setUp(){
        instance = new TopClass(1, "a")
    }

    static final List chosenList = [1, 2]

    void testAccess(){
        def xCopy;
        instance.configure {
            xCopy = x
            z = chosenList
        }
        assert xCopy == 1
        assert instance.z == chosenList
    }

}
