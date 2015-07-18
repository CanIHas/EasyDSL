package can.i.has.easy_dsl

import can.i.has.gast.ExampleAnn

@ExampleAnn(str="X", i=1, closure = {return 42})
class SomeClass {

    static void main(String... args){}
}
