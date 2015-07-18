package can.i.has.gast.utils

import groovy.transform.Canonical

@Canonical
class CompilationLogger {
    Class clazz

    void info(String msg){
        System.out.println ">> ${clazz.name} :: $msg"
    }

    void info(msg){
        info "$msg"
    }

    void warn(String msg){
        System.err.println ">> ${clazz.name} :: $msg"
    }

    void warn(msg){
        warn "$msg"
    }
}
