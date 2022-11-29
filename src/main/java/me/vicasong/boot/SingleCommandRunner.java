package me.vicasong.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import me.vicasong.CliState;
import me.vicasong.cmd.base.CommandsAutowired;
import me.vicasong.console.ToolTerminal;
import me.vicasong.console.PrefixHelp;
import me.vicasong.exception.SkipOperationException;
import me.vicasong.utils.ConsoleFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

/**
 * Command execute runner
 *
 * @author vicasong
 * @since 2021-02-08 15:31
 */
public class SingleCommandRunner implements ApplicationRunner, Ordered {

	/** log recorder */
	private static final Logger log = LoggerFactory.getLogger(SingleCommandRunner.class);


	/** Commands bean */
	private final List<Object> commands;
	/** Terminal */
	private final ToolTerminal terminal;
	/** Command list */
	private volatile List<CommandLine> cmdList;
	/** Pre handle commands */
	private static final List<String> INIT_CMD = Arrays.asList("-h", "--help", "-V", "--version");

	public SingleCommandRunner(ApplicationContext context) {
		Map<String, Object> commandBeans = context.getBeansWithAnnotation(CommandLine.Command.class);
		if (commandBeans.isEmpty()) {
			throw new UnsupportedOperationException("No commands defined.");
		}
		commands = new ArrayList<>(commandBeans.values());
		terminal = context.getBean(ToolTerminal.class);
	}

	public ToolTerminal getTerminal() {
		return terminal;
	}

	@Override
	public void run(ApplicationArguments args) {
		try {
			String[] sourceArgs = args.getSourceArgs();
			// command name
			String command = sourceArgs.length < 1 ? "" : sourceArgs[0];
			if (cmdList == null) {
				initCmd();
			}
			if (!command.isEmpty()) {
				for (CommandLine commandLine : this.cmdList) {
					// get command by command name
					Collection<String> commands;
					String name = commandLine.getCommandName();
					String[] passedArgs = sourceArgs;
					if (!StringUtils.hasText(name)) {
						commands = new ArrayList<>(commandLine.getCommandSpec().subcommands().keySet());
						Collection<String> aliases = new ArrayList<>();
						for (String c : commands) {
							aliases.addAll(Arrays.asList(commandLine.getSubcommands().get(c).getCommandSpec()
									.aliases()));
						}
						Map<String, CommandLine.Model.OptionSpec> optionSpecMap = commandLine.getCommandSpec()
								.optionsMap();
						if (null != optionSpecMap && !optionSpecMap.isEmpty()) {
							aliases.addAll(optionSpecMap.keySet());
						}
						commands.addAll(aliases);
					}
					else {
						commands = Collections.singleton(name);
						if (sourceArgs.length > 1) {
							passedArgs = new String[sourceArgs.length - 1];
							System.arraycopy(sourceArgs, 1, passedArgs, 0, passedArgs.length);
						}
						else {
							passedArgs = new String[0];
						}
					}
					if (commands.contains(command)) {
						if (parseHelpOrVersion(commandLine, passedArgs)) {
							return;
						}
						log.info("@> command start: [{}]", String.join(" ", sourceArgs));
						String[] cmdArgs = passedArgs;
						CompletableFuture<Integer> future = CompletableFuture
								.supplyAsync(() -> commandLine.execute(cmdArgs),
										runningExecutor());
						int preSizeWritten = terminal.writtenSize();
						StopWatch watch = new StopWatch();
						watch.start();
						try {
							do {
								try {
									Integer code = future.get(3, TimeUnit.SECONDS);
									setCurrentExit(code);
								}
								catch (Exception e) {
									if (e instanceof TimeoutException) {
										if (terminal.writtenSize() <= preSizeWritten) {
											// wait timeout
											ConsoleFormatter.printInfo(terminal, "command processing, may take more time than usual...");
										}
									}
									else if (e instanceof InterruptedException iErr) {
										ConsoleFormatter.printInfo(terminal, "command processing interrupted.");
										CliState.interrupted.add(iErr);
										return;
									}
									else if (e instanceof RuntimeException ex) {
										throw ex;
									}
									else {
										if (e.getCause() instanceof RuntimeException) {
											throw e.getCause();
										}
										throw new RuntimeException(e.getMessage(), e);
									}
								}
							}
							while (!future.isDone());
						}
						finally {
							watch.stop();
							log.info("@> command [{}] execution cost: {}s, canceled={}, done={}",
									String.join(" ", sourceArgs),
									watch.getTotalTimeSeconds(),
									future.isCancelled(),
									future.isDone());
						}
						return;
					}
				}
				terminal.writer().write(" > Unknown command: " + command + "\n");
			}
		}
		catch (Throwable e) {
			if (e instanceof SkipOperationException) {
				exitCode.set(0);
				return;
			}
			log.error("error execute {}", args, e);
			ConsoleFormatter.printError(terminal, e.getMessage());
		}
		finally {
			log.info("command runner exit now.");
		}
	}


	/** collect commands */
	private void initCmd() {
		if (cmdList == null) {
			synchronized (this) {
				if (cmdList == null) {
					CommandLine.Help.ColorScheme scheme = new CommandLine.Help.ColorScheme.Builder()
							.commands(CommandLine.Help.Ansi.Style.bold, CommandLine.Help.Ansi.Style.fg_white)
							.parameters(CommandLine.Help.Ansi.Style.italic, CommandLine.Help.Ansi.Style.fg_yellow)
							.options(CommandLine.Help.Ansi.Style.fg_yellow)
							.optionParams(CommandLine.Help.Ansi.Style.fg_yellow)
							.build();
					// error handle
					DefaultCommandExceptionHandler exceptionHandler = new DefaultCommandExceptionHandler(terminal);
					List<CommandLine> cmds = new ArrayList<>();
					CommandLine.IHelpFactory factory = (commandSpec, colorScheme) -> new PrefixHelp(
							commandSpec, colorScheme, CliState.PROGRAM.getFileName().toString());
					List<CommandsAutowired> autowireds = new ArrayList<>();
					for (Object bean : this.commands) {
						CommandLine commandLine = new CommandLine(bean);
						commandLine.setOut(terminal.writer());
						commandLine.setErr(terminal.writer());
						commandLine.setUnmatchedOptionsArePositionalParams(true);
						commandLine.setExecutionExceptionHandler(exceptionHandler);
						commandLine.setHelpFactory(factory);
						commandLine.setUnmatchedArgumentsAllowed(false);
						commandLine.setColorScheme(scheme);
						cmds.add(commandLine);
						if (bean instanceof CommandsAutowired cab) {
							autowireds.add(cab);
						}
					}
					cmds.sort(Comparator.comparing(o -> o.getCommandSpec().name()));
					cmdList = Collections.unmodifiableList(cmds);
					for (CommandsAutowired autowired : autowireds) {
						autowired.setCommandList(cmdList);
					}
				}
			}
		}
	}

	/** will print usage */
	private boolean parseHelpOrVersion(CommandLine cmd, String[] args) {
		try {
			CommandLine.ParseResult parseResult = cmd.parseArgs(args);
			if (parseResult.isUsageHelpRequested()) {
				CommandLine helpTarget;
				if (parseResult.hasSubcommand()) {
					helpTarget = parseResult.subcommand().asCommandLineList().get(0);
				}
				else {
					helpTarget = cmd;
				}
				if (helpTarget != null) {
					helpTarget.usage(terminal.writer());
					return true;
				}
			}
		}
		catch (Exception ignore) {
		}
		return false;
	}


	private Executor runningExecutor() {
		return Executors.newSingleThreadExecutor(new SampleThreadFactory("cmd"));
	}


	@Override
	public int getOrder() {
		return 0;
	}

	private static final ThreadLocal<Integer> exitCode = new ThreadLocal<>();

	/** program exit code */
	public static int currentExit() {
		try {
			return Optional.ofNullable(exitCode.get()).orElse(0);
		}
		finally {
			exitCode.remove();
		}
	}

	/** set exit code */
	public static void setCurrentExit(int code) {
		exitCode.set(code);
	}



	private record SampleThreadFactory(String name) implements ThreadFactory {

		private static final AtomicInteger idx = new AtomicInteger(0);

		@Override
		public Thread newThread(@NonNull Runnable r) {
			return new Thread(r, name + idx.getAndIncrement());
		}
	}
}
