package com.lk.lankabell.fault.control;

public interface VolleyCallback{
    void onSuccess(String result, TaskType type);
    void onError(String message, TaskType type);

    void onSuccess(String id, TaskType type, String faultType);
}