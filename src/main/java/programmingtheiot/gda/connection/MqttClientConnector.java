/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
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
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public MqttClientConnector()
	{
		super();
		ConfigUtil configUtil = ConfigUtil.getInstance();
		this.host = configUtil.getProperty(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
		this.port = configUtil.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
		this.brokerKeepAlive = configUtil.getInteger(ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		this.clientID = MqttClient.generateClientId();
		this.persistence = new MemoryPersistence();
		this.connOpts = new MqttConnectOptions();
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		this.connOpts.setCleanSession(false);
		this.connOpts.setAutomaticReconnect(true);
		this.brokerAddr = this.protocol + "://" + this.host + ":" + this.port;
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

	public boolean isConnected()
	{
		if(this.mqttClient.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean publishMessage(ResourceNameEnum topicName, String msg, int qos)
	{
		if(qos < 0 || qos > 2) {
			qos = DEFAULT_QOS;
		}
		_Logger.info("Publish Message");
		String topic = topicName.toString();
		_Logger.info("topic is "+topic);
		MqttMessage message = new MqttMessage(msg.getBytes(StandardCharsets.UTF_8));
		try {
			this.mqttClient.publish(topic, message);
			return true;
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}     
		return false;
	}

	@Override
	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos) 
	{
		if(qos < 0 || qos > 2) {
			qos = DEFAULT_QOS;
		}
		_Logger.info("Subscribe to topic");
		String topic = topicName.toString();
		_Logger.info("topic is "+topic);
		try {
			this.mqttClient.subscribe(topic, qos);
			return true;
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		_Logger.info("Unsubscribe from topic");
		String topic = topicName.toString();
		_Logger.info("topic is "+topic);
		try {
			this.mqttClient.unsubscribe(topic);
			return true;
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		_Logger.info("Data Listener");
		return false;
	}
	
	// callbacks
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.info("Connect Complete");
	}

	@Override
	public void connectionLost(Throwable t)
	{
		_Logger.info("Connection Lost");
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		_Logger.info("Delivery Complete");
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
		// TODO: implement this
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
		// TODO: implement this
	}
}
