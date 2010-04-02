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
    
    public boolean wasSuccessful() {
        return mProgress == 100;
    }
    
    protected void onPostExecute() {
        mProgress = 100;
    }
    
}
