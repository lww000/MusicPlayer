package com.example.boomq.musicplayer.HistoryPlay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by boomq on 2019/3/23.
 */

public class MusicSQLiteHelper extends SQLiteOpenHelper {
    public MusicSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public long insert(String table, ContentValues values){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        long id=sqLiteDatabase.insert(table,null,values);
        sqLiteDatabase.close();
        return id;
    }

    public Cursor query(String sql,String selectionArgs[]){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        return sqLiteDatabase.rawQuery(sql,selectionArgs);
    }

    public void update(String sql,String bindArgs[]){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        sqLiteDatabase.execSQL(sql,bindArgs);
        sqLiteDatabase.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("TAG", "onCreate: ");

        String sql="create table playhistory("+"id integer primary key autoincrement,"+"data text not null,"+"playdate long not null)";
        sqLiteDatabase.execSQL(sql);
        Log.i("TAG", "table create ok! ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.i("TAG", "onUpgrade: ");
        sqLiteDatabase.execSQL("drop table if exists playhistory");
        onCreate(sqLiteDatabase);
    }
}
