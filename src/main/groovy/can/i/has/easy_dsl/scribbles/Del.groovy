package can.i.has.easy_dsl.scribbles

class Del {
    def methodMissing(String name, args){
        println("method missing")
        println(name)
        println "args"
        args.each this.&println
        println "-----"
        if (args.toList().last() instanceof Closure) {
            def c = args.toList().last()
            c.delegate = this
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c.call()
        }
    }

    def propertyMissing(String name){
        println "get $name"
    }
    def propertyMissing(String name, val){
        println "set $name = $val"
    }
}
