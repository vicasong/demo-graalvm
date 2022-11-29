package me.vicasong.console;

import me.vicasong.constant.ToolConstants;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.impl.AbstractPosixTerminal;
import org.jline.terminal.spi.Pty;
import org.jline.utils.ColorPalette;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/** Console delegate */
public class SystemTerminal implements Terminal, ToolTerminal {

	private final Terminal terminal;
	private final BlockingReader reader;
	private volatile FlushPrintWriter writer;

	public SystemTerminal() {
		try {
			terminal = TerminalBuilder.builder().build();
		}
		catch (IOException e) {
			throw new IllegalStateException("Could not create Terminal: " + e.getMessage());
		}
		reader = new ToolBlockingReader(terminal);
	}

	@Override
	public String getName() {
		return "sys";
	}

	@Override
	public SignalHandler handle(Signal signal, SignalHandler handler) {
		return terminal.handle(signal, handler);
	}

	@Override
	public void raise(Signal signal) {
		terminal.raise(signal);
	}

	@Override
	public NonBlockingReader reader() {
		return terminal.reader();
	}

	@Override
	public PrintWriter writer() {
		if (writer == null) {
			synchronized (this) {
				if (writer == null) {
					// auto flush
					writer = new FlushPrintWriter(terminal.writer());
				}
			}
		}
		return writer;
	}

	@Override
	public Charset encoding() {
		return terminal.encoding();
	}

	@Override
	public InputStream input() {
		return terminal.input();
	}

	@Override
	public OutputStream output() {
		return terminal.output();
	}

	@Override
	public boolean canPauseResume() {
		return terminal.canPauseResume();
	}

	@Override
	public void pause() {
		terminal.pause();
	}

	@Override
	public void pause(boolean wait) throws InterruptedException {
		terminal.pause(wait);
	}

	@Override
	public void resume() {
		terminal.resume();
	}

	@Override
	public boolean paused() {
		return terminal.paused();
	}

	@Override
	public Attributes enterRawMode() {
		return terminal.enterRawMode();
	}

	@Override
	public boolean echo() {
		return terminal.echo();
	}

	@Override
	public boolean echo(boolean echo) {
		return terminal.echo(echo);
	}

	@Override
	public Attributes getAttributes() {
		return terminal.getAttributes();
	}

	@Override
	public void setAttributes(Attributes attr) {
		terminal.setAttributes(attr);
	}

	@Override
	public Size getSize() {
		return terminal.getSize();
	}

	@Override
	public void setSize(Size size) {
		terminal.setSize(size);
	}

	@Override
	public int getWidth() {
		int width = terminal.getWidth();
		return width >= ToolConstants.MIN_CONSOLE_WIDTH ? width : ToolConstants.DEF_CONSOLE_WIDTH;
	}

	@Override
	public int getHeight() {
		return terminal.getHeight();
	}

	@Override
	public Size getBufferSize() {
		return terminal.getBufferSize();
	}

	@Override
	public void flush() {
		terminal.flush();
	}

	@Override
	public String getType() {
		return terminal.getType();
	}

	@Override
	public boolean puts(InfoCmp.Capability capability, Object... params) {
		return terminal.puts(capability, params);
	}

	@Override
	public boolean getBooleanCapability(InfoCmp.Capability capability) {
		return terminal.getBooleanCapability(capability);
	}

	@Override
	public Integer getNumericCapability(InfoCmp.Capability capability) {
		return terminal.getNumericCapability(capability);
	}

	@Override
	public String getStringCapability(InfoCmp.Capability capability) {
		return terminal.getStringCapability(capability);
	}

	@Override
	public Cursor getCursorPosition(IntConsumer discarded) {
		return terminal.getCursorPosition(discarded);
	}

	@Override
	public boolean hasMouseSupport() {
		return terminal.hasMouseSupport();
	}

	@Override
	public boolean trackMouse(MouseTracking tracking) {
		return terminal.trackMouse(tracking);
	}

	@Override
	public MouseEvent readMouseEvent() {
		return terminal.readMouseEvent();
	}

	@Override
	public MouseEvent readMouseEvent(IntSupplier reader) {
		return terminal.readMouseEvent(reader);
	}

	@Override
	public boolean hasFocusSupport() {
		return terminal.hasFocusSupport();
	}

	@Override
	public boolean trackFocus(boolean tracking) {
		return terminal.trackFocus(tracking);
	}

	@Override
	public ColorPalette getPalette() {
		return terminal.getPalette();
	}


	@Override
	public void close() throws IOException {
		terminal.close();
	}

	@Override
	public BlockingReader inputReader() {
		return reader;
	}

	@Override
	public int writtenSize() {
		if (writer != null) {
			return writer.getWrittenSize();
		}
		return 0;
	}

	public Pty getPty() {
		if (terminal instanceof AbstractPosixTerminal pty) {
			return pty.getPty();
		}
		return null;
	}
}
