package com.hoccer.thread;

public abstract class ThreadedTask extends Thread {

    @SuppressWarnings("unused")
    private static String LOG_TAG   = "ThreadedTask";
    private int           mProgress = 0;

    public abstract void doInBackground();

    @Override
    public void run() {
        doInBackground();
        if (isInterrupted()) {
            return;
        }
        onPostExecute();
    }

    public int getProgress() {
        synchronized (this) {
            return mProgress;
        }
    }

    protected void setProgress(int pProgress) {
        synchronized (this) {
            mProgress = Math.max(0, Math.min(100, pProgress));
        }
    }

    public boolean isTaskCompleted() {
        synchronized (this) {
            return mProgress == 100;
        }
    }

    public boolean isRunning() {
        synchronized (this) {
            return (mProgress < 100 && mProgress > 0);
        }
    }

    protected void onPostExecute() {
        synchronized (this) {
            setProgress(100);
        }
    }
}
