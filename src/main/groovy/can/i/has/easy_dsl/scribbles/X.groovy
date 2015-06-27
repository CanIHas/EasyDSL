package can.i.has.easy_dsl.scribbles

//def c = {
//    a(1, b: 2){
//        c = 3
//    }
//    println "========================"
//    a(1){
//        b = 2
//    }
//    println "========================"
//    a {
//        b = 1
//    }
//    println "========================"
//    foo(1, 2, 3)
//}
//
//c.delegate = new Del()
//c.resolveStrategy = Closure.DELEGATE_FIRST
//c.call()

class C {


//    static def d( Map m = [:], val){
//        d(m, val, {})
//    }

    static def d( Map m, val = null, Closure closure){
        println(m)
        println val
        closure.call()
    }
}

C.d { println "d" }
C.d(1) { println "d" }
C.d(1, e: 2) { println "d" }