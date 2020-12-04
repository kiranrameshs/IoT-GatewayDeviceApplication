/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */ 
//run this app
package programmingtheiot.gda.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.gda.system.SystemPerformanceManager;

/**
 * Main GDA application.
 * 
 */
public class GatewayDeviceApp
{
	// static
	private static final Logger _Logger =
		Logger.getLogger(GatewayDeviceApp.class.getName());
	
	public static final long DEFAULT_TEST_RUNTIME = 600000L;

	private SystemPerformanceManager sysPerfManager;
	private DeviceDataManager devDataManager;
	
	// private var's
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param args
	 */
	public GatewayDeviceApp(String[] args)
	{
		super();
		_Logger.info("Initializing GDA...");
		this.sysPerfManager = new SystemPerformanceManager(10);
		this.devDataManager = new DeviceDataManager();
		parseArgs(args);
	}
	
	
	// static
	
	/**
	 * Main application entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		GatewayDeviceApp gwApp = new GatewayDeviceApp(args);
		gwApp.startApp();
		try {
			Thread.sleep(DEFAULT_TEST_RUNTIME);
		} catch (InterruptedException e) {
			// ignore
		}
		gwApp.stopApp(0);
	}
	
	
	// public methods
	
	/**
	 * Initializes and starts the application.
	 * 
	 */
	public void startApp()
	{
		_Logger.info("Starting GDA...");
		try {
			_Logger.info("GDA started successfully.");
			devDataManager.startManager();
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to start GDA. Exiting.", e);
			stopApp(-1);
		}
	}
	
	/**
	 * Stops the application.
	 * 
	 * @param code The exit code to pass to {@link System.exit()}
	 */
	public void stopApp(int code)
	{
		_Logger.info("Stopping GDA...");
		try {
			devDataManager.stopManager();
			_Logger.log(Level.INFO, "GDA stopped successfully with exit code {0}.", code);
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to cleanly stop GDA. Exiting.", e);
		}
		System.exit(code);
	}
	
	// private methods
	
	/**
	 * Load the config file.
	 * 
	 * 
	 * @param configFile The name of the config file to load.
	 */
	private void initConfig(String configFile)
	{
		_Logger.log(Level.INFO, "Attempting to load configuration: {0}", (configFile != null ? configFile : "Default."));
	}
	
	/**
	 * Parse any arguments passed in on app startup.
	 * <p>
	 * This method should be written to check if any valid command line args are provided,
	 * including the name of the config file. Once parsed, call {@link #initConfig(String)}
	 * with the name of the config file, or null if the default should be used.
	 * <p>
	 * If any command line args conflict with the config file, the config file
	 * in-memory content should be overridden with the command line argument(s).
	 * 
	 * @param args The non-null and non-empty args array.
	 */
	private void parseArgs(String[] args)
	{
		String configFile = null;
		if (args != null) {
			_Logger.log(Level.INFO, "Parsing {0} command line args.", args.length);
			for (String arg : args) {
				if (arg != null) {
					arg = arg.trim();
				}
			}
		} else {
			_Logger.info("No command line args to parse.");
		}
		initConfig(configFile);
	}

}
