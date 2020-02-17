package com.app.androidkt.VoiceMeeting;

public class SyncResult {
    private static final long TIMEOUT = 20000L;
    private String result;

    public String getResult() {
        long startTimeMillis = System.currentTimeMillis();
        while (result == null && System.currentTimeMillis() - startTimeMillis < TIMEOUT) {
            synchronized (this) {
                try {
                    wait(TIMEOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void setResult(String result) {
        this.result = result;
        synchronized (this) {
            notify();
        }
    }
}
