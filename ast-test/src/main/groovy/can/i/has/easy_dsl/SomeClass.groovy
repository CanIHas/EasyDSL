package can.i.has.easy_dsl

@Ann
class SomeClass {
    static void main(String[] args){
        println(new SomeClass().getExtender().message())
    }
}
