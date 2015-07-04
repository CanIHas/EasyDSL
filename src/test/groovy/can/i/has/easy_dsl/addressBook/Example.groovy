package can.i.has.easy_dsl.addressBook

class Example extends GroovyTestCase {
    AddressBook book

    void setUp(){
        book = AddressBookParser.parse {
            addressBook(name: "Hanks contacts") {
                personal = true
                owner {
                    name "Hank"
                    surname "Venture"
                    nick "Batguy"
                }
                contacts {
                    person(surname: "Samson") {
                        name "Brock"
                        email = "samson@sphinx.secret"
                    }
                    person {
                        name "Dean"
                        surname "Venture"
                        email = "dean_o@venture.industries.com"
                        nick "Dean-O"
                        nick "bro"
                    }
                }
            }
        }
    }

    void testAddressBook(){
        assert book.name == "Hanks contacts"
        assert book.owner.nicks == [ "Batguy" ]
        assert book.contacts.first().email == "samson@sphinx.secret"
        assert book.contacts.last().nicks.size() == 2
    }
}
