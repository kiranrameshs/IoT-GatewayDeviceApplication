# Gateway Device Application (Connected Devices)

## Lab Module 09


### Description
  - Create module CoapClientConnector
  - Add a custom response handler
  - Add Discovery functionality to CoapClientConnector
  - Add GET, PUT, POST, DELETE functionality to CoapClientConnector

What does your implementation do? 
This implementation is for creating a coApClient on the GDA. The connector creates the URL to the coAp server and has methods to generate get,put,post and delete methods


How does your implementation work?
The client connector, when initialized creates the coAP server address and calls the intiConnections method which will create the coApClient object using the CoapClient class of californium. Then methods are provided to generate get,put,post and delete methods. In these methods, we set the URI, call the coApHandler with the help of GenericCoapResponseHandler and call the respective methods. To run the connector, we must first run the coApServer on the GDA and then run the client connector.

### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter09

### UML Design Diagram(s)

![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter09/uml/lab9_GDA.png?raw=true)


### Unit Tests Executed
 - NA

### Integration Tests Executed

 -  ./src/test/java/programmingtheiot/part03/integration/connection/CoapClientConnectorTest



EOF.
