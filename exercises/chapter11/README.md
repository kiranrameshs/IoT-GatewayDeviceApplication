# Gateway Device Application (Connected Devices)

## Lab Module 11


### Description
  - Setup and configure your cloud service environment
  - Update the MqttClientConnector with additional features
  - Create / edit the ICloudClient interface
  - Create / edit module CloudClientConnector
  - Implement the data capture and event triggering logic between the GDA and cloud service

What does your implementation do? 
The main goal of this lab module is to integrate the GDA with the cloud services, in this case ubidots. Once the connection setup is complete, MQTT Client Connector loads its configuration parameters from a different section of the PiotConfig.props configuration file and uses a package-scoped class or sub-class to directly invoke publish, subscribe, and unsubscribe functions. Using CloudClientConnector, MQTTClientConnector is used to handle all the messaging tasks like subscribe, publish, connect and disconnect. Capture, collect, and store sensor data from the CDA and system performance data from both the CDA and GDA. Analyze the sensor data and trigger an LED actuation event based on thresholds. The final outcome of this implementation would be to connect the CDA to the GDA and the GDA to the cloud service and send data from the CDA to the GDA and the GDA to the cloud service. Send at least one actuation event from the cloud service to the GDA based on the data received by the GDA from the CDA. Use the cloud service actuation event received by the GDA to trigger an actuation event on the CDA.

How does your implementation work?
Setup the ubidots connection parameters such as API token for authorization and the root certificates. Once the configuration in Cloud.GatewayService is enabled, the connection setup is complete. CloudClientConnector implements the ICloudClient and does the following tasks of subscribe, publish, connect and disconnect to cloud services via MQTT. Subscribe to the LED actuation event topic that the cloud service will use for publishing its actuation events using CloudMqttClientConnector. On receipt of the LED actuation event, do the following:
Pass the message with its payload to DeviceDataManager, create an ActuatorData instance with the LED actuator type and name applied, set the command and message / state. Pass it to the CDA's actuator command topic

### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter11

### UML Design Diagram(s)

![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter11/uml/lab11_GDA.png?raw=true)


### Unit Tests Executed
 - NA

### Integration Tests Executed

 - ./src/test/java/programmingtheiot/part03/integration MqttClientConnectorTest
 - ./src/test/java/programmingtheiot/part03/integration CloudClientConnectorTest
 
### Performance test

 - src/test/java/programmingtheiot/part03/integration/connection MqttClientPerformanceTest.java



EOF.