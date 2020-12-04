package programmingtheiot.part03.integration.connection;

import static org.junit.Assert.*;

import org.junit.Test;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.WebLink;
import org.junit.Test;

import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemStateData;
import programmingtheiot.gda.connection.CoapClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;

public class CoapClientConnectorTest {

	public static final int DEFAULT_TIMEOUT = 5 ; 
	private CoapClientConnector coapClient = new CoapClientConnector();
	@Test
	public void testConnectAndDiscover()
	{
//		this.coapClient = new CoapClientConnector();
		assertTrue(this.coapClient.sendDiscoveryRequest(DEFAULT_TIMEOUT));

		// NOTE: If you are using a custom asynchronous discovery, include a brief wait here
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	@Test
	public void testGetRequestCon()
	{
		assertTrue(this.coapClient.sendGetRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, DEFAULT_TIMEOUT));
	}
		
	@Test
	public void testGetRequestNon()
	{
		assertTrue(this.coapClient.sendGetRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, DEFAULT_TIMEOUT));
	}
	
	@Test
	public void testPutRequestCon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPutRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, ssdJson, DEFAULT_TIMEOUT));
	}
		
	@Test
	public void testPutRequestNon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPutRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, ssdJson, DEFAULT_TIMEOUT));
	}
	
	@Test
	public void testPostRequestCon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPostRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, ssdJson, DEFAULT_TIMEOUT));
	}
		
	@Test
	public void testPostRequestNon()
	{
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setActionCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		assertTrue(this.coapClient.sendPostRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, ssdJson, DEFAULT_TIMEOUT));
	}
	
	@Test
	public void testDeleteRequestCon()
	{
		assertTrue(this.coapClient.sendDeleteRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, DEFAULT_TIMEOUT));
	}
		
	@Test
	public void testDeleteRequestNon()
	{
		assertTrue(this.coapClient.sendDeleteRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, DEFAULT_TIMEOUT));
	}
	

}