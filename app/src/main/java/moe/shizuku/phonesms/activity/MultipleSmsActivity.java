package moe.shizuku.phonesms.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.app.AppXCompatActivity;
import moe.shizuku.phonesms.databinding.ActivityMultipleSmsBinding;
import moe.shizuku.phonesms.sqlite.DBOpenHelper;
import moe.shizuku.phonesms.util.Handler;
import moe.shizuku.phonesms.util.OnHandler;
import moe.shizuku.phonesms.util.ReceiverUtil;
import moe.shizuku.phonesms.util.StringUtil;

public class MultipleSmsActivity extends AppXCompatActivity<ActivityMultipleSmsBinding> implements View.OnClickListener, OnHandler {
    private final Handler handler = new Handler(Looper.getMainLooper(), this);
    private final ReceiverUtil receiverUtil = new ReceiverUtil();
    private SendSmsTask sendSmsTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initReceiver();
        initView();
        initEdit();
    }

    private void initReceiver() {
        receiverUtil.init(this, new IntentFilter("SMS_SENT"));
        receiverUtil.setOnReceiver(new ReceiverUtil.OnReceiver() {
            @Override
            public void onReceive(BroadcastReceiver receiver, Context context, Intent intent) {
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
    }

    private void initEdit() {
        binding.phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int newLineCount = StringUtil.countNewLines(s.toString());
                binding.phoneSize.setText(String.valueOf(newLineCount));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.messageSize.setText(s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initView() {
        binding.post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (binding.post.getId() == v.getId()) {
            if (sendSmsTask != null) {
                sendSmsTask.setRun(false);
                sendSmsTask =null;
            }
            String phone = binding.phone.getText().toString();
            String[] phones = phone.split("\n");
            String message = binding.message.getText().toString();
            String interval = binding.interval.getText().toString();
            long intervalSize = 0;
            if (!interval.isEmpty()) {
                intervalSize = Long.parseLong(interval);
            }
            sendSmsTask = new SendSmsTask(this, phones, message, intervalSize);
            sendSmsTask.start();
            binding.post.setText(R.string.stop_send);
        }
    }

    @Override
    public void handleMessage(int w, String str) {
        if (w == 0) {
            binding.log.setText(str);
        } else if (w == 1) {
            binding.post.setText(R.string.send_sms);
        }
    }

    private class SendSmsTask extends Thread {
        private final Context context;
        private final String[] phones;
        private final String message;
        private final long intervalSize;
        private boolean isRun = false;


        public SendSmsTask(Context context, String[] phones, String message, long intervalSize) {
            this.context = context;
            this.phones = phones;
            this.message = message;
            this.intervalSize = intervalSize;
        }

        public void setRun(boolean run) {
            isRun = run;
        }

        @Override
        public void run() {
            super.run();
            for (String phone : phones) {
                if (isRun) break;
                handler.sendMessage(0, phone);
                sendSms(phone, message);
                try {
                    Thread.sleep(intervalSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            handler.sendMessage(1, "");
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
            insertSms(sender, context, contentValues);
            SmsManager smsManager = SmsManager.getDefault();
            Intent intent = new Intent("SMS_SENT");
            intent.putExtra("timestamp", time);
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
            smsManager.sendTextMessage(sender, null, text, sentIntent, null);
        }

        private void insertSms(String sender, Context context, ContentValues contentValues) {
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
}
