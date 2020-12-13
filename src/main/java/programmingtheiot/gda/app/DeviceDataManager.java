/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.app;

import java.io.IOException;
import java.net.MalformedURLException;
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
import programmingtheiot.gda.connection.IPersistenceListener;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.IRequestResponseClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;
import programmingtheiot.gda.connection.SmtpClientConnector;
import programmingtheiot.gda.system.SystemPerformanceManager;


/**
 * Shell representation of class for student implementation.
 *
 */
public class DeviceDataManager implements IDataMessageListener
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManager.class.getName());
	
	// private var's
	
	private boolean enableMqttClient = false;
	private boolean enableCoapServer = false;
	private boolean enableCloudClient = false;
	private boolean enableSmtpClient = false;
	private boolean enablePersistenceClient = false;
	
	//private boolean enablePersistenceClient = false;
	
	private IPubSubClient mqttClient = null;
	private ICloudClient cloudClient = null;
	private IPersistenceClient persistenceClient = null;
	private IRequestResponseClient smtpClient = null;
	private CoapServerGateway coapServer = null;
	private boolean isPersistentClientActive = false;
	
	private SystemPerformanceManager sysPerfManager;
	private int humiditySensorFloor = 0;
	private int humiditySensorCeiling = 0;
	
	// constructors
	/**
	 * initializing the boolean variables as well
	 */
	public DeviceDataManager()
	{
		super();
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.enableMqttClient  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
		this.enableCoapServer  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
		this.enableCloudClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
		this.enableSmtpClient  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SMTP_CLIENT_KEY);
		this.enablePersistenceClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
		this.humiditySensorFloor = configUtil.getInteger(ConfigConst.GATEWAY_DEVICE, ConfigConst.HUMIDITY_SENSOR_FLOOR);
		this.humiditySensorCeiling = configUtil.getInteger(ConfigConst.GATEWAY_DEVICE, ConfigConst.HUMIDITY_SENSOR_CEILING);
		initConnections();
		this.sysPerfManager = new SystemPerformanceManager();
	}
	
	/**
	 * Constructor
	 * @param enableMqttClient
	 * @param enableCoapClient
	 * @param enableCloudClient
	 * @param enableSmtpClient
	 * @param enablePersistenceClient
	 */
	public DeviceDataManager(
		boolean enableMqttClient,
		boolean enableCoapClient,
		boolean enableCloudClient,
		boolean enableSmtpClient,
		boolean enablePersistenceClient)
	{
		super();
		
		
		this.enableMqttClient  = enableMqttClient;
		this.enableCoapServer  = enableCoapClient;
		this.enableCloudClient = enableCloudClient;
		this.enableSmtpClient  = enableSmtpClient;
		this.enablePersistenceClient = enablePersistenceClient;
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.sysPerfManager = new SystemPerformanceManager();
		this.humiditySensorFloor = configUtil.getInteger(ConfigConst.GATEWAY_DEVICE, ConfigConst.HUMIDITY_SENSOR_FLOOR);
		this.humiditySensorCeiling = configUtil.getInteger(ConfigConst.GATEWAY_DEVICE, ConfigConst.HUMIDITY_SENSOR_CEILING);
		initConnections();
	}
	
	
	// public methods
	 /**
	 * Handles Actuator command response by storing data 
	 */
		@Override
		public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
		{
			_Logger.info("handleActuatorCommandResponse has been called");
			try {
				if(enablePersistenceClient)
				{
					this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
					return true;
				}	
			}
			catch(Exception ex) {
				_Logger.info("Exception occured: " + ex.getMessage());
			}
			
			return false;
		}
		
		/**
		 * handles incoming message by coverting message to Actuator is System state data
		 */
		@Override
		public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
		{
			_Logger.info("handleIncomingMessage has been called");
			DataUtil dataUtil = DataUtil.getInstance();
			try {
				ActuatorData ad = dataUtil.jsonToActuatorData(msg);
				handleIncomingDataAnalysis(resourceName,ad);
			}
			catch(Exception ex) {
				SystemStateData sd = dataUtil.jsonToSystemStateData(msg);
				handleIncomingDataAnalysis(resourceName,sd);
			}
			return false;
		}
		
		/**
		 * Handles sensor message and store the data
		**/ 
		@Override
		public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
		{
			_Logger.info("Handle Sensor Message");
			DataUtil dataUtil = DataUtil.getInstance();
			try {
				this.cloudClient.sendEdgeDataToCloud(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, data);
				if(data.getSensorType() == 1 && (data.getValue() <this.humiditySensorFloor || data.getValue() > this.humiditySensorCeiling )) {
					_Logger.info("Sending Actuator Command Message to CDA");
					ActuatorData ad = new ActuatorData();
					ad.setCommand(1);
					ad.setValue(32);
					ad.setActuatorType(2);
					ad.setStateData(data.getStateData());
					String jsonData = dataUtil.actuatorDataToJson(ad);
					_Logger.info(" Actuator Command Message is "+jsonData);
					this.mqttClient.connectClient();
					this.mqttClient.publishMessage(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, jsonData, 0);
					handleUpstreamTransmission(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, jsonData);
					if(isPersistentClientActive)
					{
						this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
					}
					return true;
				}
			}
			catch(Exception ex) {
				_Logger.info("Exception occured: " + ex.getMessage());
			}
			return false;
		}
		/**
		 * handles system performance data by storing it and converting to json
		 * 
		 */
		@Override
		public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
		{
			_Logger.info("handleSystemPerformanceMessage has been called");
			DataUtil dataUtil = DataUtil.getInstance();
			try {
				if(enablePersistenceClient)
				{
					this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
					return true;
				}
					
			}
			catch(Exception ex) {
				_Logger.info("Exception occured: " + ex.getMessage());
			}
			return false;
		}
		
		/**
		 * starts data device manager.
		 * we check the boolean values of each protocol and decide whether to enable that connection
		 * 
		 */
		public void startManager()
		{
			_Logger.info("Data Device Manager has started");
			this.sysPerfManager.startManager();
			if(this.enableCloudClient) {
				if(this.cloudClient.connectClient()) {
					_Logger.info("connected to cloudClientConnector successfully");
				}else {
					_Logger.info("failed to connect to cloudClientConnector");
				}
			}
			if(this.enableMqttClient) {
				if(this.mqttClient.connectClient()) {
					_Logger.info("connected to mqttClientConnector successfully");
				}else {
					_Logger.info("failed to connect to mqttClientConnector");
				}
			}
			if(this.enablePersistenceClient) {
				if(this.persistenceClient.connectClient()) {
					_Logger.info("connected to redisPersistenceAdapter successfully");
					enablePersistenceClient = true;
				}else {
					_Logger.info("failed to connect to redisPersistenceAdapter");
				}
			}
			if(this.enableCoapServer) {
				if(this.coapServer.startServer()) {
					_Logger.info("connected to coapServerGateway successfully");
				}else {
					_Logger.info("failed to connect to coapServerGateway");
				}
			}
		}
		
		/**
		 * stops data device manager.
		 * we check the boolean values of each protocol and decide whether to disable that connection
		 */
		public void stopManager()
		{
			_Logger.info("Data Device Manager has stopped");
			this.sysPerfManager.stopManager();
			if(this.enableCloudClient) {
				if(this.cloudClient.disconnectClient()) {
					_Logger.info("disconnected from cloudClientConnector successfully");
				}else {
					_Logger.info("failed to disconnect from cloudClientConnector");
				}
			}
			if(this.enableMqttClient) {
				if(this.mqttClient.disconnectClient()) {
					_Logger.info("disconnected from mqttClientConnector successfully");
				}else {
					_Logger.info("failed to disconnect from mqttClientConnector");
				}
			}
			if(this.enablePersistenceClient) {
				if(this.persistenceClient.disconnectClient()) {
					_Logger.info("disconnected from redisPersistenceAdapter successfully");
					enablePersistenceClient = false;
				}else {
					_Logger.info("failed to disconnect from redisPersistenceAdapter");
				}
			}
			if(this.enableCoapServer) {
				if(this.coapServer.stopServer()) {
					_Logger.info("disconnected from coapServerGateway successfully");
				}else {
					_Logger.info("failed to disconnect from coapServerGateway");
				}
			}
		}

		
		// private methods
		
		/**
		 * Initializes the enabled connections. This will NOT start them, but only create the
		 * instances that will be used in the {@link #startManager() and #stopManager()) methods.
		 * 
		 */
		private void initConnections()
		{
		if(this.enableCloudClient) {
			this.cloudClient = new CloudClientConnector();
			this.cloudClient.setDataMessageListener(this);
		}
		
		this.coapServer = new CoapServerGateway();
		this.smtpClient = new SmtpClientConnector();
		if(this.enableMqttClient)
		{
			this.mqttClient = new MqttClientConnector();
			this.mqttClient.setDataMessageListener(this);
		}
			
		this.persistenceClient = new RedisPersistenceAdapter();
	}
	
		/**
		 * handles IncomingDataAnalysis for actuator
		 * @param resourceName
		 * @param data
		 */
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, ActuatorData data){
		_Logger.fine("handleIncomingDataAnalysis with Actuator data has been called");
		DataUtil dataUtil = DataUtil.getInstance();
		data.setActuatorType(100);
		data.setCommand(1);
		String jsonActuatorData = dataUtil.actuatorDataToJson(data);
		this.mqttClient.connectClient();
		this.mqttClient.publishMessage(ResourceNameEnum.CDA_DISPLAY_RESPONSE_RESOURCE, jsonActuatorData, 1);
	}

	/**
	 * handles IncomingDataAnalysis for SystemStateData
	 * @param resourceName
	 * @param data
	 */
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName,SystemStateData data){
		_Logger.fine("handleIncomingDataAnalysis with SystemStateData has been called");
	}
	
	private void handleUpstreamTransmission(ResourceNameEnum resourceName, String msg) {
		_Logger.fine("handleUpstreamTransmission has been called");
	}
	
}