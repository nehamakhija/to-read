package com.example.neham.to_read.db;

import android.provider.BaseColumns;

public final class Contract {
    public static final String DB_NAME="com.example.neham.to_read.db";
    public static final int DB_VERSION=1;

    private Contract(){
        //to prevent accidental initialization
    }
    public class BookEntry implements BaseColumns {

        public static final String TABLE="book";
        public static final String COL_BOOK_NAME="title";
        public static final String COL_BOOK_AUTHOR="author";
        public static final String COL_BOOK_DESCRIPTION="desc";
    }

}
