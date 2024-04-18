package moe.shizuku.phonesms.util;

public class StringUtil {
    // 统计换行符的数量
    public static int countNewLines(String text) {
        int newLineCount = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                newLineCount++;
            }
        }
        return newLineCount;
    }
}
