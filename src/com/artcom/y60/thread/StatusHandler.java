package com.artcom.y60.thread;

public interface StatusHandler {
    
    public void onSuccess();
    
    public void onError(Throwable e);
    
    public void onProgress(int progress);
}
