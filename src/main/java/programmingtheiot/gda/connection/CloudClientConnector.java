/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */

package programmingtheiot.gda.connection;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.app.DeviceDataManager;

/**
 * Shell representation of class for student implementation.
 *
 */
public class CloudClientConnector implements ICloudClient {

	private static final Logger _Logger = Logger.getLogger(DeviceDataManager.class.getName());
	private String topicPrefix = "";
	private MqttClientConnector mqttClient = null;
	private IDataMessageListener dataMsgListener = null;
	private int qosLevel = 1;

	/**
	 * CloudClientConnector constructor. Depending on the cloud service, the topic
	 * names may or may not begin with a "/", so this code should be updated
	 * according to the cloud service provider's topic naming conventions
	 * 
	 * @param args NA
	 */
	public CloudClientConnector() {
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.topicPrefix = configUtil.getProperty(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.BASE_TOPIC_KEY);
		if (topicPrefix == null) {
			topicPrefix = "/";
		} else {
			if (!topicPrefix.endsWith("/")) {
				topicPrefix += "/";
			}
		}
	}

	/**
	 * connect to the MQTT client
	 * @param args NA
	 */
	@Override
	public boolean connectClient() {
		if (this.mqttClient == null) {
			this.mqttClient = new MqttClientConnector(true);
		}
		return this.mqttClient.connectClient();
	}

	/**
	 * discconect from the mqtt client and return a true if success
	 * 
	 * @param args NA
	 */
	@Override
	public boolean disconnectClient() {
		if (this.mqttClient != null) {
			return this.mqttClient.disconnectClient();
		}
		return false;
	}

	/**
	 * publish SensorData to the cloud using mqtt client
	 * 
	 * @param args NA
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data) {
		if (resource != null && data != null) {
			String payload = DataUtil.getInstance().sensorDataToJson(data);
			return publishMessageToCloud(resource, data.getName(), payload);
		}
		return false;
	}

	/**
	 * publish SystemPerformanceData to the cloud using mqtt client
	 * 
	 * @param args NA
	 */
	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data) {
		if (resource != null && data != null) {
			SensorData cpuData = new SensorData();
			cpuData.setName(ConfigConst.CPU_UTIL_NAME);
			cpuData.setValue(data.getCpuUtilization());
			boolean cpuDataSuccess = sendEdgeDataToCloud(resource, cpuData);
			if (!cpuDataSuccess) {
				_Logger.warning("Failed to send CPU utilization data to cloud service.");
			}
			SensorData memData = new SensorData();
			memData.setName(ConfigConst.MEM_UTIL_NAME);
			memData.setValue(data.getMemoryUtilization());
			boolean memDataSuccess = sendEdgeDataToCloud(resource, memData);
			if (!memDataSuccess) {
				_Logger.warning("Failed to send memory utilization data to cloud service.");
			}
			return (cpuDataSuccess == memDataSuccess);
		}
		return false;
	}

	/**
	 * Subscribe to the Edge events
	 * 
	 * @param args NA
	 */
	@Override
	public boolean subscribeToEdgeEvents(ResourceNameEnum resource) {
		boolean success = false;
		String topicName = null;
		if (isMqttClientConnected()) {
			topicName = createTopicName(resource);
			this.mqttClient.subscribeToTopic(topicName, this.qosLevel);
			success = true;
		} else {
			_Logger.warning(
					"Subscription methods only available for MQTT. No MQTT connection to broker. Ignoring. Topic: "
							+ topicName);
		}
		return success;
	}

	/**
	 * Unsubscribe to edge events
	 * 
	 * @param args NA
	 */
	@Override
	public boolean unsubscribeFromEdgeEvents(ResourceNameEnum resource) {
		boolean success = false;
		String topicName = null;
		if (isMqttClientConnected()) {
			topicName = createTopicName(resource);
			this.mqttClient.unsubscribeFromTopic(topicName);
			success = true;
		} else {
			_Logger.warning(
					"Unsubscribe method only available for MQTT. No MQTT connection to broker. Ignoring. Topic: "
							+ topicName);
		}
		return success;
	}

	/**
	 * Check if the mqtt client is connected
	 * 
	 * @return true if connected else false
	 */
	private boolean isMqttClientConnected() {
		if (this.mqttClient.isConnected())
			return true;
		return false;
	}

	/**
	 * Connect to DataMessageListene
	 * 
	 * @param args NA
	 */
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener) {
		if (listener != null) {
			this.dataMsgListener = listener;
			return true;
		}
		return false;
	}

	// public methods
	private String createTopicName(ResourceNameEnum resource) {
		return this.topicPrefix + resource.getResourceName() + "/";// + resource.getResourceType();
	}

	private boolean publishMessageToCloud(ResourceNameEnum resource, String itemName, String payload) {
		String topicName = createTopicName(resource) + "-" + itemName;
		try {
			_Logger.finest("Publishing payload value(s) to Ubidots: " + topicName);
			this.mqttClient.publishMessage(topicName, payload.getBytes(), this.qosLevel);
			return true;
		} catch (Exception e) {
			_Logger.warning("Failed to publish message to Ubidots: " + topicName);
		}
		return false;
	}

}
