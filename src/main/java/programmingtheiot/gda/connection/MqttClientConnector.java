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
	// static variables
	
	private static final Logger _Logger = Logger.getLogger(MqttClientConnector.class.getName());
	private static final  int DEFAULT_QOS = 1;
	
	// parameters
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
	private boolean useCloudGatewayConfig = false;
	// constructors
	
	/**
	 * Default Constructor for the mqttClient
	 * 
	 */	
	public MqttClientConnector()
	{
		this(false);
	}
	
	/**
	 * Parameterized Constructor for the mqttClient. parameters is for cloud connection
	 * 
	 */	
	public MqttClientConnector(boolean useCloudGatewayConfig)
	{
		super();
		
		this.useCloudGatewayConfig = useCloudGatewayConfig;
		
		if (useCloudGatewayConfig) {
			initClientParameters(ConfigConst.CLOUD_GATEWAY_SERVICE);
		} else {
			initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
		}
	}
	
	// public methods
	
	
	@Override
	/**
	 * method to connect to the client
	 */
	public boolean connectClient()
	{
		if (this.mqttClient == null) {
		    try {
		    	_Logger.info("MQTT client instance not available, initializing client");
		    	_Logger.info("MQTT broker address: "+this.brokerAddr+ "clientID: "+this.clientID+"persistence"+this.persistence);
				this.mqttClient = new MqttClient(this.brokerAddr, this.clientID, this.persistence);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		    this.mqttClient.setCallback(this);
		}
		if (! this.mqttClient.isConnected()) {
		    try {
		    	_Logger.info("Connecting to MQTT broker");
				this.mqttClient.connect(this.connOpts);
				return true;
			}  catch (MqttException e) {
				e.printStackTrace();
			}
		}
		else {
			_Logger.info("Already Connected to MQTT broker");
			return false;
		}
		return false;
	}

	@Override
	/**
	 * method to disconnect the client
	 */
	public boolean disconnectClient()
	{
		if (this.mqttClient.isConnected()) {
		    try {
		    	_Logger.info("Disconnecting from MQTT broker");
				this.mqttClient.disconnect();
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
	 * to check if the mqttClient is connected
	 * @return
	 */
	public boolean isConnected()
	{
		if(this.mqttClient.isConnected()) {
			return true;
		}
		return false;
	}
	
	@Override
	/**
	 * method to publish a message 
	 */
	public boolean publishMessage(ResourceNameEnum topicName, String msg, int qos)
	{
		return publishMessage(topicName.getResourceName(), msg.getBytes(StandardCharsets.UTF_8), qos);
	}

	@Override
	/**
	 * method to subscribe to a topic
	 */
	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos) 
	{
		return subscribeToTopic(topicName.getResourceName(),qos);
	}

	@Override
	/**
	 * method to unsubscribe to a topic
	 */
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		return unsubscribeFromTopic(topicName.getResourceName());
	}

	@Override
	/**
	 * setter for data message listener
	 */
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
	/**
	 * logger when connection is succesful
	 * once connect is complete start the subscription process to all the required CDA and cloud topics
	 */
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
		 
		int qos = 1;
		try {
			this.mqttClient.subscribe("/v1.6/devices/constraineddevice/displaycmd");
			_Logger.log(Level.INFO, "inside connectComplete with topic name" + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
			this.mqttClient.subscribe("/v1.6/devices/gatewaydevice/mgmtstatusmsg");
			_Logger.info("MQTT subscribe to display cmd successful");
			_Logger.info("ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName()   "+ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
			this.mqttClient.subscribe(
				ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
				qos,
				new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));
			_Logger.info("MQTT subscribe to CDA ACTUATOR RESP RES successful");
			this.mqttClient.subscribe(
					ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(),
					qos,
					new SensorResponseMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
			_Logger.info("MQTT subscribe to CDA SENSOR MSG RES successful");
			this.mqttClient.subscribe(
					ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(),
					qos,
					new SysPerfResponseMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));
			_Logger.info("MQTT subscribe to CDA SYS PERF MSG RES successful");
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to CDA actuator response topic."+e.getStackTrace().toString());
		}
	}
	
	
	@Override
	/**
	 * logger when connection is lost
	 */
	public void connectionLost(Throwable t)
	{
		_Logger.info("Connection Lost");
	}
	
	@Override
	/**
	 * logger when delivery is complete
	 */
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		_Logger.info("Delivery Complete");
	}
	
	@Override
	/**
	 * 
	 */
	public void messageArrived(String topic, MqttMessage msg) throws Exception
	{
		System.out.println("MESSAGE ARRIVED WITH TOPIC: "+ topic);
		_Logger.log(Level.INFO, "Message arrived on topic" + topic + " Messgae: " + msg.toString());
		topic = topic.replace("/v1.6/devices/", "ProgrammingIoT/");
		String display = "constraineddevice/displaycmd";
		if(topic.contains(display)) {
			topic = topic.replace(display, "ConstrainedDevice/DisplayCmd");
			_Logger.info(topic);
		}
		String msgContent = new String(msg.getPayload());
		try {
			if(this.dataMsgListener != null) {
				ResourceNameEnum resource = ResourceNameEnum.getEnumFromValue(topic);
				this.dataMsgListener.handleIncomingMessage(resource, msgContent);
				
			}else {
				_Logger.info("No Data message listener on topic:" + topic);
			}
		}
		catch(Exception ex) {
			_Logger.log(Level.WARNING,"Failed to handle arrived message: " + topic);
		}
	}

	
	// private methods
	
	/**
	 * Called by the constructor to set the MQTT client parameters to be used for the connection.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 * Paho Java client requires a client ID
	 * these are specific to the MQTT connection which will be used during connect
	 * if encryption is enabled, try to load and apply the cert(s)
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
		this.clientID = MqttClient.generateClientId();
		this.persistence = new MemoryPersistence();
		this.connOpts    = new MqttConnectOptions();
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		if (this.enableEncryption) {
			initSecureConnectionParameters(configSectionName);
		}
		if (configUtil.hasProperty(configSectionName, ConfigConst.CRED_FILE_KEY)) {
			initCredentialConnectionParameters(configSectionName);
		}
		this.brokerAddr  = this.protocol + "://" + this.host + ":" + this.port ;
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
		this.connOpts.setUserName("BBFF-wYK3jgMFd1ivT1O2BskL5vKY8XXpt3");
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to enable encryption.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 * override current config parameters
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
	
	/**
	 * Actuator Response Message Listener
	 * @author kiran
	 *
	 */
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
		/**
		 * receive the message from the cloud mostly actuatordata hence forward to CDA after processing
		 */
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
	
	/**
	 * Sensor Response Message Listener
	 * @author kiran
	 *
	 */
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
		/**
		 * Receive the message from the cloud mostly actuatordata hence forward to CDA after processing
		 */
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			_Logger.warning("Message recieved is: "+ message);
			try {
				SensorData sensorData =
					DataUtil.getInstance().jsonToSensorData(new String(message.getPayload()));
				_Logger.warning("Sensor data created ");
				_Logger.warning("data message listener is: "+this.dataMsgListener);
				if (this.dataMsgListener != null) {
					_Logger.warning("datamsglistener handle message will be called");
					this.dataMsgListener.handleSensorMessage(resource, sensorData);
				}
				_Logger.warning("Message recieved is: "+ message);
			} catch (Exception e) {
				_Logger.warning("Failed to convert message payload to Sensor Data.");
			}
		}
	}
	
	/**
	 * System Performance Response Message Listener
	 * @author kiran
	 *
	 */
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
		/**
		 * receive the message from the cloud mostly actuatordata hence forward to CDA after processing
		 */
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
	
	/**
	 * Protected method to publish a message to the mqtt broker to a topic
	 * @param topic
	 * @param payload
	 * @param qos
	 * @return
	 */
	protected boolean publishMessage(String topic, byte[] payload, int qos)
	{
		MqttMessage message = new MqttMessage(payload);
		if (qos < 0 || qos > 2) {
			qos = 0;
		}
		message.setQos(qos);
		try {
			_Logger.info("Publishing message to topic: " + topic);
			
			this.mqttClient.publish(topic, message);
			return true;
		} catch (MqttPersistenceException e) {
			_Logger.warning("Persistence exception thrown when publishing to topic: " + topic);
		} catch (MqttException e) {
			_Logger.warning("MQTT exception thrown when publishing to topic: " + topic);
		}
		return false;
	}
	
	/**
	 * Protected method to subscribe to a topic
	 * @param topic
	 * @param qos
	 * @return
	 */
	protected boolean subscribeToTopic(String topic, int qos)
	{
		try {
			this.mqttClient.subscribe(topic, qos);
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to topic: " + topic);
		}
		return false;
	}
	
	/**
	 * protected method to unsubscribe to a topic
	 * @param topic
	 * @return
	 */
	protected boolean unsubscribeFromTopic(String topic)
	{
		try {
			this.mqttClient.unsubscribe(topic);
			
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to unsubscribe from topic: " + topic);
		}
		
		return false;
	}

}