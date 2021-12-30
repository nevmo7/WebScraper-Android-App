package com.example.nevermore.webscraper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Never More on 1/29/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "clan.db";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "LEVEL";
    public static final String COL_3 = "POINTS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + "table_log" + " (NAME TEXT,DATE TEXT,SIZE INTEGER,CLCR TEXT,CLCRNAME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "table_log");
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String name, String level, Integer points, String TABLE_NAME){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,name);
        contentValues.put(COL_2,level);
        contentValues.put(COL_3,points);
        long result = sqLiteDatabase.insert(TABLE_NAME, null,contentValues);
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean insertInLog(String tableName, String dateCreated, Integer size, String clcr, String clcrName){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",tableName);
        contentValues.put("DATE",dateCreated);
        contentValues.put("SIZE",size);
        contentValues.put("CLCR",clcr);
        contentValues.put("CLCRNAME",clcrName);
        long result = sqLiteDatabase.insert("table_log", null,contentValues);
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public void createTable(String TABLE_NAME){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (NAME TEXT,LEVEL TEXT,POINTS INTEGER)");
    }

    public Cursor getAllData(String TABLE_NAME){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getAllLogData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from " + "table_log", null);
        return res;
    }

    public Integer deleteLogData(String TABLE_NAME){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete("table_log","NAME=?",new String[] {TABLE_NAME});
    }

    public void deleteData(String TABLE_NAME){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME); //delete table
    }

    public ArrayList getDbName(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ArrayList<String> dbNamesList = new ArrayList<>();
        Cursor dbNames = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (dbNames.moveToFirst()) {
            while ( !dbNames.isAfterLast() ) {
                dbNamesList.add( dbNames.getString(dbNames.getColumnIndex("name")));
                dbNames.moveToNext();
            }
        }
        return dbNamesList;
    }
}
