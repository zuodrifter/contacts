package com.example.materialtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DATABASE_NAME="ui.db";
    //数据库版本号
    private static final int DATABASE_VERSION=1;


    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    //建表UI
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table UI( account varchar(30) primary key,password varchar(30))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}