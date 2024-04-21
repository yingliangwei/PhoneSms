package moe.shizuku.phonesms.sms.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.baolian.network.util.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.sms.activity.SmsDetailedActivity;
import moe.shizuku.phonesms.sqlite.DBOpenHelper;
import moe.shizuku.phonesms.util.DynamicSmsVerifyCode;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SmsReceiver extends BroadcastReceiver implements Callback {
    private final String CHANNEL_ID = "12345678d5f54f9";
    private final CharSequence CHANNEL_NAME = "12z345658788555";
    private final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) return;
        if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) return;
        try {
            insertSms(context, intent);
            okhttp(context, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void okhttp(Context context, Intent intent) throws JSONException {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context, SharedPreferencesManager.set);
        boolean postSms = sharedPreferencesManager.getBool("postSms", false);
        if (!postSms) return;
        String url = sharedPreferencesManager.getString("url", null);
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        int slot = bundle.getInt("android.telephony.extra.SLOT_INDEX", -1);//接收卡槽
        String sender = null;
        long timestampMillis = 0;
        StringBuilder builder = new StringBuilder();
        for (SmsMessage smsMessage : smsMessages) {
            sender = smsMessage.getOriginatingAddress();  //发送人号码
            String messageBody = smsMessage.getMessageBody();//短信内容
            builder.append(messageBody);
            timestampMillis = smsMessage.getTimestampMillis();//时间
        }
        if (sender == null) return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", sender);
        jsonObject.put("messageBody", builder.toString());
        jsonObject.put("timestampMillis", timestampMillis);
        jsonObject.put("slot", slot);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = FormBody.create(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder().url(url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(this);
    }

    private void insertSms(Context context, Intent intent) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, "sms", null, 1);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        database.beginTransaction();
        int slot = bundle.getInt("android.telephony.extra.SLOT_INDEX", -1);//接收卡槽
        String sender = null;
        long timestampMillis = 0;
        StringBuilder builder = new StringBuilder();
        for (SmsMessage smsMessage : smsMessages) {
            sender = smsMessage.getOriginatingAddress();  //发送人号码
            String messageBody = smsMessage.getMessageBody();//短信内容
            builder.append(messageBody);
            timestampMillis = smsMessage.getTimestampMillis();//时间
        }
        if (sender == null) return;
        ContentValues contentValues = new ContentValues();
        contentValues.put("sender", sender);
        contentValues.put("message", builder.toString());
        contentValues.put("sim", slot);
        contentValues.put("send", 1);
        contentValues.put("sms_send", 0);
        contentValues.put("timestamp", timestampMillis);
        if (dbOpenHelper.isStringExists(database, "sms", "sender", sender)) {
            dbOpenHelper.updateParameter(database, "sms", contentValues, "sender=?", new String[]{sender});
            database.insert("sms_message", null, contentValues);
        } else {
            database.insert("sms", null, contentValues);
            database.insert("sms_message", null, contentValues);
        }
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context, SharedPreferencesManager.set);
        boolean verification = sharedPreferencesManager.getBool("verification", false);
        if (verification) {
            String verify = DynamicSmsVerifyCode.extractVerificationCode(builder.toString());
            if (verify != null) {
                DynamicSmsVerifyCode.copyToClipboard(context, verify);
                Toast.makeText(context, R.string.verify_copy, Toast.LENGTH_SHORT).show();
            }
        }
        sendNotification(context, sender, sender, builder.toString());
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        dbOpenHelper.close();
        context.sendBroadcast(new Intent("sms"));//通知更新
    }


    public void sendNotification(Context context, String sender, String title, String message) {
        // 创建一个意图，用于点击通知时打开的 Activity
        Intent intent = new Intent(context, SmsDetailedActivity.class);
        intent.putExtra("sender", sender);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        // 创建通知构建器
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher) // 设置通知的小图标
                        .setContentTitle(title) // 设置通知标题
                        .setContentText(message) // 设置通知内容
                        .setAutoCancel(true) // 设置点击通知后自动取消
                        .setContentIntent(pendingIntent); // 设置点击通知后的意图
        // 获取 NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 如果 Android 版本高于等于 Android 8.0（Oreo），需要创建通知渠道
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);
        // 发送通知
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {

    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

    }
}
