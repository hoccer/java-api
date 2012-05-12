/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Hoccer GmbH Berlin, and are copy protected
 * by law. They may be used, modified and redistributed under the terms
 * of GNU General Public License referenced below. 
 *    
 * Alternative licensing without the obligations of the GPL is
 * available upon request.
 * 
 * GPL v3 Licensing:
 * 
 * This file is part of the "Linccer Java-API".
 * 
 * Linccer Java-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Linccer Java-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Linccer Java-API. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
