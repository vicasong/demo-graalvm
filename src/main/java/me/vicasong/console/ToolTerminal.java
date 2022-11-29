package me.vicasong.console;

import org.jline.terminal.Terminal;

/**
 * Console terminal
 *
 * @author vicasong
 * @since 2022-07-27 11:47
 */
public interface ToolTerminal extends Terminal {

	/** Block console reader */
	BlockingReader inputReader();

	/** number of characters printed */
	int writtenSize();

	/** Block console reader */
	interface BlockingReader {

		/** Provides a formatted prompt, then reads a password or passphrase from the console with echoing disabled. */
		char[] readPassword(String fmt, Object... args);

		/** Provides a formatted prompt, then reads a single line of text from the console. */
		String readLine(String fmt, Object... args);
	}
}
