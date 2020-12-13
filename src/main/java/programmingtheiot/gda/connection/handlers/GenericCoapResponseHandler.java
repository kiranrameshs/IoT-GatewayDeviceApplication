package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.OptionSet;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.gda.connection.CoapClientConnector;

public class GenericCoapResponseHandler implements CoapHandler  {
	//Logger
	private static final Logger _Logger =
			Logger.getLogger(CoapClientConnector.class.getName());
	
	private IDataMessageListener dataMsgListener;

	//Constructor for GenericCoapResponseHandler
	public GenericCoapResponseHandler(IDataMessageListener dataMsgListener) {
		this.dataMsgListener=dataMsgListener;
	}
	
	//overriding onLoad
	@Override
	/**
	 * Check if the coAp response is null else get the options on load
	 */
	public void onLoad(CoapResponse response)
	{
		if (response != null) {
			OptionSet options = response.getOptions();
			if (this.dataMsgListener != null) {
			}
		} else {
			_Logger.warning("No CoAP response to process. Response is null.");
		}
	}

	//overriding onError
	@Override
	public void onError()
	{
		_Logger.warning("Error processing CoAP response. Ignoring.");
	}
	
}