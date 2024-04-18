package moe.shizuku.phonesms.activity.pager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import moe.shizuku.phonesms.R;
import moe.shizuku.phonesms.activity.MultipleSmsActivity;
import moe.shizuku.phonesms.adapter.RecyclerAdapter;
import moe.shizuku.phonesms.adapter.SmsAdapter;
import moe.shizuku.phonesms.databinding.PagerSmsBinding;
import moe.shizuku.phonesms.entity.SmsEntity;
import moe.shizuku.phonesms.sms.activity.SmsDetailedActivity;
import moe.shizuku.phonesms.sqlite.DBOpenHelper;
import moe.shizuku.phonesms.util.ReceiverUtil;

public class SmsPager extends RecyclerAdapter<PagerSmsBinding> implements Runnable, Toolbar.OnMenuItemClickListener, ReceiverUtil.OnReceiver {
    private SmsAdapter smsAdapter;
    private final List<SmsEntity> entities = new ArrayList<>();
    private final ReceiverUtil receiverUtil = new ReceiverUtil();

    public SmsPager(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        receiverUtil.init(getContext(), new IntentFilter("sms"));
        receiverUtil.setOnReceiver(this);
        initRecycler();
        initData();
        initToolbar();
    }

    private void initToolbar() {
        getBinding().toolbar.setOnMenuItemClickListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        entities.clear();
        smsAdapter.notifyDataSetChanged();
        new Thread(this).start();
    }

    private void initRecycler() {
        smsAdapter = new SmsAdapter(getContext(), entities);
        getBinding().recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().recycler.setAdapter(smsAdapter);
    }

    @Override
    public void run() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getContext(), "sms", null, 1);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("sms", null, null, null, null, null, "timestamp DESC");
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
                    entities.add(new SmsEntity(sender, message, timestamp, sim, sendX, sms_sendX));
                    getBinding().recycler.post(new Runnable() {
                        @Override
                        public void run() {
                            smsAdapter.notifyItemChanged(entities.size());
                        }
                    });
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        dbOpenHelper.close();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.SingleSMS) {
            dialogInput();
        } else {
            getContext().startActivity(new Intent(getContext(), MultipleSmsActivity.class));
        }
        return false;
    }

    private void dialogInput() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));

        EditText editText = new EditText(getContext());
        LinearLayout.LayoutParams editLinear = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        editLinear.leftMargin = 50;
        editLinear.rightMargin = 50;
        editText.setLayoutParams(editLinear);
        linearLayout.addView(editText);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.InputPhone)
                .setView(linearLayout)
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), SmsDetailedActivity.class);
                    intent.putExtra(SmsDetailedActivity.SENDER, editText.getText().toString());
                    getContext().startActivity(intent);
                })
                .show();
    }

    @Override
    public void onReceive(BroadcastReceiver receiver, Context context, Intent intent) {
        initData();
    }
}
