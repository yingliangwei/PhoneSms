package moe.shizuku.phonesms.util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import moe.shizuku.phonesms.R;

public class TimeUtils {
    public static String getTimeFromTimestamp(Context context, long timestamp) {
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();
        // 创建Calendar实例，并设置时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        // 获取时间的年月日
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，需要加1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 将时间戳转换为日期时间
        Date date = new Date(timestamp);
        // 创建SimpleDateFormat实例，用于格式化时间
        SimpleDateFormat sdf;
        // 判断日期是否是今天或前天
        if (isToday(timestamp, currentTime)) {
            // 如果是今天，则返回小时和分钟
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else if (isYesterday(timestamp, currentTime)) {
            // 如果是昨天，则返回月和天
            sdf = new SimpleDateFormat(String.format("MM%sdd%s", context.getString(R.string.month), context.getString(R.string.day)), Locale.getDefault());
        } else {
            // 否则返回年月日
            sdf = new SimpleDateFormat(String.format("yyyy%sMM%sdd%s", context.getString(R.string.year), context.getString(R.string.month), context.getString(R.string.day)), Locale.getDefault());
        }
        // 格式化时间并返回结果
        return sdf.format(date);
    }

    private static boolean isToday(long timestamp, long currentTime) {
        // 获取当前时间的年月日
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentTime);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        // 获取时间戳的年月日
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 比较年月日是否相同
        return year == currentYear && month == currentMonth && day == currentDay;
    }

    private static boolean isYesterday(long timestamp, long currentTime) {
        // 获取昨天的日期时间戳
        long yesterdayTime = currentTime - 24 * 60 * 60 * 1000;

        // 判断时间戳是否在昨天的时间范围内
        return timestamp >= yesterdayTime && timestamp < currentTime;
    }
}
