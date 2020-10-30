/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import programmingtheiot.common.ConfigConst;

/**
 * Convenience wrapper to store system state data, including location
 * information, action command, state data and a list of the following
 * data items:
 * <p>SystemPerformanceData
 * <p>SensorData
 * 
 */
public class SystemStateData extends BaseIotData implements Serializable
{
	// static
	
	public static final int NO_ACTION = 0;
	public static final int REBOOT_SYSTEM_ACTION = 1;
	public static final int GET_SYSTEM_STATE_ACTION = 2;
	
	public static final String DEFAULT_LOCATION = ConfigConst.NOT_SET;
	
	// private var's
	
    private String location = DEFAULT_LOCATION;
    
    private int actionCmd = NO_ACTION;
    
    private List<SystemPerformanceData> sysPerfDataList = null;
    private List<SensorData> sensorDataList = null;
    
    
    
	// constructors
	
	public SystemStateData()
	{
		super();
		sysPerfDataList = new ArrayList<>();
		sensorDataList = new ArrayList<>();
	}
	
	
	// public methods
	
	public boolean addSensorData(SensorData data)
	{
		if(this.sensorDataList == null || data == null) {
			return false;
		}
		sensorDataList.add(data);
		return true;
		
	}
	
	public boolean addSystemPerformanceData(SystemPerformanceData data)
	{
		if(this.sysPerfDataList == null || data == null) {
			return false;
		}
		sysPerfDataList.add(data);
		return true;
	}
	
	public int getActionCommand()
	{
		return this.actionCmd;
	}
	
	public String getLocation()
	{
		return this.location;
	}
	
	public List<SensorData> getSensorDataList()
	{
		return this.sensorDataList;
	}
	
	public List<SystemPerformanceData> getSystemPerformanceDataList()
	{
		return this.sysPerfDataList;
	}
	
	public void setActionCommand(int actionCmd)
	{
		this.actionCmd = actionCmd;
	}
	
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	public void updateData(SystemStateData data)
	{
		
		super.setName(data.getName());
		super.setStateData(data.getStateData());
		super.setStatusCode(data.getStatusCode());
		this.setActionCommand(data.getActionCommand());
		this.setLocation(data.getLocation());
	}
	
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleToString()
	 */
	protected String handleToString()
	{
		return "";
	}
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
	}
	
}
