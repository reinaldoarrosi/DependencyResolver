#Dependency Resolver

DependencyResolver is a simple service locator where you can bind your interfaces and abstract classes to a concrete implementation and retrieve instances later. It supports bind parameters and named binding.

## Installation

- Clone this repository
- Import project into Eclipse
- Export as .JAR file
- Add a reference to the exported jar
- Have fun!

## Usage
To bind your classes you must create a DependencyModule and override the **load** method
    
    public class DefaultModule extends DependencyModule {
        
        @Override
        public void load() {
            bind(Soldier.class, USASoldier.class);
            bind(Soldier.class, NATOSoldier.class).withConstructorArguments("Canadian", 25).named("NATO");
            bind(Weapon.class, Colt.class).named("Colt");
            bind(Weapon.class, Uzi.class).named("Uzi");
        }
    }
    
After you've created the module you can register it through this *static method call*
    
    Dependencies.addModule(new DefaultModule());
    
And now you can resolve your dependencies this way
    
    // Uzi weapon
    Weapon weapon1 = Dependencies.forClass(Weapon.class).named("Uzi").resolve();
    
    // Colt weapon
    Weapon weapon2 = Dependencies.forClass(Weapon.class).named("Colt").resolve();
    
    
    //USASoldier: Weapon = Colt, Nationality = "" and Age = 0
    Soldier soldier = Dependencies.forClass(Soldier.class)
        .withConstructorArguments(weapon2)
        .resolve();
    
    //NATOSoldier: Weapon = Uzi, Nationality = Canadian and Age = 25
    Soldier soldier = Dependencies.forClass(Soldier.class)
        .withConstructorArguments(weapon1)
        .resolve();
        
Note that you can specify arguments when binding classes and also when resolving a dependency. 

It's also possible to add multiple DependencyModules. Each module will contribute with its' bindings. Bindings with the same type and name will be overwritten (the last one being the valid one).