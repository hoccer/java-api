package com.artcom.y60.thread;

public abstract class ThreadedTask extends Thread {
    
    private int mProgress = 0;
    
    public abstract void doInBackground();
    
    @Override
    public void run() {
        doInBackground();
        onPostExecute();
    }
    
    public int getProgress() {
        return mProgress;
    }
    
    protected void setProgress(int pProgress) {
        mProgress = Math.max(0, Math.min(100, pProgress));
    }
    
    public boolean isDone() {
        return mProgress == 100;
    }
    
    public boolean isRunning() {
        return (mProgress < 100 && mProgress > 0);
    }
    
    protected void onPostExecute() {
        mProgress = 100;
    }
    
}
