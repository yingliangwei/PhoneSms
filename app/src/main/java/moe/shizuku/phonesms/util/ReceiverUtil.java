package moe.shizuku.phonesms.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

public class ReceiverUtil {
    public final Receiver receiver = new Receiver();
    private OnReceiver onReceiver;

    public void init(Context activity, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            activity.registerReceiver(receiver, filter);
        }
    }

    public void setOnReceiver(OnReceiver onReceiver) {
        this.onReceiver = onReceiver;
    }

    public void unregister(Context activity) {
        activity.unregisterReceiver(receiver);
    }

    public interface OnReceiver {

        default void onReceive(BroadcastReceiver receiver, Context context, Intent intent) {

        }
    }

    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (onReceiver != null) {
                onReceiver.onReceive(this, context, intent);
            }
        }
    }
}
