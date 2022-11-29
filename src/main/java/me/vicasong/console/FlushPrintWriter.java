package me.vicasong.console;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.lang.NonNull;

/**
 * auto flush PrintWriter
 *
 * @author vicasong
 * @since 2022-08-24 09:34
 */
public class FlushPrintWriter extends PrintWriter {

	private final AtomicInteger sizeWritten = new AtomicInteger(0);

	public FlushPrintWriter(Writer out) {
		super(out, true);
	}

	@Override
	public void write(int c) {
		super.write(c);
		flush();
		sizeWritten.incrementAndGet();
	}

	@Override
	public void write(@NonNull char[] buf, int off, int len) {
		super.write(buf, off, len);
		flush();
		sizeWritten.addAndGet(len);
	}

	@Override
	public void write(@NonNull String s, int off, int len) {
		super.write(s, off, len);
		flush();
		sizeWritten.addAndGet(len);
	}

	public int getWrittenSize() {
		return sizeWritten.get();
	}
}
