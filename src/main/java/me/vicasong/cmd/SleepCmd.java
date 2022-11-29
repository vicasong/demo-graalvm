package me.vicasong.cmd;

import java.time.Duration;
import java.util.concurrent.Callable;

import me.vicasong.cmd.base.AbstractCommands;
import me.vicasong.console.ToolTerminal;
import me.vicasong.constant.Commands;
import picocli.CommandLine;

import org.springframework.boot.convert.DurationStyle;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * test command - sleep
 *
 *
 * @author vicasong
 * @since 2022-08-10 14:35
 */
@Component
@CommandLine.Command(name = Commands.Test.SLEEP,
		mixinStandardHelpOptions = true,
		hidden = true,
		description = "Program sleep.")
public class SleepCmd extends AbstractCommands implements Callable<Integer> {

	@CommandLine.Parameters(index = "0",
			paramLabel = "<Time>",
			description = "Sleep time. %n for example: 1h means 1 hour, 3d means 3 days, 20s means 20 seconds and 5m means 5 minutes. ")
	protected String time;

	public SleepCmd(ToolTerminal terminal) {
		super(terminal);
	}

	@Override
	public Integer call() throws Exception {
		Duration duration;
		if (StringUtils.hasText(time)) {
			try {
				duration = DurationStyle.detectAndParse(time);
			}
			catch (IllegalArgumentException e) {
				printError(e.getMessage());
				return 1;
			}
		}
		else {
			duration = Duration.ofSeconds(30);
		}
		printInfo("Program going to sleep: %dms".formatted(duration.toMillis()));
		Thread.sleep(duration.toMillis());
		return 0;
	}

}
