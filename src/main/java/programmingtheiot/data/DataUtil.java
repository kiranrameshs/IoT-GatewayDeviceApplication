/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;

/**
 * Data Utility class to handle sensor, actuator and Sys perf data to json and vice versa
 *
 */
public class DataUtil
{
	// static
	
	private static final DataUtil _Instance = new DataUtil();

	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return ConfigUtil
	 */
	public static final DataUtil getInstance()
	{
		return _Instance;
	}
	
	
	// private var's
	// constructors
	
	/**
	 * Default (private).
	 * 
	 */
	private DataUtil()
	{
		super();
	}
	
	
	// public methods
	/**
	 * Convert actuatorData to JSON data
	 * @param actuatorData
	 * @return JSON data
	 */
	public String actuatorDataToJson(ActuatorData actuatorData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(actuatorData);
		return jsonData;
	}
	/**
	 * Convert sensorData to JSON data
	 * @param sensorData
	 * @return JSON data
	 */
	public String sensorDataToJson(SensorData sensorData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(sensorData);
		return jsonData;
	}
	/**
	 * Convert SystemPerformanceData to JSON data
	 * @param SystemPerformanceData
	 * @return JSON data
	 */
	public String systemPerformanceDataToJson(SystemPerformanceData sysPerfData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(sysPerfData);
		return jsonData;
	}
	/**
	 * Convert SystemStateData to JSON data
	 * @param SystemStateData
	 * @return JSON data
	 */
	public String systemStateDataToJson(SystemStateData sysStateData)
	{
		Gson gson = new Gson();
		String jsonData = gson.toJson(sysStateData);
		return jsonData;
	}
	/**
	 * Convert JSON data to ActuatorData
	 * @param jsonData
	 * @return
	 */
	public ActuatorData jsonToActuatorData(String jsonData)
	{
		Gson gson = new Gson();
		ActuatorData actuatorData = gson.fromJson(jsonData, ActuatorData.class);
		return actuatorData;
	}
	/**
	 * Convert JSON data to SensorData
	 * @param jsonData
	 * @return
	 */
	public SensorData jsonToSensorData(String jsonData)
	{
		Gson gson = new Gson();
		SensorData sensorData = gson.fromJson(jsonData, SensorData.class);
		return sensorData;
	}
	/**
	 * Convert JSON data to SystemPerformanceData
	 * @param jsonData
	 * @return
	 */
	public SystemPerformanceData jsonToSystemPerformanceData(String jsonData)
	{
		Gson gson = new Gson();
		SystemPerformanceData sysPerfData = gson.fromJson(jsonData, SystemPerformanceData.class);
		return sysPerfData;
	}
	/**
	 * Convert JSON data to SystemStateData
	 * @param jsonData
	 * @return
	 */
	public SystemStateData jsonToSystemStateData(String jsonData)
	{
		Gson gson = new Gson();
		SystemStateData sysStateData = gson.fromJson(jsonData, SystemStateData.class);
		return sysStateData;
	}
	
}
