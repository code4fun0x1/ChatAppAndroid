package com.example.shashank.enigmaproxy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shashank on 30-04-2016.
 */
public class StatusData {


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

    class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
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


    //making it final means it is creted only once whichever part of the app request for it first
    private final DbHelper dbhelper;

    public StatusData(Context context) {
        this.dbhelper = new DbHelper(context);

    }


    public void close() {
        this.dbhelper.close();
    }

    public void insert(ContentValues values) {
        SQLiteDatabase db = this.dbhelper.getWritableDatabase();
        db.insertOrThrow(TABLE, null, values);
        db.close();
    }

    public Cursor getStatusUpdate() {
        //return the entire databse for  initial set-up  of the app
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        return  db.query(TABLE, null, null, null, null, null, ID);
    }

    public int getLatestTID() {

        SQLiteDatabase db = this.dbhelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, null, null, null, null, TID);
        int tid = 0;
        if (cursor.moveToLast()) {
                tid = cursor.getInt(cursor.getColumnIndex("tid"));

        }
        cursor.close();
        return tid;


    }

    public Cursor getLatestUpdate(int x) {
        //return the entire databse for  initial set-up  of the app

        String a[]={x+""};
        SQLiteDatabase db = this.dbhelper.getReadableDatabase();


        return db.query(TABLE,null,"tid >?",a,null,null,ID);
    }


}
