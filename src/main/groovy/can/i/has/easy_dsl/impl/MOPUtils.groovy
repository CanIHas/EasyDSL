package can.i.has.easy_dsl.impl


class MOPUtils {
    static void setProperty(obj, String name, val){
        if (obj.metaClass)
            obj.metaClass.setProperty(obj, name, val)
        else
            obj."$name" = val
    }

    static def getProperty(obj, String name){
        if (obj.metaClass)
            obj.metaClass.getProperty(obj, name)
        else
            obj."$name"
    }
}
