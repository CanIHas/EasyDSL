# EasyDSL

So, you need a DSL.
Usually you will use it to set up some objects, which then will be used in some process.
For example, you could need a DSL for simple address book, that would look like this:

    addressBook {
        owner "Hank Venture"
        person {
            name "Brock"
            surname "Samson"
        }
        person {
            name "Dean"
            surname "Venture"
            family = true
        }
    }

In you application you would obtain some AddressBook object and analyze it, for example by looking through personas.

It is easily doable with EasyDSL.

First, you define and
