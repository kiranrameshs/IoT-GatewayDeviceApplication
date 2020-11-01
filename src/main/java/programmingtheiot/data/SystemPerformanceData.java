/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

/**
 * Shell representation of class for student implementation.
 *
 */
public class SystemPerformanceData extends BaseIotData implements Serializable
{
	// static
	
	
	// private var's
	private float cpuUtilization;
	private float memoryUtilization;
	private float diskUtilization;
    
	// constructors
	
	public SystemPerformanceData()
	{
		super();
		cpuUtilization = 0.0f;
		memoryUtilization  = 0.0f;
		diskUtilization  = 0.0f;
	}
	
	
	// public methods
	
	public float getCpuUtilization()
	{
		return this.cpuUtilization;
	}
	
	public float getDiskUtilization()
	{
		return this.diskUtilization;
	}
	
	public float getMemoryUtilization()
	{
		return this.memoryUtilization;
	}
	
	public void setCpuUtilization(float val)
	{
		this.cpuUtilization = val;
	}
	
	public void setDiskUtilization(float val)
	{
		this.diskUtilization = val;
	}
	
	public void setMemoryUtilization(float val)
	{
		this.memoryUtilization = val;
	}
	/**
	 * Called by superClass handleUpdate
	 * @param SystemPerformanceData
	 */
	public void updateData(SystemPerformanceData data)
	{
		
		super.setName(data.getName());
		super.setStateData(data.getStateData());
		super.setStatusCode(data.getStatusCode());
		this.setCpuUtilization(data.getCpuUtilization());
		this.setDiskUtilization(data.getDiskUtilization());
		this.setMemoryUtilization(data.getMemoryUtilization());
	}
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleToString()
	 */
	protected String handleToString()
	{
		return null;
	}
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
	}
	
}
