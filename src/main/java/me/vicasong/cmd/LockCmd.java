package me.vicasong.cmd;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import me.vicasong.cmd.base.AbstractCommands;
import me.vicasong.console.ToolTerminal;
import me.vicasong.constant.Commands;
import picocli.CommandLine;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * test command - lock file
 *
 *
 * @author vicasong
 * @since 2022-08-10 14:35
 */
@Component
@CommandLine.Command(name = Commands.Test.LOCK,
		mixinStandardHelpOptions = true,
		hidden = true,
		description = "Lock file test.")
public class LockCmd extends AbstractCommands implements Callable<Integer> {

	@CommandLine.Parameters(index = "0",
			paramLabel = "<file>",
			description = "Specify an file to lock.")
	protected String file;

	@CommandLine.Option(names = {"-n", "--noblock"},
			description = "Fail rather than wait.")
	protected boolean noblock;

	public LockCmd(ToolTerminal terminal) {
		super(terminal);
	}

	@Override
	public Integer call() throws Exception {
		File lockFile;
		if (StringUtils.hasText(file)) {
			lockFile = Paths.get(file).toFile();
		} else {
			throw new IllegalArgumentException("Missing argument: file");
		}
		printInfo("Lock file: " + lockFile.getAbsolutePath());
		synchronized (this) {
			try (RandomAccessFile rf = new RandomAccessFile(lockFile, "rw");
				 FileLock fileLock = noblock ?
						 rf.getChannel().tryLock() :
						 rf.getChannel().lock()) {
				if (fileLock != null) {
					printInfo("Locked.");
				} else {
					printInfo("Lock failed.");
					return 1;
				}
				// wait until ctrl+c
				wait();
			} catch (InterruptedException ignore) {
			}
		}
		return 0;
	}

}
