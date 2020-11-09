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
 ![CONNECT](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/CONNECT.PNG?raw=true) 
 #### - CONNACK
 ![CONNACK](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/CONNACK.PNG?raw=true) 
 #### - PUBLISH
 ![PUBLISH](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/PUBLISH.PNG?raw=true) 
 #### - PUBACK
 ![PUBACK](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/PUBACK.PNG?raw=true)  
 #### - PUBREC
 ![PUBREC](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/PUBREC.PNG?raw=true)  
 #### - PUBREL
 ![PUBREL](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/PUBREL.PNG?raw=true)  
 #### - PUBCOMP
 ![PUBCOMP](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/PUBCOMP.PNG?raw=true)  
 #### - SUBSCRIBE
 ![SUBSCRIBE](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/SUBSCRIBE.PNG?raw=true)  
 #### - SUBACK
 ![SUBACK](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/SUBACK.PNG?raw=true)  
 #### - UNSUBSCRIBE
 ![UNSUBSCRIBE](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/UNSUBSCRIBE.PNG?raw=true)  
 #### - UNSUBACK
 ![UNSUBACK](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/UNSUBACK.PNG?raw=true)  
 #### - PINGREQ
 ![PINGREQ](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/PINGREQ.PNG?raw=true)  
 #### - PINGRESP
 ![PINGRESP](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/PINGRESP.PNG?raw=true)  
 #### - DISCONNECT
 ![DISCONNECT](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter07/pcap/DISCONNECT.PNG?raw=true) 

### Unit Tests Executed
 - All Unit tests under part01
 - All Unit tests under part02

### Integration Tests Executed

 -  ./src/test/java/programmingtheiot/part03/integration/MqttClientConnectorTest



EOF.
