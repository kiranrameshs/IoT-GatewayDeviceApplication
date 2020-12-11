/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public abstract class BaseIotData implements Serializable
{
	// static
	
	public static final float DEFAULT_VAL = 0.0f;
	public static final int DEFAULT_STATUS = 0;
	
	// private var's

    private String  name       = ConfigConst.NOT_SET;
	private String  timeStamp  = null;
    private boolean hasError   = false;
    private int     statusCode = 0;
    
    private long    timeStampMillis = 0L;

    
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	protected BaseIotData()
	{
		super();
	}
	
	
	// public methods
	
	public String getName()
	{
		return this.name;
	}
	
	public String getStateData()
	{
		return null;
	}
	
	public int getStatusCode()
	{
		return this.statusCode;
	}
	
	public String getTimeStamp()
	{
		return this.timeStamp;
	}
	
	public long getTimeStampMillis()
	{
		return this.timeStampMillis;
	}
	
	public boolean hasError()
	{
		return this.hasError;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setStateData(String data)
	{
	}
	
	public void setStatusCode(int code)
	{
		this.statusCode = code;
	}
	
	/**
	 * Call handleUpdate method from respective derived class to update the latestData
	 */
	public void updateData(BaseIotData data)
	{
		
		handleUpdateData(data);
	}
	
	// protected methods
	
	/**
	 * Template method to handle data update for the sub-class.
	 * 
	 * @param BaseIotData While the parameter must implement this method,
	 * the sub-class is expected to cast the base class to its given type.
	 */
	protected abstract void handleUpdateData(BaseIotData data);
	
}
