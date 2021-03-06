/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */

package programmingtheiot.data;

import java.io.Serializable;
import programmingtheiot.common.ConfigConst;

/**
 * System Performance Data Class
 *
 */
public class SystemPerformanceData extends BaseIotData implements Serializable {
	// static

	// private var's
	private float cpuUtilization;
	private float memoryUtilization;
	private float diskUtilization;

	// constructors

	/**
	 * Default.
	 * 
	 */
	public SystemPerformanceData() {
		super();

		super.setName(ConfigConst.SYS_PERF_DATA);
	}

	// public methods

	public float getCpuUtilization() {
		return this.cpuUtilization;
	}

	public float getDiskUtilization() {
		return this.diskUtilization;
	}

	public float getMemoryUtilization() {
		return this.memoryUtilization;
	}

	public void setCpuUtilization(float val) {
		this.cpuUtilization = val;
	}

	public void setDiskUtilization(float val) {
		this.diskUtilization = val;
	}

	public void setMemoryUtilization(float val) {
		this.memoryUtilization = val;
	}

	/**
	 * Called by superClass handleUpdate to update the the data attributes
	 * this updates all the values of the sensor data and passes it as type sensor data to be published to GDA
	 * @param SystemPerformanceData
	 */
	public void updateData(SystemPerformanceData data) {
		super.setName(data.getName());
		super.setStateData(data.getStateData());
		super.setStatusCode(data.getStatusCode());
		this.setCpuUtilization(data.getCpuUtilization());
		this.setDiskUtilization(data.getDiskUtilization());
		this.setMemoryUtilization(data.getMemoryUtilization());
	}

	protected String handleToString() {
		return "cpu utilization= " + this.cpuUtilization + "memory utilization= " + this.memoryUtilization
				+ "disk utilization= " + this.diskUtilization;
	}

	protected void handleUpdateData(BaseIotData data) {
	}

}
