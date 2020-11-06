# Gateway Device Application (Connected Devices)

## Lab Module 07


### Description
  - Install and configure Mosquitto MQTT Broker for your platform	
  - Create / edit module MqttClientConnector
  - Create the callback infrastructure for MqttClientConnector
  - Create the publish and subscribe methods for MqttClientConnector
  - Connect MqttClientConnector to DeviceDataManager

What does your implementation do? 
In this lab module, the goal is to integrate the MQTT connection/ connector on GDA so that the messages are communicated between the CDA and GDA using MQTT broker. The module MqttClientConnector uses the APIs connect, subscribe, publish, unsubscribe, disconnect from the message broker. For GDA, the incoming messages are Sensor data from the CDA and the outgoing messages are Actuator commands. Once the Sensor data is received, if threshold value is crossed, then the respective Actuator Data command to be published to the MQTT Broker is triggered 



How does your implementation work?
 - GDA App is run, 
 - DeviceDataManager initializes the MQTT client connection to the broker depending on the configurations 
 - First, the MQTT broker (installed in Windows using a exe and run in the background)
 - Once the message is received by the Device Data manager from the broker to the subscribed topic, the actuator command is published to the same subscribed topic 
 - Once the StopManager() is triggered, then the client connection to the MQTT broker is disconnected and closed

### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter07

### UML Design Diagram(s)

![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/uml/lab7_GDA.png?raw=true)

### Wireshark PCAP capture Snap(s)
 #### - CONNECT
 
 #### - CONNACK
 
 #### - PUBLISH
 
 #### - PUBACK
 
 #### - PUBREC
 
 #### - PUBREL
 
 #### - PUBCOMP
 
 #### - SUBSCRIBE
 
 #### - SUBACK
 
 #### - UNSUBSCRIBE
 
 #### - UNSUBACK
 
 #### - PINGREQ
 
 #### - PINGRESP
 
 #### - DISCONNECT
 

### Unit Tests Executed
 - All Unit tests under part01
 - All Unit tests under part02

### Integration Tests Executed

 -  ./src/test/java/programmingtheiot/part03/integration/MqttClientConnectorTest



EOF.
