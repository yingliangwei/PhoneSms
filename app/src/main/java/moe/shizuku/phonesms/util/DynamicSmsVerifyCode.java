package moe.shizuku.phonesms.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicSmsVerifyCode {
    public static String extractVerificationCode(String message) {
        String verificationCode = null;
        Pattern pattern = Pattern.compile("(\\d{4,6})"); // 匹配4到6位数字验证码
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            verificationCode = matcher.group(0);
        }
        return verificationCode;
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }
}