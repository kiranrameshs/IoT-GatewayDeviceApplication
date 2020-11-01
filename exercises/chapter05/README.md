# Gateway Device Application (Connected Devices)

## Lab Module 05


### Description
  - Create/update data containers for sensor and actuator data
  - Create/edit module - DataUtil
  - Add functionality to BaseSystemUtilTask implementation
  - Create/edit the module - DeviceDataManager
  - Connect DeviceDataManager to GatewayDeviceApp

What does your implementation do? 
In this lab module, Data containers for actuator and sensor data are implemented (Getters and setters with updateData func()). DataUtil takes care of data conversions from actuator, sensor data types to JSON to send the data out and vice versa of converting JSON data to actuator/sensor data after receiving it from CDA. SystemStateData is a combination of sensor and System performance data. DeviceDataManager is implemented to take the trigger from the App and start all the Managers and connection clients. 

How does your implementation work?
 - GDA App is run, 
 - DeviceDataManager is called to start and stop in the app's start/stop methods. 
 - This handles SysPerfManager and all the connection client initialization
 - This also handles the messages to be received and sent using the DataUtil module (Uses module Gson for conversions)
 - Once the StopManager() is triggered, then the other managers are stopped and the connection clients are closed

### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter05

### UML Design Diagram(s)

![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter05/uml/lab5_GDA.png?raw=true)

### Unit Tests Executed
 - ./data/ActuatorDataTest
 - ./data/SensorDataTest
 - ./data/SystemPerformanceDataTest
 - ./data/SystemStateDataTest
 - ./data/DataUtilTest
 - All Unit tests under part01

### Integration Tests Executed

 - ./data/DataIntegrationTest
 - ./app/DeviceDataManagerNoCommsTest
 - ./app/GatewayDeviceAppTest



EOF.
