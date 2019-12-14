package com.example.pfuternik.vidyoiodemo;

public class Call {
    private String caller;
    private String receiver;
    private String token;
    private int connected;

    public Call(String caller, String receiver, String token, int connected) {
        this.caller = caller;
        this.receiver = receiver;
        this.token = token;
        this.connected = connected;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getConnected() {
        return connected;
    }

    public void setConnected(int connected) {
        this.connected = connected;
    }
}
