package com.artcom.y60.thread;

import com.artcom.y60.Logger;

public abstract class ThreadedTask extends Thread {

    private static String LOG_TAG   = "ThreadedTask";
    private int           mProgress = 0;

    public abstract void doInBackground();

    @Override
    public void run() {
        doInBackground();
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
            Logger.v(LOG_TAG, "threaded progress ", mProgress);
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
            mProgress = 100;
        }
    }

}
