package com.example.neham.to_read.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper{

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Contract.BookEntry.TABLE + " (" +
                    Contract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    Contract.BookEntry.COL_BOOK_NAME+ " TEXT," +
                    Contract.BookEntry.COL_BOOK_AUTHOR + " TEXT," +
                    Contract.BookEntry.COL_BOOK_DESCRIPTION + " TEXT)";

    public BookDbHelper(Context context){
        super(context,Contract.DB_NAME,null,Contract.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
