## Custom kieserver PoC


</br>The project is organized as maven project, consisting of the following projects:

* kieserver/ruleFlight: kjar for the *hrule.drl* set of rules.
* kieserver/customKieserver: an implemntation of a kieserver to test different features on *7.0.0-SNAPSHOT version*


### Description
</br>Build 2 versions of kjar :
- version 1.0.0 : set the customer reduction to 12
- version 2.0.0 : set the customer reduction to 13

</br>*rule "ruleUsingMkt"
</br>
</br>when
</br>    c : Customer( frequent == true )
</br>    Flight( "MKT1" memberOf matchMap ) from c.flight // if you remove from it works!
</br>then
</br>   //for version 1.0.0 set reduction to 12
</br>     //c.setReduction(12);
</br>    //for version 2.0.0 set reduction to 13
</br>    c.setReduction(13);
</br>end*

#### ruleFlight
</br>uncomment the line //c.setReduction(12); on src/main/resource/hrule.drl
</br>set the version on pom.xml to 1.0.0
</br>build the kjar version:
```
cd ruleFlight/
```
```
mvn clean install
```
</br>comment the line    c.setReduction(12); on src/main/resource/hrule.drl
</br>uncomment the line  //c.setReduction(13); on src/main/resource/hrule.drl
</br>set the version on pom.xml to 2.0.0
</br>build the kjar version:
```
cd ruleFlight/
```
```
mvn clean install
```

#### customKieserver

</br>*Fact.java* : a generic fact
</br>*KieServerApi.java* : set up kieserver properties, instanciate a new kieserver, create/update containers using alias feature.
</br>*CustomKieServerExtension.java* : implementation of kieserver extension based on drools extension (all other extension are disabled), that provides rules capabilities (inserting facts, fire rules), can be used to implement a new transport.
</br>*CustomKieContainerCommandServiceImpl.java* : implementation of a custom serialization, a generic fact is mapped to the object using drools declared type api(reflection), each fact is inserted using InsertObjectCommand, the last command is a FireAllRulesCommand.
</br>*KieServerApiTest.java* : assert that kieserver will use latest version using alias feature.  

### Next step

</br>Implemntation of protobuf and IPC transport
</br>Implementation of a custom policy (to update and remove containers)  

### Documentation
</br>Extension : http://mswiderski.blogspot.fr/2015/12/kie-server-extend-existing-server.html
</br>Policy : http://mswiderski.blogspot.fr/2016_11_01_archive.html (not yet implemented)

