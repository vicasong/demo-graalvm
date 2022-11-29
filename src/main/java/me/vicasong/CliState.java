package me.vicasong;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLI Status
 *
 * @author vicasong
 * @since 2021-03-02 16:51
 */
public class CliState {

	/** log recorder */
	private static final Logger log = LoggerFactory.getLogger(CliState.class);

	public static volatile boolean EXITING = false;

	public static final Vector<InterruptedException> interrupted = new Vector<>();

	public static final boolean DEBUG_MODE;

	/** Current PID */
	public static final long PID;

	/** Program path */
	public static final Path PROGRAM;

	static {
		PID = getPid();
		PROGRAM = getProgramPath();
		DEBUG_MODE = debugModeSwitch();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (CliState.interrupted.isEmpty() && CliState.EXITING) {
				return;
			}
			Map<Thread, StackTraceElement[]> stMap = Thread.getAllStackTraces();
			// only dump the stack trace with the method "exit":
			for (Map.Entry<Thread, StackTraceElement[]> stElmt : stMap.entrySet()) {
				for (StackTraceElement st : stElmt.getValue()) {
					if (st.getMethodName().equals("exit")) {
						printStackTrace(stElmt.getKey(), stElmt.getValue());
						break;
					}
				}
			}
			for (InterruptedException exception : CliState.interrupted) {
				if (exception != null) {
					log.error("Interrupted at: ", exception);
				}
			}
		}));
	}

	public static boolean processAlive(long pid) {
		Optional<ProcessHandle> process = ProcessHandle.of(pid);
		return process.isPresent();
	}


	private static long getPid() {
		//  get PID
		final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		return runtime.getPid();
	}

	private static Path getProgramPath() {
		URL currentPath = CliState.class.getProtectionDomain()
				.getCodeSource()
				.getLocation();
		try {
			if (currentPath.getProtocol().equalsIgnoreCase("jar")) {
				String path = currentPath.getPath();
				if (path.contains("!")) {
					path = path.substring(0, path.indexOf("!"));
				}
				currentPath = new URL(path);
			}
			return Paths.get(currentPath.toURI());
		}
		catch (Exception e) {
			System.err.println("Unable to get current program info: " + currentPath + "\ncaused by: " + e);
			throw new RuntimeException(e);
		}
	}

	private static boolean debugModeSwitch() {
		List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		return inputArguments.stream()
				.anyMatch(p -> p.toLowerCase().contains("-agentlib:jdwp"));

	}

	private static void printStackTrace(Thread thread, StackTraceElement[] stElmts) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement stElmt : stElmts) {
			sb.append("\n\tat ").append(stElmt);
		}
		log.error("dumping thread: {} {}", thread, sb);
	}

}
