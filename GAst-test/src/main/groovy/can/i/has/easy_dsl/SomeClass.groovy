package can.i.has.easy_dsl

import can.i.has.gast.ExampleAnn

@ExampleAnn(str="X", i=3, closure = {return 42})
class SomeClass {

    static void main(String... args){
        println new SomeClass().showYourPossibilities("XYZ")
    }
}
