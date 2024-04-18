package moe.shizuku.phonesms.sms.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.adapter.TextAdapter;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivitySmsDetailedBinding;
import moe.shizuku.phonesms.entity.SmsEntity;
import moe.shizuku.phonesms.sqlite.DBOpenHelper;
import moe.shizuku.phonesms.util.Handler;
import moe.shizuku.phonesms.util.OnHandler;
import moe.shizuku.phonesms.util.ReceiverUtil;

public class SmsDetailedActivity extends AppXCompatActivity<ActivitySmsDetailedBinding> implements Runnable, OnHandler, View.OnClickListener {
    public static String SENDER = "sender";
    private final List<SmsEntity> strings = new ArrayList<>();
    private TextAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private String sender;
    private final ReceiverUtil receiverUtil = new ReceiverUtil();
    private final ReceiverUtil receiverUtil1 = new ReceiverUtil();

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        initReceiver();
        initRecycler();
        initData();
        initView();
    }

    private void initIntent() {
        sender = getIntent().getStringExtra(SENDER);
        if (sender == null) {
            // 获取Intent中的Uri
            Uri smsUri = getIntent().getData();
            if (smsUri != null) {
                sender = smsUri.getSchemeSpecificPart();
            }
        }
        if (sender == null) {
            finish();
        }
        binding.toolbar.setTitle(sender);
    }

    private void initReceiver() {
        receiverUtil.init(this, new IntentFilter("sms"));
        receiverUtil.setOnReceiver(new ReceiverUtil.OnReceiver() {
            @Override
            public void onReceive(BroadcastReceiver receiver, Context context, Intent intent) {
                initData();
            }
        });
        receiverUtil1.init(this, new IntentFilter("SMS_SENT"));
        receiverUtil1.setOnReceiver(new ReceiverUtil.OnReceiver() {
            @Override
            public void onReceive(BroadcastReceiver receiver, Context context, Intent intent) {
                if (receiver.getResultCode() == -1) {
                    Toast.makeText(context, R.string.send_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.send_success, Toast.LENGTH_SHORT).show();
                }
                long timestamp = intent.getLongExtra("timestamp", 0);
                update(context, timestamp, receiver.getResultCode());
            }

            private void update(Context context, long timestamp, int code) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("sms_send", code);

                DBOpenHelper dbOpenHelper = new DBOpenHelper(context, "sms", null, 1);
                SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
                database.update("sms", contentValues, "timestamp=?", new String[]{String.valueOf(timestamp)});
                DBOpenHelper dbOpenHelper1 = new DBOpenHelper(context, "sms_message", null, 1);
                SQLiteDatabase database1 = dbOpenHelper.getReadableDatabase();
                database1.update("sms_message", contentValues, "timestamp=?", new String[]{String.valueOf(timestamp)});
                database1.close();
                dbOpenHelper1.close();
                database.close();
                dbOpenHelper.close();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiverUtil.unregister(this);
        receiverUtil1.unregister(this);
    }

    private void initView() {
        binding.send.setOnClickListener(this);
    }

    private void initData() {
        String sms_body = getIntent().getStringExtra("sms_body");
        if (sms_body != null) {
            binding.message.setText(sms_body);
        }
        strings.clear();
        new Thread(this).start();
    }

    private void initRecycler() {
        adapter = new TextAdapter(this, strings);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void handleMessage(int w) {
        if (w == 0) {
            adapter.notifyItemChanged(strings.size());
        } else if (w == 1) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recycler.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
            }
        }
    }

    @Override
    public void run() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this, "sms", null, 1);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("sms_message", new String[]{"sender", "message", "sim", "timestamp", "send"}, "sender=?", new String[]{sender}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int senderIndex = cursor.getColumnIndex("sender");
            int messageIndex = cursor.getColumnIndex("message");
            int simIndex = cursor.getColumnIndex("sim");
            int timestampIndex = cursor.getColumnIndex("timestamp");
            int send = cursor.getColumnIndex("send");
            int sms_send = cursor.getColumnIndex("sms_send");
            if (sms_send != -1 && senderIndex != -1 && messageIndex != -1 && simIndex != -1 && timestampIndex != -1 && send != -1) {
                do {
                    String sender = cursor.getString(senderIndex);
                    String message = cursor.getString(messageIndex);
                    int sim = cursor.getInt(simIndex);
                    long timestamp = cursor.getLong(timestampIndex);
                    int sendX = cursor.getInt(send);
                    int sms_sendX = cursor.getInt(sms_send);
                    strings.add(new SmsEntity(sender, message, timestamp, sim, sendX, sms_sendX));
                    handler.sendMessage(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        dbOpenHelper.close();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.send.getId()) {
            String text = binding.message.getText().toString();
            sendSms(sender, text);
        }
    }

    private void sendSms(String sender, String text) {
        if (text.isEmpty()) return;
        long time = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put("sender", sender);
        contentValues.put("message", text);
        contentValues.put("sim", 0);
        contentValues.put("send", 2);
        contentValues.put("sms_send", 0);
        contentValues.put("timestamp", time);
        insertSms(this, contentValues);
        SmsManager smsManager = SmsManager.getDefault();
        Intent intent = new Intent("SMS_SENT");
        intent.putExtra("timestamp", time);
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
        smsManager.sendTextMessage(sender, null, text, sentIntent, null);
    }

    private void insertSms(Context context, ContentValues contentValues) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, "sms", null, 1);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        database.beginTransaction();
        if (dbOpenHelper.isStringExists(database, "sms", "sender", sender)) {
            dbOpenHelper.updateParameter(database, "sms", contentValues, "sender=?", new String[]{sender});
            database.insert("sms_message", null, contentValues);
        } else {
            database.insert("sms", null, contentValues);
            database.insert("sms_message", null, contentValues);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        dbOpenHelper.close();
        context.sendBroadcast(new Intent("sms"));//通知更新
    }
}
