/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import javax.net.ssl.SSLSocketFactory;
import programmingtheiot.common.SimpleCertManagementUtil;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class MqttClientConnector implements IPubSubClient, MqttCallbackExtended
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(MqttClientConnector.class.getName());
	private static final  int DEFAULT_QOS = 1;
	
	// params
	private int port;
	private int brokerKeepAlive;
	private MqttConnectOptions connOpts;
	private MemoryPersistence persistence;
	private String clientID;
	private String brokerAddr;
	private String host;
	private String protocol = "tcp";
	private MqttClient mqttClient;
	private boolean enableEncryption;
	private String pemFileName;
	private IDataMessageListener dataMsgListener = null;

	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
//	public MqttClientConnector()
//	{
//		super();
//		ConfigUtil configUtil = ConfigUtil.getInstance();
//		this.host = configUtil.getProperty(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
//		this.port = configUtil.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
//		this.brokerKeepAlive = configUtil.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
//		this.clientID = MqttClient.generateClientId();
//		this.persistence = new MemoryPersistence();
//		this.connOpts = new MqttConnectOptions();
//		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
//		this.connOpts.setCleanSession(false);
//		this.connOpts.setAutomaticReconnect(true);
//		this.brokerAddr = this.protocol + "://" + this.host + ":" + this.port;
//	}
	
	public MqttClientConnector()
	{
		super();
		
		initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
		
	}
	
	
	// public methods
	@Override
	public boolean connectClient()
	{
		if (this.mqttClient == null) {
		    try {
		    	_Logger.info("MQTT client instance not available, initializing client");
				this.mqttClient = new MqttClient(this.brokerAddr, this.clientID, this.persistence);
				this.mqttClient.setCallback(this);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		if (! this.mqttClient.isConnected()) {
		    try {
		    	_Logger.info("Connecting to MQTT broker");
				this.mqttClient.connect(this.connOpts);
				_Logger.info("Connected to MQTT broker");
				return true;
			}  catch (MqttException e) {
				e.printStackTrace();
				_Logger.info("Error connecting to MQTT broker");
			}
		}
		else {
			_Logger.info("Already Connected to MQTT broker");
			return false;
		}
		return false;
	}

	/**
	 * Disconnect from broker after unsubscribe
	 * Return true if disconnection successful
	 */
	@Override
	public boolean disconnectClient()
	{
		if (this.mqttClient.isConnected()) {
		    try {
				this.mqttClient.disconnect();
				_Logger.info("Disconnected from MQTT broker");
				return true;
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		else {
			_Logger.info("Already disconnected");
			return false;
		}
		return true;
	}
	
	/**
	 * Check if the client is connected to the Broker based on the conn state.
	 * Return true if connected
	 */
	public boolean isConnected()
	{
		if(this.mqttClient.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Check if the topic name is valid else return false
	 * Publish message
	 * Return true if publish successful
	 */
	@Override
	public boolean publishMessage(ResourceNameEnum topicName, String msg, int qos)
	{
		if(qos < 0 || qos > 2) {
			qos = DEFAULT_QOS;
		}
//		_Logger.info("Publish Message");
		String topic = topicName.toString();
		byte[] message = msg.getBytes(StandardCharsets.UTF_8);
		try {
			this.mqttClient.publish(topic, message, qos, true );
//			_Logger.info("Publish successfull");
			return true;
		} catch (MqttPersistenceException e) {
//			_Logger.info("Publish failed");
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}     
		return false;
	}

	/**
	 * Check if the topic name is valid else return false
	 * Subscribe to a topic
	 * Return true if subscription successful
	 */
	@Override
	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos) 
	{
		if(qos < 0 || qos > 2) {
			qos = DEFAULT_QOS;
		}
		_Logger.info("Subscribe topic");
		String topic = topicName.toString();
		try {
			this.mqttClient.subscribe(topic, qos);
			_Logger.info("Subscription to "+topic+" successfull");
			return true;
		} catch (MqttException e) {
			_Logger.info("Subscription failed");
			e.printStackTrace();
			
		}
		return false;
	}

	/**
	 * Unsubscribe to topic previously subscribed topic
	 * Return true if unsubscribe successful
	 */
	@Override
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		_Logger.info("Unsubscribe topic");
		String topic = topicName.toString();
		try {
			this.mqttClient.unsubscribe(topic);
			_Logger.info("Unsubscribe successfull");
			return true;
		} catch (MqttException e) {
			_Logger.info("Unsubscribe failed");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
	        if (listener != null) {
	            this.dataMsgListener = listener;
	            return true;
	        }
		
	        return false;
	}
	
	// callbacks
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
		
		int qos = 1;
		
		// Option 2
		try {
			this.mqttClient.subscribe(
				ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
				qos,
				new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));
			this.mqttClient.subscribe(
					ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(),
					qos,
					new SensorResponseMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
			this.mqttClient.subscribe(
					ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(),
					qos,
					new SysPerfResponseMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to CDA actuator response topic.");
		}
	}

	@Override
	public void connectionLost(Throwable t)
	{
		_Logger.info("Connection Lost");
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
//		_Logger.info("Delivery Complete");
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception
	{
		_Logger.info("Message Arrived");
	}

	
	// private methods
	
	/**
	 * Called by the constructor to set the MQTT client parameters to be used for the connection.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initClientParameters(String configSectionName)
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.host =
			configUtil.getProperty(
				configSectionName, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
		this.port =
			configUtil.getInteger(
				configSectionName, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
		this.brokerKeepAlive =
			configUtil.getInteger(
				configSectionName, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		this.enableEncryption =
			configUtil.getBoolean(
				configSectionName, ConfigConst.ENABLE_CRYPT_KEY);
		this.pemFileName =
			configUtil.getProperty(
				configSectionName, ConfigConst.CERT_FILE_KEY);
		
		// Paho Java client requires a client ID
		this.clientID = MqttClient.generateClientId();
		
		// these are specific to the MQTT connection which will be used during connect
		this.persistence = new MemoryPersistence();
		this.connOpts    = new MqttConnectOptions();
		
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
//		this.connOpts.setCleanSession(this.useCleanSession);
//		this.connOpts.setAutomaticReconnect(this.enableAutoReconnect);
		
		// if encryption is enabled, try to load and apply the cert(s)
		if (this.enableEncryption) {
			initSecureConnectionParameters(configSectionName);
		}
		
		// if there's a credential file, try to load and apply them
		if (configUtil.hasProperty(configSectionName, ConfigConst.CRED_FILE_KEY)) {
			initCredentialConnectionParameters(configSectionName);
		}
		
		// NOTE: URL does not have a protocol handler for "tcp" or "ssl",
		// so construct the URL manually
		this.brokerAddr  = this.protocol + "://" + this.host + ":" + this.port;
		
		_Logger.info("Using URL for broker conn: " + this.brokerAddr);
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to load credentials.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initCredentialConnectionParameters(String configSectionName)
	{
		// TODO: implement this
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to enable encryption.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initSecureConnectionParameters(String configSectionName)
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		try {
			_Logger.info("Configuring TLS...");
			
			if (this.pemFileName != null) {
				File file = new File(this.pemFileName);
				
				if (file.exists()) {
					_Logger.info("PEM file valid. Using secure connection: " + this.pemFileName);
				} else {
					this.enableEncryption = false;
					
					_Logger.log(Level.WARNING, "PEM file invalid. Using insecure connection: " + pemFileName, new Exception());
					
					return;
				}
			}
			
			SSLSocketFactory sslFactory =
				SimpleCertManagementUtil.getInstance().loadCertificate(this.pemFileName);
			
			this.connOpts.setSocketFactory(sslFactory);
			
			// override current config parameters
			this.port =
				configUtil.getInteger(
					configSectionName, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_MQTT_SECURE_PORT);
			
			this.protocol = ConfigConst.DEFAULT_MQTT_SECURE_PROTOCOL;
			
			_Logger.info("TLS enabled.");
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to initialize secure MQTT connection. Using insecure connection.", e);
			
			this.enableEncryption = false;
		}
	}
	
	private class ActuatorResponseMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		ActuatorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			try {
				ActuatorData actuatorData =
					DataUtil.getInstance().jsonToActuatorData(new String(message.getPayload()));
				
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleActuatorCommandResponse(resource, actuatorData);
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to ActuatorData.");
			}
		}
		
	}
	
	private class SensorResponseMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		SensorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			try {
				SensorData sensorData =
					DataUtil.getInstance().jsonToSensorData(new String(message.getPayload()));
				
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleSensorMessage(resource, sensorData);
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to ActuatorData.");
			}
		}
	}
	
	private class SysPerfResponseMessageListener implements IMqttMessageListener
	{
		private ResourceNameEnum resource = null;
		private IDataMessageListener dataMsgListener = null;
		
		SysPerfResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
		{
			this.resource = resource;
			this.dataMsgListener = dataMsgListener;
		}
		
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			try {
				SystemPerformanceData sysPerfData =
					DataUtil.getInstance().jsonToSystemPerformanceData(new String(message.getPayload()));
				
				if (this.dataMsgListener != null) {
					this.dataMsgListener.handleSystemPerformanceMessage(resource, sysPerfData);
				}
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to ActuatorData.");
			}
		}
	}
	
	
}
