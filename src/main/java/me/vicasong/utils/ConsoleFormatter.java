package me.vicasong.utils;

import picocli.CommandLine;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import org.springframework.shell.table.Table;

import java.io.PrintWriter;

/**
 * Console printer
 *
 * @author vicasong
 * @since 2022-07-25 09:19
 */
@SuppressWarnings("unused")
public class ConsoleFormatter {


	public static void printInfo(Terminal terminal, String msg) {
		printInfo(terminal.writer(), msg);
	}


	public static void printInfo(PrintWriter writer, String msg) {
		writer.println(msg);
	}


	public static void printWarn(PrintWriter writer, String msg) {
		writer.println(warn(msg)
				.toAnsi());
	}


	public static void printWarn(Terminal terminal, String msg) {
		warn(msg)
				.println(terminal);
	}


	public static void printError(PrintWriter writer, String msg) {
		writer.println(error(msg)
				.toAnsi());
	}

	public static void printError(Terminal terminal, String msg) {
		error(msg)
				.println(terminal);
	}

	public static void printTip(PrintWriter writer, String msg) {
		writer.println(tip(msg)
				.toAnsi());
	}

	public static void printTip(Terminal terminal, String msg) {
		tip(msg)
				.println(terminal);
	}

	private static AttributedString error(String msg) {
		if (msg == null) {
			msg = "null";
		}
		return new AttributedStringBuilder()
				.append("ERROR: ", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
				.append(msg, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
				.append(msg.endsWith(".") ? "" : ".")
				.toAttributedString();
	}

	private static AttributedString warn(String msg) {
		if (msg == null) {
			msg = "null";
		}
		return new AttributedStringBuilder()
				.append("WARN: ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW))
				.append(msg)
				.append(msg.endsWith(".") ? "" : ".")
				.toAttributedString();
	}

	private static AttributedString tip(String msg) {
		if (msg == null) {
			msg = "null";
		}
		return new AttributedStringBuilder()
				.append(msg, AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
				.append(msg.endsWith(".") ? "" : ".")
				.toAttributedString();
	}


	public static void printTable(Terminal terminal, Table table) {
		if (table != null) {
			int width = terminal.getWidth();
			String content = table.render(width);
			int idx = content.indexOf("\n");
			if (idx > 0) {
				width = idx;
			}
			print(terminal.writer(), content);
			terminal.writer().println();
		}
	}

	public static void printUsage(PrintWriter writer, CommandLine commandLine) {
		commandLine.usage(writer);
	}

	private static void print(PrintWriter writer, String content) {
		writer.print(content);
	}

	public static void printVersion(Terminal terminal, String versionTag, long clientVersion) {
		AttributedStringBuilder sb = new AttributedStringBuilder()
				.append("    ______                        \n")
				.append("   / ____/___   _____ _____ __  __\n")
				.append("  / /_   / _ \\ / ___// ___// / / /\n")
				.append(" / __/  /  __// /   / /   / /_/ / \n")
				.append("/_/     \\___//_/   /_/    \\__, /  \n")
				.append("                         /____/   ")
				.append("  Commandline Tool \n\n", AttributedStyle.BOLD)
				.append("Version: ", AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
				.append(versionTag, AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
		sb.append("\n")
				.append("Build  : ", AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
				.append(String.valueOf(clientVersion), AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
				.append("\n")
				.append("(C) 2021-2022 BGI Genomics Co., Ltd. All Rights Reserved.")
				.toAttributedString()
				.println(terminal);
	}
}
