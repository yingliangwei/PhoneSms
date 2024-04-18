package moe.shizuku.phonesms.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE sms (\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "sender TEXT,\n" +
                "message TEXT,\n" +
                "sim int,\n" +
                "send int,\n" +
                "sms_send int,"+
                "timestamp DATETIME\n" +
                ");";
        db.execSQL(sql);
        String sql1 = "CREATE TABLE sms_message (\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "sender TEXT,\n" +
                "message TEXT,\n" +
                "sim int,\n" +
                "send int,\n" +
                "sms_send int,"+
                "timestamp DATETIME\n" +
                ");";
        db.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean isStringExists(SQLiteDatabase db, String tableName, String columnName, String searchString) {
        // 执行查询
        Cursor cursor = db.query(
                tableName,       // 表名
                new String[]{columnName},  // 要查询的列名
                columnName + "=?",  // WHERE 子句
                new String[]{searchString}, // WHERE 子句的参数
                null,            // GROUP BY 子句
                null,            // HAVING 子句
                null             // ORDER BY 子句
        );

        // 检查结果
        boolean exists = (cursor.getCount() > 0);

        // 关闭 Cursor
        cursor.close();

        return exists;
    }

    public int updateParameter(SQLiteDatabase db, String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        // 执行更新操作
        return db.update(tableName, values, whereClause, whereArgs);
    }

}
