# Gateway Device Application (Connected Devices)

## Lab Module 02


### Description
  - Update GDA
  - Update SystemPerformanceManager module
  - Connect SystemPerformanceManager to GatewayDeviceApp
  - Update BaseSystemUtilTask
  - Create the SystemCpuUtilTask module and implement the functionality to retrieve CPU utilization
  - Create the SystemMemUtilTask module and implement the functionality to retrieve JVM memory utilization

What does your implementation do? 
In this lab module, SystemPerformanceManager was implemented and connected to the GDA. This module provides the System CPU Utilization values and System Memory Utilization values during runtime. Meanwhile, to verify the implementation, SystemPerformanceManagerTest was updated to run the jobs using ScheduledExecutorService, ScheduledFuture on different threads

How does your implementation work?
When the GDA app is run, SystemPerformanceManager is called to start and stop in the app's start / stop methods. It has an instance of SystemCpuUtilTask and SystemMemUtilTask which are inherited from BaseSystemUtilTask. This base class has a method getTelemetryValue which calls a template called getSystemUtil which are implemnted in the child classes. The child classes uses the library java.lang.management.ManagementFactory and gets the values ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() and ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() 
The SystemPerformanceManagerTest runs the test with poll time of 30 seconds using the jobs called inside the startManager() and stopManager() methods

### Code Repository and Branch

URL: https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/tree/chapter02

### UML Design Diagram(s)

![GDA](https://github.com/NU-CSYE6530-Fall2020/gateway-device-app-kiran-ramesh-s/blob/chapter02/uml/lab2_GDA.png?raw=true)

### Unit Tests Executed
-  
ConfigUtilTest (7/7)
SystemCpuUtilTaskTest (1/1)
SystemMemUtilTaskTest (1/1)

### Integration Tests Executed

GatewayDeviceAppTest (1/1)
SystemPerformanceManagerTest (1/1)



EOF.
