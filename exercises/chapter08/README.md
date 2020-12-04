# Gateway Device Application (Connected Devices)

## Lab Module 08


### Description
  - Install and configure Californium Tools for your platform
  - Create / edit module CoapServerGateway
  - Create a generic resource handler
  - Update CoapServerGateway to register all required resource handlers

What does your implementation do? 
In this lab module, the goal is to implement the CoAP server. coAp servers need resource handlers to function such as GET, PUT, POST and DELETE request methods for a given resource name. So these instances are created and registered.


How does your implementation work?
The Device Data Manager can control the coAP server through start and stop methods. Once the coAp server is started, an instance of the server is created and used from the server class. The resources for the server is based on the constructor called, for example it can either be default resources or custom resources. GenericCoapResourceHandler is used for local resource implementations

### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter08

### UML Design Diagram(s)

![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter08/uml/lab8_GDA.png?raw=true)


### Unit Tests Executed
 - NA

### Integration Tests Executed

 -  ./src/test/java/programmingtheiot/part03/integration/connection/CoapServerGatewayTest



EOF.
