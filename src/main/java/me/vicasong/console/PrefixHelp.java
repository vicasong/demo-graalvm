package me.vicasong.console;

import picocli.CommandLine;

import java.util.Arrays;

/**
 * Prefix command name helper
 *
 * @author vicasong
 * @since 2022-07-28 14:10
 */
public class PrefixHelp extends CommandLine.Help {

	private final String prefix;

	public PrefixHelp(CommandLine.Model.CommandSpec commandSpec, ColorScheme colorScheme, String prefix) {
		super(commandSpec, colorScheme);
		this.prefix = prefix;
	}

	@Override
	protected String insertSynopsisCommandName(int synopsisHeadingLength, Ansi.Text optionsAndPositionalsAndCommandsDetails) {
		if (synopsisHeadingLength < 0) {
			throw new IllegalArgumentException("synopsisHeadingLength must be a positive number but was " + synopsisHeadingLength);
		}

		CommandLine.Model.CommandSpec commandSpec = commandSpec();
		int width = commandSpec.usageMessage().width();
		ColorScheme colorScheme = colorScheme();
		// Fix for #142: first line of synopsis overshoots width
		String commandName = commandSpec.qualifiedName();
		if (prefix != null && !prefix.isEmpty()) {
			commandName = String.format("%s %s", prefix, commandName);
		}

		// Fix for #739: infinite loop if firstColumnLength >= width (so 2nd column width becomes zero or negative)
		int indent = synopsisHeadingLength + commandName.length() + 1; // +1 for space after command name
		if (indent > commandSpec.usageMessage().synopsisAutoIndentThreshold() * width) {
			indent = commandSpec.usageMessage()
					.synopsisIndent() < 0 ? synopsisHeadingLength : commandSpec.usageMessage().synopsisIndent();
			indent = Math.min(indent, (int) (0.9 * width));
		}
		TextTable textTable = TextTable.forColumnWidths(colorScheme, width);
		textTable.setAdjustLineBreaksForWideCJKCharacters(commandSpec.usageMessage()
				.adjustLineBreaksForWideCJKCharacters());
		textTable.indentWrappedLines = indent;

		// right-adjust the command name by length of synopsis heading
		Ansi.Text PADDING = Ansi.OFF.new Text(stringOf(synopsisHeadingLength), colorScheme);
		textTable.addRowValues(PADDING.concat(colorScheme.commandText(commandName))
				.concat(optionsAndPositionalsAndCommandsDetails));
		return textTable.toString().substring(synopsisHeadingLength); // cut off leading synopsis heading spaces
	}

	private static String stringOf(int length) {
		char[] buff = new char[length];
		Arrays.fill(buff, 'X');
		return new String(buff);
	}
}
