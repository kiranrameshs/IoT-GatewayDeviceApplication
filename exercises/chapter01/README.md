# Gateway Device Application (Connected Devices)

## Lab Module 01

### Description

What does your implementation do? 
As part of first assignment, the task was to read Chapter 1 of Programming the Internet of Things, then setup the development environment for the GDA i.e. the Java components. 
First step was to initialise git in the respective folder and clone the the GDA repository. Then import the project from the folder on to Eclipse IDE. Run the GatewayDeviceApp.java from programmingtheiot.gda.app folder
GDA starts successfully as seen from the log prints from the console

How does your implementation work?
When the GatewayDeviceApp.java from programmingtheiot.gda.app folder is run, this gets an instance of the ConfigUtil.java through initConfig(configFile) which loads the configurations from DEFAULT_CONFIG_FILE_NAME (default in this case). The GDA starts successfully as seen from the log prints in the console.


### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter01

### UML Design Diagram(s)
![CDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter01/uml/lab1_CDA.png?raw=true)
![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter01/uml/lab1_GDA.png?raw=true)



### Unit Tests Executed

GatewayDeviceAppTest (1/1)
SystemPerformanceManagerTest (1/1)
ConfigUtilTest (7/7)

### Integration Tests Executed

