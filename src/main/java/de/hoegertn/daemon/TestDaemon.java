package de.hoegertn.daemon;

import java.util.Map;

/**
 * @author hoegertn
 * 
 */
public class TestDaemon implements IDaemonLifecycleListener {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		DaemonStarter.startDaemon("testdaemon", new TestDaemon());
	}

	@Override
	public void stopping() {
		System.err.println("Stopping");
	}

	@Override
	public void stopped() {
		System.err.println("Stopped");
	}

	@Override
	public void started() {
		System.err.println("Started");
	}

	@Override
	public void signalUSR2() {
		System.err.println("SIGUSR2");
	}

	@Override
	public void exception(final LifecyclePhase phase, final Throwable exception) {
		System.err.println("Exception in phase: " + phase.name());
		exception.printStackTrace();
	}

	@Override
	public boolean doStop() {
		System.err.println("do stop code");
		return true;
	}

	@Override
	public boolean doStart() {
		System.err.println("do start code");
		return true;
	}

	@Override
	public void aborting() {
		System.err.println("SYSTEM ABORT");
	}

	@Override
	public Map<String, String> loadProperties() {
		return null;
	}
}
