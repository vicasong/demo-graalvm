package me.vicasong.boot;


import me.vicasong.exception.SkipOperationException;
import me.vicasong.utils.ConsoleFormatter;
import jakarta.validation.ValidationException;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Command error handle
 *
 * @author vicasong
 * @since 2022-07-25 16:25
 */
public record DefaultCommandExceptionHandler(
        Terminal terminal) implements CommandLine.IExecutionExceptionHandler {
    /**
     * log recorder
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultCommandExceptionHandler.class);

    @Override
    public int handleExecutionException(Exception ex,
                                        CommandLine commandLine,
                                        CommandLine.ParseResult parseResult) throws Exception {
        Throwable err = ex;
        if (ex instanceof ValidationException) {
            err = ex.getCause() != null ? ex.getCause() : ex;
        }
        if (err instanceof SkipOperationException) {
            // skip this
            return 0;
        }

        log.error("error execute command: <{} {}>", commandLine.getCommandName(), parseResult.originalArgs(), err);
        String msg = err.getClass() == RuntimeException.class ?
                err.getMessage():
                String.format("%s %s", err.getClass().getSimpleName()
                .replace("Exception", "")
                .replace("Error", ""),
                err.getMessage());
        ConsoleFormatter.printError(terminal, msg);
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
