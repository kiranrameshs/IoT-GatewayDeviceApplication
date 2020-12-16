/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */

package programmingtheiot.gda.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.logging.Logger;
import java.util.logging.Level;

import programmingtheiot.gda.app.GatewayDeviceApp;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemMemUtilTask extends BaseSystemUtilTask {
	// constructors

	private static final Logger _Logger = Logger.getLogger(SystemMemUtilTask.class.getName());

	public SystemMemUtilTask() {
		super();
	}

	// protected methods

	@Override
	protected float getSystemUtil() {
		double val_t = Runtime.getRuntime().totalMemory();
		double val_f = Runtime.getRuntime().freeMemory();
		double val_u = val_t - val_f;
		_Logger.info("Memory used: " + val_u);
		_Logger.info("Memory avail: " + val_t);
		double val = ((double) val_u / (double) val_t) * 100.0d;
		return (float) val;
		// return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
	}

}
