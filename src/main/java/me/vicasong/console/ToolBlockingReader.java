package me.vicasong.console;

import me.vicasong.utils.ConsoleFormatter;
import org.jline.terminal.Terminal;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;

/**
 * Console reader
 *
 * @author vicasong
 * @since 2022-07-27 15:24
 */
public class ToolBlockingReader implements ToolTerminal.BlockingReader {

	private final Console console;
	private final Terminal terminal;

	public ToolBlockingReader(Terminal terminal) {
		this.terminal = terminal;
		console = System.console();
	}

	@Override
	public char[] readPassword(String fmt, Object... args) {
		if (console == null) {
			// console no exist, read from steam
			terminal.writer().printf(fmt, args);
			terminal.writer().flush();
			return readLineFromInputStream().toCharArray();
		}
		return console.readPassword(fmt, args);
	}

	@Override
	public String readLine(String fmt, Object... args) {
		if (console == null) {
			// console no exist, read from steam
			terminal.writer().printf(fmt, args);
			terminal.writer().flush();
			return readLineFromInputStream();
		}
		return console.readLine(fmt, args);
	}

	private String readLineFromInputStream() {
		try {
			String line = new BufferedReader(terminal.reader())
					.readLine();
			if (line != null) {
				return line;
			}
		}
		catch (IOException e) {
			ConsoleFormatter.printError(terminal, e.getMessage());
		}
		return "";
	}

}
