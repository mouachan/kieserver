# kieserver

## Custom kieserver PoC

The project is organized as maven project, consisting of the following projects:

* kieserver/ruleFlight: kjar for the *hrule.drl* set of rules.
* kieserver/customKieserver: an implemntation of a kieserver with a custom extension to test alias feature on *7.0.0-SNAPSHOT version*


### Description
Build 2 versions of kjar :
- version 1.0.0 : set the customer reduction to 12
- version 2.0.0 : set the customer reduction to 13

rule "ruleUsingMkt"

when
    c : Customer( frequent == true )
    Flight( "MKT1" memberOf matchMap ) from c.flight // if you remove from it works!
then
   //for version 1.0.0 set reduction to 12
     //c.setReduction(12);
    //for version 2.0.0 set reduction to 13
    c.setReduction(13);
end

When a container is created with a higher and an existing alias, kiserver will use the latest container version to run rules.    
#### ruleFlight
uncomment the line //c.setReduction(12); on src/main/resource/hrule.drl
set the version on pom.xml to 1.0.0
build the kjar version:
```
cd ruleFlight/
```
```
mvn clean install
```
comment the line    c.setReduction(12); on src/main/resource/hrule.drl
Uncomment the line  //c.setReduction(13); on src/main/resource/hrule.drl
set the version on pom.xml to 2.0.0
build the kjar version:
```
cd ruleFlight/
```
```
mvn clean install
```

#### customKieserver

Extension : http://mswiderski.blogspot.fr/2015/12/kie-server-extend-existing-server.html
Policy : http://mswiderski.blogspot.fr/2016_11_01_archive.html (not yet implemented)
Fact.java : a Generic fact
*KieServerApi.java* : set up kieserver properties, instanciate a new kieserver, create/update containers using alias feature.
*CustomKieServerExtension.java* : implentation of kieserver extension based on drools extension (all other extension are disabled), that provides rules capabilites (inserting facts, fire rules).
*CustomKieContainerCommandServiceImpl.java* : allow to create a batch commands from Fact object (mapped to the object using FactType), each fact is inserted using InsertObjectCommand, the last command is a FireAllRulesCommand, , can be used to implement protobuf feature.
*KieServerApiTest.java* : assert that kieserver will use latest version using alias feature.  

### Next step

Add an implemntation of protobuf and IPC transport
Add a custom policy (to update and remove containers)  
