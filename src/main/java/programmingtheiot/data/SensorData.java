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
public class SensorData extends BaseIotData implements Serializable
{
	// static
	
	public static final int DEFAULT_SENSOR_TYPE = 0;
	

	// private var's
	private float value;
	private int sensorType;
	
    
	// constructors
	
	public SensorData()
	{
		super();
		value=0.0f;
		sensorType = DEFAULT_SENSOR_TYPE;
	}
	
	public SensorData(int sensorType)
	{
		super();
		value=0.0f;
		this.sensorType = sensorType;
	}
	
	
	// public methods
	
	public float getValue()
	{
		return this.value;
	}
	
	public void setValue(float val)
	{
		this.value = val;
	}
	/**
	 * Called by superClass handleUpdate
	 * @param SensorData
	 */
	public void updateData(SensorData data)
	{
		super.setName(data.getName());
		super.setStateData(data.getStateData());
		super.setStatusCode(data.getStatusCode());
		this.setValue(data.getValue());
	}
	
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		
	}
	
}
