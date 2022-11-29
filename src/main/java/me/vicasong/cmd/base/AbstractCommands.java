package me.vicasong.cmd.base;

import me.vicasong.console.ToolTerminal;
import picocli.CommandLine;
import me.vicasong.utils.ConsoleFormatter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import org.springframework.shell.table.Table;

import java.util.Set;

/**
 * Command support
 *
 * @author vicasong
 * @since 2021-01-19 11:30
 */
public abstract class AbstractCommands {

	/** Console */
	protected ToolTerminal terminal;

	/** Command spec */
	@CommandLine.Spec
	protected CommandLine.Model.CommandSpec spec;

	public AbstractCommands(ToolTerminal terminal) {
		this.terminal = terminal;
	}


	protected int preferWidth() {
		return terminal.getWidth();
	}


	public void printInfo(String msg) {
		ConsoleFormatter.printInfo(terminal, msg);
	}


	public void printWarn(String msg) {
		ConsoleFormatter.printWarn(terminal, msg);
	}


	public void printError(String msg) {
		ConsoleFormatter.printError(terminal, msg);
	}


	public void printTable(Table table) {
		ConsoleFormatter.printTable(terminal, table);
	}



	public void printUsage(CommandLine.Model.CommandSpec spec) {
		ConsoleFormatter.printUsage(terminal.writer(), spec.commandLine());
	}


	protected void validate() {
		Set<ConstraintViolation<AbstractCommands>> violations = validator.validate(this);

		if (!violations.isEmpty()) {
			StringBuilder errorMsg = new StringBuilder();
			for (ConstraintViolation<?> violation : violations) {
				errorMsg.append("ERROR: ")
						.append(violation.getMessage())
						.append(": ")
						.append(violation.getInvalidValue())
						.append("\n");
			}
			throw new CommandLine.ParameterException(spec.commandLine(), errorMsg.toString());
		}
	}


	private static final Validator validator;

	static {
		ValidatorFactory factory = Validation
				.byDefaultProvider()
				.configure()
				.messageInterpolator(new ParameterMessageInterpolator())
				.buildValidatorFactory();
		validator = factory.getValidator();
	}

}
