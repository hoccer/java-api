package com.hoccer.thread;

public interface StatusHandler {
    
    public void onSuccess();
    
    public void onError(Throwable e);
    
    public void onProgress(int progress);
}
