# Gateway Device Application (Connected Devices)

## Lab Module 10


### Description
  - Update MqttClientConnector to support encrypted connections to the broker
  - Update MqttClientConnector to send received messages to an IDataMessageListener instance 
  - Update DeviceDataManager to handle analyze messages from the CDA and take an appropriate action
  - Update sensor and actuator data containers to set the appropriate device name
  - Test the integration between the CDA and GDA using MQTT

What does your implementation do? 
First, we test the performance for each of the QoS levels: QoS 0, QoS 1, and QoS 2 with MAX_TEST_RUNS = 10000 by disabling unnecessary logging
 - How long was the connect / disconnect? -> 11.9 ms
 - For the QoS tests, include the percentage difference between each QoS level, with QoS 0 tests as the baseline -> QOS1 -25& and QOS2 +57% 
 - Which ran fastest? -> QOS0
 - Which ran slowest? -> QOS2

This implementation is mainly about handling both upstream and downstream messages on GDA such as sensor, sysperf from downstream and actuatorData as upstream. 
MqttClientConnector supports TLS encrypted connections with the broker. GDA subscribes to CDA's topic and handles SensorData, ActuatorData and SystemPerfData. 
Implementation also includes analysis of all 3 types of messages.

How does your implementation work?
MqttClientConnector uses SSLSocketFactory to initilaize the connection with TLS encryption. Then the subscribe functionality handles the received messages and converts to respective data types.
GDA checks if incoming SensorData (Humidity) crosses these threshold values. Once the threshold is crossed, create an ActuatorData message with the command set to turn on/off the humidifier with right actuator type and the command. 
Once the ActuatorData message is created, we publish it to the CDA's ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE topic along with ActuatorData (state data).
For systemperf and systemstatedata containers to get appropriate device names, they are added as configuration in ConfigConst, SystemStateData and SystemPerformanceData recieve this as an argument in the constructor of their respective classes and is used in generateTelemetry()


### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter10

### UML Design Diagram(s)

![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter10/uml/lab10_GDA.png?raw=true)

 ### Performance test Snap(s)

 #### - CoAP
 ![CoAP](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter10/pcap/CoAPTest.PNG?raw=true) 
 #### - MQTT
 ![GETRESPONSE](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter10/pcap/MQTTTest.PNG?raw=true) 

### Unit Tests Executed
 - NA

### Integration Tests Executed

 -  ./src/test/java/programmingtheiot/part03/integration/connection/CoapClientConnectorTest
 
### Performance test

 - src/test/java/programmingtheiot/part03/integration/connection MqttClientPerformanceTest.java



EOF.
