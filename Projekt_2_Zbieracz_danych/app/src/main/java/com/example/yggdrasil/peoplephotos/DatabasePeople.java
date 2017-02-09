package com.example.yggdrasil.peoplephotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by Yggdrasil on 2016-11-27.
 */

public class DatabasePeople extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="databasepeople.db";
    public static final String TABLE_NAME="People";
    public static final String COL_ID="id";
    public static final String COL_NAME="name";
    public static final String COL_SURNAME="surname";
    public static final String COL_BIRTHDATE="birthDate";
    public static final String COL_PHOTO_PATH="photoPath";

    public DatabasePeople(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_PEOPLE=
                "CREATE TABLE "+TABLE_NAME+"("+
                        COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        COL_NAME+" TEXT,"+
                        COL_SURNAME+" TEXT,"+
                        COL_BIRTHDATE+" TEXT,"+
                        COL_PHOTO_PATH+" TEXT);";
        db.execSQL(CREATE_TABLE_PEOPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPerson(String name,String surname,String birthDate, String photoPath){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL_NAME,name);
        values.put(COL_SURNAME,surname);
        values.put(COL_BIRTHDATE,birthDate);
        values.put(COL_PHOTO_PATH,photoPath);
        db.insertOrThrow(TABLE_NAME,null,values);
    }

    public Cursor getRow()
    {
        String[] columns={COL_ID,COL_NAME,COL_SURNAME,COL_BIRTHDATE,COL_PHOTO_PATH};
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,columns,null,null,null,null,null);
        return cursor;
    }

    public void deletePerson(int id){
        String[] columns={COL_ID,COL_NAME,COL_SURNAME,COL_BIRTHDATE,COL_PHOTO_PATH};
        SQLiteDatabase dba=getReadableDatabase();
        String[] argss={""+id};
        Cursor cursor=dba.query(TABLE_NAME,columns,"id=?",argss,null,null,null);
        while(cursor.moveToNext()) {
            String photoPath = cursor.getString(3);
            File file=new File(photoPath);
            file.delete();
        }

        SQLiteDatabase db=getWritableDatabase();
        String[] args={""+id};
        db.delete(TABLE_NAME,"id=?",args);
    }

    public void updatePerson(int id, String name, String surname,String birthDate, String photoPath){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL_NAME,name);
        values.put(COL_SURNAME,surname);
        values.put(COL_BIRTHDATE,birthDate);
        values.put(COL_PHOTO_PATH, photoPath);
        String[] args={""+id};
        db.update(TABLE_NAME,values,"id=?",args);
    }
}
