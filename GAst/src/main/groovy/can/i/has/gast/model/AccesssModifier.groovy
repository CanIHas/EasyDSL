package can.i.has.gast.model


enum AccesssModifier {
    PUBLIC("public"),
    PROTECTED("protected"),
    PRIVATE("private"),
    PACKAGE("@groovy.transform.PackageScope")

    final String code;

    AccesssModifier(String code) {
        this.code = code
    }


}