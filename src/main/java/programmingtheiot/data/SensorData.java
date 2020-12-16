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
 * 
 *
 */
public class SensorData extends BaseIotData implements Serializable {
	// static

	public static final int DEFAULT_SENSOR_TYPE = 0;

	// private var's
	private float value;
	private int sensorType;

	// constructors
	/**
	 * default constructor
	 */
	public SensorData() {
		super();
		value = 0.0f;
		sensorType = DEFAULT_SENSOR_TYPE;
	}

	/**
	 * 
	 * @return the type of sensor in our case 3 sensors so an int value for the sensor is returned
	 */
	public int getSensorType() {
		return sensorType;
	}

	/**
	 * 
	 * @param sensorType set the sensor type for the data 
	 * in our case 3 sensors so an int value for the sensor is returned
	 */
	public void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}

	public SensorData(int sensorType) {
		super();
		value = 0.0f;
		this.sensorType = sensorType;
	}

	// public methods

	public float getValue() {
		return this.value;
	}

	public void setValue(float val) {
		this.value = val;
	}

	/**
	 * Called by superClass handleUpdate
	 * this updates all the values of the sensor data and passes it as type sensor data to be published to GDA
	 * @param SensorData
	 */
	public void updateData(SensorData data) {
		super.setName(data.getName());
		super.setStateData(data.getStateData());
		super.setStatusCode(data.getStatusCode());
		this.setValue(data.getValue());
	}

	protected void handleUpdateData(BaseIotData data) {

	}

}
