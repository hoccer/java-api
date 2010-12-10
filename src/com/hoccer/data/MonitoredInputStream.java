package com.hoccer.data;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class MonitoredInputStream extends FilterInputStream {

    private volatile long bytesRead;

    public MonitoredInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        return (int) updateProgress(super.read());
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return (int) updateProgress(super.read(b, off, len));
    }

    @Override
    public long skip(long n) throws IOException {
        return updateProgress(super.skip(n));
    }

    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    private long updateProgress(long numBytesRead) {
        if (numBytesRead > 0) {
            this.bytesRead += numBytesRead;
            onBytesRead(bytesRead);
        }

        return numBytesRead;
    }

    abstract public void onBytesRead(long totalNumBytesRead);
}
