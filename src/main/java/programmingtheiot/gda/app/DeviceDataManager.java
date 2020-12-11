/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */

package programmingtheiot.gda.app;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.SystemStateData;

import programmingtheiot.gda.connection.CloudClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;
import programmingtheiot.gda.connection.ICloudClient;
import programmingtheiot.gda.connection.IPersistenceClient;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.IRequestResponseClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;
import programmingtheiot.gda.connection.SmtpClientConnector;
import programmingtheiot.gda.system.SystemPerformanceManager;
import programmingtheiot.common.ResourceNameEnum;

/**
 * Shell representation of class for student implementation.
 *
 */
public class DeviceDataManager implements IDataMessageListener {
	// static
	private static final Logger _Logger = Logger.getLogger(DeviceDataManager.class.getName());

	// private var's
	private SystemPerformanceManager sysPerfManager;
	private boolean enableMqttClient = true;
	private boolean enableCoapServer = false;
	private boolean enableCloudClient = false;
	private boolean enableSmtpClient = false;
	private boolean enablePersistenceClient = false;
	private boolean isPersistentClientActive = false;
	private IPubSubClient mqttClient = null;
	private ICloudClient cloudClient = null;
	private IPersistenceClient persistenceClient = null;
	private IRequestResponseClient smtpClient = null;
	private CoapServerGateway coapServer = null;
	private int humiditySensorFloor = 0;
	private int humiditySensorCeiling = 0;

	// constructors
	/**
	 * Initialize the predicates using the configurations in the config const file and using them initialize the connections
	 */
	public DeviceDataManager() {
		super();
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.enableMqttClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
		this.enableCoapServer = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
		this.enableCloudClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
		this.enableSmtpClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SMTP_CLIENT_KEY);
		this.enablePersistenceClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE,ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
		this.humiditySensorFloor = configUtil.getInteger(ConfigConst.GATEWAY_DEVICE, ConfigConst.HUMIDITY_SENSOR_FLOOR);
		this.humiditySensorCeiling = configUtil.getInteger(ConfigConst.GATEWAY_DEVICE,ConfigConst.HUMIDITY_SENSOR_CEILING);
		initConnections();
		this.sysPerfManager = new SystemPerformanceManager(10);
	}

	/**
	 * Overloaded constructor to pass predicates as the arg to handle a particular scenario of handling only few connections 
	 * @param enableMqttClient
	 * @param enableCoapClient
	 * @param enableCloudClient
	 * @param enableSmtpClient
	 * @param enablePersistenceClient
	 */
	public DeviceDataManager(boolean enableMqttClient, boolean enableCoapClient, boolean enableCloudClient,
			boolean enableSmtpClient, boolean enablePersistenceClient) {
		super();
		this.enableMqttClient = enableMqttClient;
		this.enableCloudClient = enableCloudClient;
		this.enableSmtpClient = enableSmtpClient;
		this.enablePersistenceClient = enablePersistenceClient;
		this.enableCoapServer = enableCoapClient;
		initConnections();
		this.sysPerfManager = new SystemPerformanceManager(10);
	}

	// public methods
	/**
	 * Handles Actuator command response by storing data if persistence client is available
	 */
	@Override
	public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data) {
		try {
			if (isPersistentClientActive) {
				this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
				return true;
			}
		} catch (Exception ex) {
			_Logger.info("Exception occured: " + ex.getMessage());
		}
		return false;
	}

	/**
	 * Handles incoming message by converting message to Actuator or System state and call Incoming Data Analysis
	 * message is the input
	 **/
	@Override
	public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg) {
		DataUtil dataUtil = DataUtil.getInstance();
		try {
			ActuatorData ad = dataUtil.jsonToActuatorData(msg);
			handleIncomingDataAnalysis(resourceName, ad);
		} catch (Exception ex) {
			SystemStateData sd = dataUtil.jsonToSystemStateData(msg);
			handleIncomingDataAnalysis(resourceName, sd);
		}
		return false;
	}

	/**
	 * Handles sensor message, check exceptional case is reached using floor, ceiling values, send an Actuator COmmand Resp if triggered, store data
	 * if persistence client is available
	 **/
	@Override
	public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data) {
		DataUtil dataUtil = DataUtil.getInstance();
		try {
			if (data.getValue() < this.humiditySensorFloor || data.getValue() > this.humiditySensorCeiling) {
				ActuatorData ad = new ActuatorData();
				ad.setCommand(1);
				ad.setStateData(data.getStateData());
				this.mqttClient.publishMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, ad.toString(), 0);
				String jsonData = dataUtil.actuatorDataToJson(ad);
				handleUpstreamTransmission(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, jsonData);
				if (isPersistentClientActive) {
					this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
				}
				return true;
			}
		} catch (Exception ex) {
			_Logger.info("Exception occured: " + ex.getMessage());
		}
		return false;
	}

	/**
	 * handles system performance data by storing it and converting to json f persistence client is available
	 */
	@Override
	public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data) {
		_Logger.info("handleSystemPerformanceMessage has been called");
		DataUtil dataUtil = DataUtil.getInstance();
		try {
			if (isPersistentClientActive) {
				this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
				return true;
			}
		} catch (Exception ex) {
			_Logger.info("Exception occured: " + ex.getMessage());
		}
		return false;
	}

	/**
	 * Starts data device manager, CloudCLient/PersistenceClient based on configurations using MQTT/CoAP 
	 */
	public void startManager() {
		_Logger.info("Data Device Manager has started");
		this.sysPerfManager.startManager();
		if (this.enableCloudClient) {
			if (this.cloudClient.connectClient()) {
				_Logger.info("connected to cloudClientConnector successfully");
			} else {
				_Logger.info("failed to connect to cloudClientConnector");
			}
		}
		if (this.enableMqttClient) {
			if (this.mqttClient.connectClient()) {
				_Logger.info("connected to mqttClientConnector successfully");
			} else {
				_Logger.info("failed to connect to mqttClientConnector");
			}
		}
		if (this.enablePersistenceClient) {
			if (this.persistenceClient.connectClient()) {
				_Logger.info("connected to redisPersistenceAdapter successfully");
				isPersistentClientActive = true;
			} else {
				_Logger.info("failed to connect to redisPersistenceAdapter");
			}
		}
		if (this.enableCoapServer) {
			if (this.coapServer.startServer()) {
				_Logger.info("connected to coapServerGateway successfully");
			} else {
				_Logger.info("failed to connect to coapServerGateway");
			}
		}
	}

	/**
	 * Stops data device manager and CloudCLient/PersistenceClient based on configurations using MQTT/CoAP  if started by the manager
	 */
	public void stopManager() {
		_Logger.info("Data Device Manager has stopped");
		this.sysPerfManager.stopManager();
		if (this.enableCloudClient) {
			if (this.cloudClient.disconnectClient()) {
				_Logger.info("disconnected from cloudClientConnector successfully");
			} else {
				_Logger.info("failed to disconnect from cloudClientConnector");
			}
		}
		if (this.enableMqttClient) {
			if (this.mqttClient.disconnectClient()) {
				_Logger.info("disconnected from mqttClientConnector successfully");
			} else {
				_Logger.info("failed to disconnect from mqttClientConnector");
			}
		}
		if (this.enablePersistenceClient) {
			if (this.persistenceClient.disconnectClient()) {
				_Logger.info("disconnected from redisPersistenceAdapter successfully");
				isPersistentClientActive = false;
			} else {
				_Logger.info("failed to disconnect from redisPersistenceAdapter");
			}
		}
		if (this.enableCoapServer) {
			if (this.coapServer.stopServer()) {
				_Logger.info("disconnected from coapServerGateway successfully");
			} else {
				_Logger.info("failed to disconnect from coapServerGateway");
			}
		}
	}

	// private methods

	/**
	 * Initializes the enabled connections. This will NOT start them, but only
	 * create the instances that will be used in the {@link #startManager() and
	 * #stopManager()) methods.
	 */
	private void initConnections() {
		this.cloudClient = new CloudClientConnector();
		this.coapServer = new CoapServerGateway();
		this.smtpClient = new SmtpClientConnector();
		this.mqttClient = new MqttClientConnector();
		this.persistenceClient = new RedisPersistenceAdapter();
	}

	/**
	 * handles IncomingDataAnalysis for actuator
	 * 
	 * @param resourceName
	 * @param data
	 */
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, ActuatorData data) {
		_Logger.fine(" Incoming Data Analysis with Actuator");
	}

	/**
	 * handles IncomingDataAnalysis for SystemStateData
	 * 
	 * @param resourceName
	 * @param data
	 */
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, SystemStateData data) {
		_Logger.fine("Incoming Data Analysis with SystemStateData");
	}

	/**
	 * handles UpstreamTransmission
	 * 
	 * @param resourceName
	 * @param data
	 */
	private void handleUpstreamTransmission(ResourceNameEnum resourceName, String msg) {
		_Logger.fine("handleUpstreamTransmission has been called");
	}
}
