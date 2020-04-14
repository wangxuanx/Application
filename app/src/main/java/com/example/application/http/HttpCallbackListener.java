package com.example.application.http;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
