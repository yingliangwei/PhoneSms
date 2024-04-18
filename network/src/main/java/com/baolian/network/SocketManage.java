package com.baolian.network;

import android.accounts.NetworkErrorException;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.baolian.network.overall.Overall;
import com.baolian.network.so.Handler;
import com.baolian.network.so.OnHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class SocketManage extends Thread implements OnHandler {
    private static final String TAG = "SocketManage";
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private Socket socket;
    private final int MUTE_SIZE = 1024;
    private final int timeout = 3_000;
    private boolean isConnected = false, main;
    private final OnMessage onMessage;
    private Message message;

    public boolean getIsConnected() {
        return isConnected;
    }


    public SocketManage(OnMessage onMessage) {
        this.onMessage = onMessage;
    }

    public SocketManage(OnMessage onMessage, Message message) {
        this.onMessage = onMessage;
        this.message = message;
    }

    public SocketManage(OnMessage onMessage, Message message, boolean main) {
        this.onMessage = onMessage;
        this.message = message;
        this.main = main;
    }

    public SocketManage(OnMessage onMessage, boolean main) {
        this.onMessage = onMessage;
        this.main = main;
    }

    @Override
    public void handleMessage(String str) {
        if (onMessage != null) {
            onMessage.onSuccess(str);
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket();
            socket.setKeepAlive(true);
            socket.connect(new InetSocketAddress(Overall.host, 6333), timeout);
            isConnected = true;
            if (onMessage != null) onMessage.onConnect(this, message);
            Read();
        } catch (IOException | NetworkErrorException | DataFormatException e) {
            isConnected = false;
            if (e.toString().endsWith("closed")) return;
            if (onMessage != null) onMessage.onError();
            Log.e(TAG, e.toString());
        }
    }


    private void send(String text) throws IOException {
        byte[] data = compress(text.getBytes());
        String encoded = Base64.encodeToString(data, Base64.NO_WRAP);
        byte[] end = "\n".getBytes();
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(encoded.getBytes());
        outputStream.write(end);
    }

    public void send(Class<?> clazz, int code, JSONObject data) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clazz", clazz.getName());
        jsonObject.put("code", code);
        jsonObject.put("data", data);
        jsonObject.put("lang", getCurrentLanguage());
        if (getIsConnected()) {
            send(jsonObject.toString());
        }
    }

    public void send(String clazz, int code, JSONObject data) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clazz", clazz);
        jsonObject.put("code", code);
        jsonObject.put("data", data);
        jsonObject.put("lang", getCurrentLanguage());
        if (getIsConnected()) {
            send(jsonObject.toString());
        }
    }

    public String getCurrentLanguage() {
        Locale current = Locale.getDefault();
        return current.getLanguage();
    }

    public void close() throws IOException {
        socket.close();
    }

    //读取服务端信息
    public void Read() throws IOException, NetworkErrorException, DataFormatException {
        byte[] bytes = new byte[MUTE_SIZE];
        StringBuilder builder = new StringBuilder();
        InputStream stream = socket.getInputStream();
        int numBytesRead;
        while ((numBytesRead = stream.read(bytes)) != -1) {
            for (int i = 0; i < numBytesRead; i++) {
                byte c = bytes[i];
                byte end = (byte) '\n';
                if (c == end) {
                    String text = builder.toString();
                    byte[] decodedBytes = Base64.decode(text, Base64.NO_WRAP);
                    byte[] data = decompress(decodedBytes);
                    String str = new String(data);
                    Log.e(TAG, str);
                    if (main) {
                        handler.sendString(str);
                    } else if (onMessage != null) {
                        onMessage.onSuccess(str);
                    }
                    close();
                    builder.delete(0, builder.length());
                } else {
                    builder.append((char) c);
                }
            }
        }
    }

    public byte[] decompress(byte[] compressedData) throws DataFormatException, IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    public byte[] compress(byte[] input) {
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        deflater.end();
        return outputStream.toByteArray();
    }

    public Socket getSocket() {
        return socket;
    }

    public static void request(OnMessage onMessage) {
        new SocketManage(onMessage).start();
    }

    public static void request(OnMessage onMessage, Message message) {

        new SocketManage(onMessage, message).start();
    }


    public interface OnMessage {
        default void onConnect(SocketManage socketManage) {
        }

        default void onConnect(SocketManage socketManage, Message message) {
            onConnect(socketManage);
        }

        default void onSuccess(String msg) {

        }

        default void onError() {

        }
    }
}
