package moe.shizuku.phonesms.util;

import android.widget.CompoundButton;
import android.widget.EditText;

public class EditUtil {

    public static void PlainText(CompoundButton button, EditText editText) {
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditUtil.PlainText(editText, isChecked);
            }
        });
    }

    public static void PlainText(EditText editText, boolean is) {
        if (is) {
            showPlainText(editText);
        } else {
            hidePlainTextWithDelay(editText);
        }
    }

    public static void showPlainText(EditText editText) {
        editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        editText.setSelection(editText.getText().length());
    }

    public static void hidePlainTextWithDelay(EditText editText) {
        editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setSelection(editText.getText().length());
    }
}
