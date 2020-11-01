/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.util.logging.Logger;

import programmingtheiot.data.SensorData;

/**
 *
 */
public abstract class BaseSystemUtilTask
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(BaseSystemUtilTask.class.getName());
	
	
	// private
	private SensorData latestSensorData = null;
	
	// constructors
	
	public BaseSystemUtilTask()
	{
		super();
	}
	
	
	// public methods
	/**
	 * Use respective getSystemUtil methods from derived classes to generate telemetry value
	 * @return
	 */ 
	public SensorData generateTelemetry()
	{
		SensorData sd = new SensorData();
		this.latestSensorData = sd;
		this.latestSensorData.setValue(this.getSystemUtil());
		return this.latestSensorData;
	}
	
	public float getTelemetryValue()
	{
		if(this.latestSensorData == null) {
			SensorData sd = this.generateTelemetry();
			return sd.getValue();
		}
		else {
			return this.latestSensorData.getValue();
		}
		
	}
	
	
	// protected methods
	
	/**
	 * Template method definition. Sub-class will implement this to retrieve
	 * the system utilization measure.
	 * 
	 * @return float
	 */
	protected abstract float getSystemUtil();
	
}
