package com.artcom.y60.thread;

public abstract class ThreadedTask extends Thread {
    
    private int mProgress = 0;
    
    @Override
    public abstract void run();
    
    public int getProgress() {
        return mProgress;
    }
    
    protected void setProgress(int pProgress) {
        mProgress = pProgress;
    }
    
}
