package com.example.shashank.enigmaproxy.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

/**
 * Created by Shashank on 04-11-2016.
 */

public class MyDBHandler {


    private MyDBHelper helper;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;
    private OnDBUpdateListener listener;
    private static MyDBHandler handler;


    public static MyDBHandler getInstance(Context context){
        if(handler==null){
            handler=new MyDBHandler(context);
        }
        return handler;
    }

    private  MyDBHandler(Context context){
        helper=new MyDBHelper(context);
        dbWrite=helper.getWritableDatabase();
        dbRead=helper.getReadableDatabase();
    }


    public long insert(String tablename, ContentValues cv){

        long x=dbWrite.insert(tablename,null,cv);

        if(listener!=null) {
            Bundle b=new Bundle();
          b.putString("uid",cv.getAsString("uid"));
          b.putString("name",cv.getAsString("name"));
          b.putString("mtype",cv.getAsString("mtype"));
          b.putString("utype",cv.getAsString("utype"));
          b.putString("propic",cv.getAsString("propic"));
          b.putString("message",cv.getAsString("message"));
            listener.onUpdate(b);
        }
        return x;
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return dbRead.query(table,columns,selection,selectionArgs,groupBy,having,orderBy);
    }

    public Cursor getStatusUpdate() {
        //return the entire databse for  initial set-up  of the app
        return  dbRead.query("timeline", null, null, null, null, null, "_id");
    }

    public int getLatestTID() {

        Cursor cursor = dbRead.query("timeline", null, null, null, null, null, "tid");
        int tid = 0;
        if (cursor.moveToLast()) {
            tid = cursor.getInt(cursor.getColumnIndex("tid"));

        }
        cursor.close();
        return tid;


    }



    public interface OnDBUpdateListener{
        void onUpdate(Bundle b);
    }

    public void setOnDBUpdateListener(OnDBUpdateListener l){
        listener=l;
    }





    private class MyDBHelper extends SQLiteOpenHelper {

        public static final String TAG="DBLOG";
        static final int VERSION = 4;
        static final String DATABASE = "timeline.db";
        static final String TABLE = "timeline";
        static final String ID = "_id";
        static final String NAME = "name";
        static final String UID = "uid";
        static final String UTYPE = "utype";
        static final String MTYPE = "mtype";
        static final String PROPIC = "propic";
        static final String MESSAGE = "message";
        static final String TID = "tid";

        static final String PROFILETABLE="profile";


        public MyDBHelper(Context context) {
            super(context, DATABASE, null, VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE + " (" + ID + " int primary key, " + TID + " int, " + UID + " int, " + UTYPE +
                    " text, " + MTYPE + " text, " + NAME + " text, " + PROPIC + " text, " + MESSAGE + " text)");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            db.execSQL("drop table " + TABLE);
            this.onCreate(db);
        }



    }


}
