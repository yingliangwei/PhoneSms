package moe.shizuku.phonesms.util;

import android.content.Context;
import android.widget.Toast;

import com.baolian.network.util.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import moe.shizuku.phonesms.R;

public class LoginUtil {
    public static JSONObject toJson(Context context) throws JSONException {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context, SharedPreferencesManager.user);
        String _key = sharedPreferencesManager.getString("key", null);
        String id = sharedPreferencesManager.getString("id", null);
        if (_key == null) throw new JSONException(context.getString(R.string.noLogin));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("key", _key);
        return jsonObject;
    }

    public static boolean showLogin(Context context) {
        if (isLogin(context)) {
            return true;
        }
        Toast.makeText(context, "未登录", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean isLogin(Context context) {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context, SharedPreferencesManager.user);
        String _key = sharedPreferencesManager.getString("key", null);
        return (_key != null);
    }
}
