package com.example.client_binderservice;

public interface ICallback {
    void onError(String errMsg);
    void onUpdateProgressBar(int progress);
    void onComplete();
}
