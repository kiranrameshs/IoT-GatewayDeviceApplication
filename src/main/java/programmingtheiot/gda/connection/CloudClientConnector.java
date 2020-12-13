//dileep
/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import java.util.Properties;
import programmingtheiot.data.DataUtil;

/**
 * Shell representation of class for student implementation.
 *
 */
public class CloudClientConnector implements ICloudClient
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CloudClientConnector.class.getName());
	
	// private var's
	private String topicPrefix = "";
	private MqttClientConnector mqttClient = null;
	private IDataMessageListener dataMsgListener = null;
	private int qosLevel = 1;
	// constructors
	
	/**
	 * Default.
	 * this.topicPrefix = this.topicPrefix.toLowerCase();
	 * Depending on the cloud service, the topic names may or may not begin with a "/", so this code
	 * should be updated according to the cloud service provider's topic naming conventions
	 */
	public CloudClientConnector()
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.topicPrefix =
			configUtil.getProperty(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.BASE_TOPIC_KEY);
		_Logger.info("this.topicPrefix: " + this.topicPrefix);
		if (topicPrefix == null) {
			topicPrefix = "/";
		} else {
			if (! topicPrefix.endsWith("/")) {
				topicPrefix += "/";
			}
		}
	}
	
	
	// public methods
	/**
	 * Connect the client and set the Data message listener if the client is not connected yet
	 */
	@Override
	public boolean connectClient()
	{
		if (this.mqttClient == null) {
			this.mqttClient = new MqttClientConnector(true);
			this.mqttClient.setDataMessageListener(this.dataMsgListener);
			this.mqttClient.connectClient();
			return this.mqttClient.isConnected();
		}else {
			if(this.mqttClient.isConnected()) {
				return true;
			}
			return false;
		}
	}

	/**
	 * disconnect from the broker after checking if the client is still connected
	 */
	@Override
	public boolean disconnectClient()
	{
		if(this.mqttClient != null && this.mqttClient.isConnected()) {
			this.mqttClient.disconnectClient();
			return true;
		}
		return false;
	}

	public boolean isConnected()
	{
		return false;
	}
	
	/**
	 * Set the Data message listener implemented on the device data manager
	 */
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		_Logger.info("SetDataMessageListener");
		if(listener != null) {
			this.dataMsgListener = listener;
		}
		return false;
	}

	/**
	 * Method to form the sensor data message and form all the args required by the pubmish message 
	 * cpuData, memory Data etc from the GDA
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data)
	{
		if (resource != null && data != null) {
			String payload = DataUtil.getInstance().sensorDataToJson(data);
			return publishMessageToCloud(resource, data.getName(), payload);
		}
		return false;
	}

	/**
	 * Method to form the sensor data message and form all the args required by the pubmish message 
	 * cpuData, memory Data etc from the GDA
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data)
	{
		if (resource != null && data != null) {
			SensorData cpuData = new SensorData();
			cpuData.setName(ConfigConst.CPU_UTIL_NAME);
			cpuData.setValue(data.getCpuUtilization());
			boolean cpuDataSuccess = sendEdgeDataToCloud(resource, cpuData);
			if (! cpuDataSuccess) {
				_Logger.warning("Failed to send CPU utilization data to cloud service.");
			}
			SensorData memData = new SensorData();
			memData.setName(ConfigConst.MEM_UTIL_NAME);
			memData.setValue(data.getMemoryUtilization());
			boolean memDataSuccess = sendEdgeDataToCloud(resource, memData);
			if (! memDataSuccess) {
				_Logger.warning("Failed to send memory utilization data to cloud service.");
			}
			return (cpuDataSuccess == memDataSuccess);
		}
		return false;
	}

	/**
	 * subscribe to the cloud topics to recieve the actuator command message from cloud if the event is 
	 * triggered based on the sensor data
	 */
	@Override
	public boolean subscribeToEdgeEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		String topicName = null;
		if (this.mqttClient.isConnected()) {
			topicName = createTopicName(resource);
			this.mqttClient.subscribeToTopic(topicName, this.qosLevel);
			success = true;
		} else {
			_Logger.warning("Subscription methods only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		return success;
	}

	/**
	 * Unsubscribe from the topic of the cloud broker using the create topic method and mqtt client connector 
	 */
	@Override
	public boolean unsubscribeFromEdgeEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		String topicName = null;
		if (this.mqttClient.isConnected()) {
			topicName = createTopicName(resource);
			this.mqttClient.unsubscribeFromTopic(topicName);
			success = true;
		} else {
			_Logger.warning("Unsubscribe method only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		return success;
	}
	
	
	// private methods
	/**
	 * Create a topic name using the device name and the resource type from the resoureEnum
	 * @param resource
	 * @return
	 */
	private String createTopicName(ResourceNameEnum resource)
	{
		return (this.topicPrefix + resource.getDeviceName() + "/" + resource.getResourceType()).toLowerCase();
	}
	
	/**
	 * Publish the mesaage to cloud using cloud client parameters after connecting to the broker
	 * @param resource
	 * @param itemName
	 * @param payload
	 * @return
	 */
	public boolean publishMessageToCloud(ResourceNameEnum resource, String itemName, String payload)
	{
		String topicName = createTopicName(resource) + "-" + itemName;
		try {
			_Logger.finest("Publishing payload value(s) to Ubidots: " + topicName);
			if(mqttClient == null) {
				this.mqttClient = new MqttClientConnector(true);
				this.mqttClient.setDataMessageListener(this.dataMsgListener);
				this.mqttClient.connectClient();
			}
			this.mqttClient.publishMessage(topicName, payload.getBytes(), this.qosLevel);
			return true;
		} catch (Exception e) { 
			_Logger.warning("Failed to publish message to Ubidots: " + topicName);
		}
		return false;
	}
}