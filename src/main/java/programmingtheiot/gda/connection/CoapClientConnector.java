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

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.DataUtil;
import programmingtheiot.gda.connection.handlers.GenericCoapResponseHandler;

/**
 * Shell representation of class for student implementation.
 *
 */
public class CoapClientConnector implements IRequestResponseClient
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(CoapClientConnector.class.getName());
	
	// params
	private String     protocol;
	private String     host;
	private int        port;
	private String     serverAddr;
	private CoapClient clientConn;
	private IDataMessageListener dataMsgListener;
	
	// constructors
	
	/**
	 * Default.
	 * All config data will be loaded from the config file.
	 */
	public CoapClientConnector()
	{
		ConfigUtil config = ConfigUtil.getInstance();
		this.host = config.getProperty(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
		if (config.getBoolean(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.ENABLE_CRYPT_KEY)) {
			this.protocol = ConfigConst.DEFAULT_COAP_SECURE_PROTOCOL;
			this.port     = config.getInteger(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_COAP_SECURE_PORT);
		} else {
			this.protocol = ConfigConst.DEFAULT_COAP_PROTOCOL;
			this.port     = config.getInteger(ConfigConst.COAP_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_COAP_PORT);
		}
		this.serverAddr = this.protocol + "://" + this.host + ":" + this.port;
		initClient();
		_Logger.info("Using URL for server conn: " + this.serverAddr);
	}
		
	/**
	 * Constructor.
	 * 
	 * @param host
	 * @param isSecure
	 * @param enableConfirmedMsgs
	 */
	public CoapClientConnector(String host, boolean isSecure, boolean enableConfirmedMsgs)
	{
	}
	
	// public methods
	/**
	 * Use Generic CoAP Response Handler to send discover Request to a URI
	 */
	@Override
	public boolean sendDiscoveryRequest(int timeout)
	{
		this.clientConn.setURI("/.well-known/core");
		GenericCoapResponseHandler responseHandler = new GenericCoapResponseHandler(this.dataMsgListener);
		this.clientConn.get(responseHandler);
		return true;
	}

	/**
	 * Handling the generation of delete request
	 * @param resource
	 * @param enableCON
	 * @param timeout
	 * @return
	 */
	public boolean sendDeleteRequest(ResourceNameEnum resource, boolean enableCON, int timeout)
	{
		CoapResponse response = null;
		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}
		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());
		CoapHandler responseHandler = new GenericCoapResponseHandler(this.dataMsgListener);
		this.clientConn.delete(responseHandler);
		return true;
	}

	/**
	 * handling the generation of get request
	 * @param resource
	 * @param enableCON
	 * @param timeout
	 * @return
	 */
	public boolean sendGetRequest(ResourceNameEnum resource, boolean enableCON, int timeout)
	{
		CoapResponse response = null;
		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}
		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());
		CoapHandler responseHandler = new GenericCoapResponseHandler(this.dataMsgListener);
		this.clientConn.get(responseHandler);
		return true;
	}

	/**
	 * handling the generation of post request
	 * @param resource
	 * @param enableCON
	 * @param payload
	 * @param timeout
	 * @return
	 */
	public boolean sendPostRequest(ResourceNameEnum resource, boolean enableCON, String payload, int timeout)
	{
		CoapResponse response = null;
		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}
		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());
		CoapHandler responseHandler = new GenericCoapResponseHandler(this.dataMsgListener);
		this.clientConn.post(responseHandler, payload, MediaTypeRegistry.TEXT_PLAIN);
		return true;
	}

	/**
	 * handling the generation of put request
	 * @param resource
	 * @param enableCON
	 * @param payload
	 * @param timeout
	 * @return
	 */
	public boolean sendPutRequest(ResourceNameEnum resource, boolean enableCON, String payload, int timeout)
	{
		CoapResponse response = null;
		if (enableCON) {
			this.clientConn.useCONs();
		} else {
			this.clientConn.useNONs();
		}
		this.clientConn.setURI(this.serverAddr + "/" + resource.getResourceName());
		CoapHandler responseHandler = new GenericCoapResponseHandler(this.dataMsgListener);
		this.clientConn.put(responseHandler, payload, MediaTypeRegistry.TEXT_PLAIN);
		return true;
	}
	
	/**
	 * setter for the data message listener
	 */
	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;
		return true;
	}

	/**
	 * Start the Observer
	 */
	@Override
	public boolean startObserver(ResourceNameEnum resource, int ttl)
	{
		return false;
	}

	/**
	 * Stop the Observer
	 */
	@Override
	public boolean stopObserver(int timeout)
	{
		return false;
	}
	
	// private methods
	// 
	/**
	 * Starting the coAp client connector by calling the CoapClient class of californium
	 */
	private void initClient() {
		try {
			this.clientConn = new CoapClient(this.serverAddr);
			_Logger.info("Created client connection to server / resource: " + this.serverAddr);
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to connect to broker: " + (this.clientConn != null ? this.clientConn.getURI() : this.serverAddr), e);
		}
	}

	@Override
	public boolean sendDeleteRequest(ResourceNameEnum resource, int timeout) {
		return false;
	}

	@Override
	public boolean sendGetRequest(ResourceNameEnum resource, int timeout) {
		return false;
	}

	@Override
	public boolean sendPostRequest(ResourceNameEnum resource, String payload, int timeout) {
		return false;
	}

	@Override
	public boolean sendPutRequest(ResourceNameEnum resource, String payload, int timeout) {
		return false;
	}
	
}