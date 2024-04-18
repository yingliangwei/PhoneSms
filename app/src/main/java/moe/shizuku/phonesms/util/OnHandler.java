package moe.shizuku.phonesms.util;

public interface OnHandler {
    default void handleMessage(String str) {

    }

    default void handleMessage(int w, String str) {

    }

    default void handleMessage(int w) {

    }
}
