package can.i.has.easy_dsl

import can.i.has.gast.Templatable

//@Templatable(name = "MyAnn")
@interface SomeAnn {
    String value() default ""
    int x()
}