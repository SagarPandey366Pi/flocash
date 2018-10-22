package wallettemplate;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

public class RestartApplication{
	
    public static void restartApplication(String envvar) throws IOException {
    	
    	final String SUN_JAVA_COMMAND = "sun.java.command";
    	
    	try {
    	// java binary
    	String java = System.getProperty("java.home") + "/bin/java";
    	// vm arguments
    	List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
    	StringBuffer vmArgsOneLine = new StringBuffer();
    	for (String arg : vmArguments) {
    	// if it's the agent argument : we ignore it otherwise the
    	// address of the old application and the new one will be in conflict
    	if (!arg.contains("-agentlib")) {
    	vmArgsOneLine.append(arg);
    	vmArgsOneLine.append(" ");
    	}
    	}
    	// init the command to execute, add the vm args
    	final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

    	// program main and program arguments
    	String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
    	// program main is a jar
    	if (mainCommand[0].endsWith(".jar")) {
    	cmd.append("-jar " + new File(mainCommand[0]).getPath());
    	} 
    	else 
    	{
    	cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
    	}
    	for (int i = 1; i < mainCommand.length; i++) {
    	cmd.append(" ");
    	cmd.append(mainCommand[i]);
    	System.out.println("Enviromental variable restart: "+envvar);
    	}
    	// execute the command in a shutdown hook, to be sure that all the
    	// resources have been disposed before restarting the application
    	Runtime.getRuntime().addShutdownHook(new Thread() {
    	@Override
    	public void run() {
    	try {
    	Runtime.getRuntime().exec(cmd.toString());
    	} catch (IOException e) {
    	e.printStackTrace();
    	}
    	}
    	});
    	System.exit(0);
    	} catch (Exception e) {
    	throw new IOException("Error while trying to restart the application", e);
    	}
    }
}
