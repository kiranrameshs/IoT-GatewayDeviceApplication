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
	 * 
	 */
	public ActuatorData()
	{
		super();
		val = 0.0f;
		command = 0;
	}
	
	
	// public methods
	
	public int getCommand()
	{
		return this.command;
	}
	
	public float getValue()
	{
		return this.val;
	}
	
	public void setCommand(int command)
	{
		this.command = command;
	}
	
	public void setValue(float val)
	{
		this.val = val;
	}
	
	public void updateData(ActuatorData data)
	{
		super.setName(data.getName());
		super.setStateData(data.getStateData());
		super.setStatusCode(data.getStatusCode());
		this.setValue(data.getValue());
		this.setCommand(data.getCommand());
	}
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		
	}
	
}
