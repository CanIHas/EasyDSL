# EasyDSL

## Get it!

Project is versioned without patch versions.
 
Current stable version is 0.1 - it doesn't support following example.

Current development version is 0.2-SNAPSHOT - it does.

Use following dependency:

    compile 'can.i.has:EasyDSL:<version>'

This project is hosted on bintray (for stable builds) and artifactory (for snapshots).

Use following gradle configuration:

    repositories {
        //stable builds
        maven { url 'http://dl.bintray.com/filipmalczak/maven' }
        
        //snapshots
        maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local' }
    }

### First-steps tutorial

It is available on repo as a [test case](src/test/groovy/can/i/has/easy_dsl/addressBook/Example.groovy) and package 
that is in.

### Wanted effect

So, you need a DSL.
Usually you will use it to set up some objects, which then will be used in some process.
For example, you could need a DSL for simple address book, that would look like this:

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

In you application you would obtain some `AddressBook` object and analyze it, for example by looking through personas.

It is easily doable with EasyDSL.

### Create top class

First, you define and annotate top level class as such:

> I'm adding `@Canonical` to each and every class defined here for easy comparison and representation in further example.

    @Canonical
    class AddressBook {
        @Access
        boolean personal
        
        @Configure
        Person owner
    
        @CollectScope(elementName = "person", type = Person)
        @Configure
        List<Person> contacts
    }

And thats pretty much it here. 
`@Access` annotation will generate both setter and getter in result DSL.
`@Configure` means that DSLs method owner has up to 3 arguments:
 
    owner(Map properties =[:], Person defaultValue = null, Closure configure).

It will be described further. In the end, this is what you use to get standard gradle-like hierarchy.
 
`@CollectScope` is example of modifier annotation - it doesn't generate DSL behaviour, but extends it.
It means that instead of standard case (like with @Configure above) it should generate method with field name 
(a "scope"), taking closure which is executed to create and collect list elements.
It should only be used on List fields. 

> I'm thinking about extending this annotation so that it may call custom method with single parameter.
> At this moment that method is add(), though the type isn't checked at any time, so actually
> you can use this annotation on field of any type that has add(Object arg) method or such, but with
> explicit argument class instead of Object.

Its parameters specify what will be the name and return type of `@Configure`-generated method used to create list elements.

### Create classes for lower-level elements

Next, you create `Person` class:

    @Canonical
    class Person {
        @Access
        @MethodSetter
        String name

        @Access
        @MethodSetter
        String surname

        @Access
        @MethodSetter
        String email

        @Collect(elementName = "nick", type = String)
        @MethodSetter
        List<String> nicks
    }

As shown above, there may be more than one non-modifier annotation on each field.

`@MethodSetter` annotation allows you to omit `=` sign in DSL while setting attributes.
This is actually alias for another annotation with special configuration, but that is out of scope of 
this first-steps tutorial.

`@Collect` is another modifier annotation.
It works in similiar fashion as `@CollectScope`, but it doesn't create method visualising hierarchy like `contacts` in 
the class above.

* todo: discuss Configure
 
### Wrap it up into parser

Now, we create additional class for parsing our DSL. 
It isn't necessary, but it allows "cleaner" approach for providing DSL to end-user.
  
    class AddressBookParser {
        @Configure
        AddressBook addressBook
        
        static AddressBook parse(Closure closure){
            return Utils.configure(new AddressBookParser(), closure).addressBook
        }
    }
    
Yep, that's it!

`Utils.configure` takes an object and configuration closure, then creates a `Configurator` object.
It's metaclass is expando-enhanced with methods, basing on field and method annotations.
Those methods keep track of first given object and access its fields and delegate to its methods.
Closure is called with that `Configurator` as delegate, and then configured object (first argument) is returned.

There is also a [trait](src/main/groovy/can/i/has/easy_dsl/ConfigurableTrait.groovy) that provides
lazily initiated field `Configurator configurator` assigned to `this` object.
Besides, it provides a method `configure(Closure closure)` that calls `Utils.configure` with `this` object
and closure passed as argument and returns its result. That means that parser above can be written as:
 
    class AddressBookParser implements ConfigurableTrait{
        @Configure
        AddressBook addressBook
        
        static AddressBook parse(Closure closure){
            return new AddressBookParser().configure(closure).addressBook
        }
    } 

which is a nice alternative to using `Utils` as a category (since both its methods are static).

> For those who wonder: 
> `Utils.getConfigurator` looks for objects `configurator` property and if there isn't one or it is `null`, then it 
> creates and stores it. 
> That method is used internally to recursively configure the whole object hierarchy, so you can provide you custom
> `configurator` field to add your own hand-crafted extensions of generated DSL. 


### Test it

Now you can test it, e.g. with a Groovy script:

    AddressBook book = AdressBookParser.parse {
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
     
    assert book.name == "Hanks contacts"
    assert book.owner.nicks == [ "Batguy" ]
    assert book.contacts.first().email == "samson@sphinx.secret"
    assert book.contacts.last().nicks.size() == 2
    
Profit!