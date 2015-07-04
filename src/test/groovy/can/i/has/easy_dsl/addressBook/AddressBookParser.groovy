package can.i.has.easy_dsl.addressBook

import can.i.has.easy_dsl.Utils
import can.i.has.easy_dsl.api.field.Configure

class AddressBookParser {
    @Configure
    AddressBook addressBook

    static AddressBook parse(Closure closure){
        return Utils.configure(new AddressBookParser(), closure).addressBook
    }
}
