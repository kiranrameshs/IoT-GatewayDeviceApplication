/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

/**
 * Shell representation of class for student implementation.
 *
 */
public class ActuatorData extends BaseIotData implements Serializable
{
	// static
	public static final int DEFAULT_COMMAND = 0;
	public static final int COMMAND_OFF = DEFAULT_COMMAND;
	public static final int COMMAND_ON = 1;
	
	// private var's
    private float val;
    private int command;
    
	// constructors
	/**
	 * Default.
	 */
	public ActuatorData()
	{
		super();
		this.val = 0.0f;
		this.command=DEFAULT_COMMAND;
	}
	
	// public methods
	/**
	 * Set command (ON/OFF) of Actuator Data.
	 */
	public int getCommand()
	{
		return this.command;
	}
	/**
	 * Get Value of Actuator Data..
	 */
	public float getValue()
	{
		return this.val;
	}
	/**
	 * Set command (ON/OFF) of Actuator Data..
	 */
	public void setCommand(int command)
	{
		this.command = command;
	}
	/**
	 * Set Value of Actuator Data.
	 */
	public void setValue(float val)
	{
		this.val = val;
	}
	/**
	 * Default.
	 */
	public void updateData(ActuatorData data)
	{
		super.setName(data.getName());
		super.setStateData(data.getStateData());
		super.setStatusCode(data.getStatusCode());
		this.setValue(data.getValue());
		this.setCommand(data.getCommand());
	}

	protected void handleUpdateData(BaseIotData data)
	{
		
	}
	
	
	
}
