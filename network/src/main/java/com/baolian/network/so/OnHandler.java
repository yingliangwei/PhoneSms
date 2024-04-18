package com.baolian.network.so;

public interface OnHandler {
    default void handleMessage(String str){

    }
    default void handleMessage(int w,String str){

    }
}
