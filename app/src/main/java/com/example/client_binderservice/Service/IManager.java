package com.example.client_binderservice.Service;

import com.example.client_binderservice.ICallback;

public interface IManager {
    String getName();
    void registerCallback(ICallback callback);
    void download();
}

