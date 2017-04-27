package com.example.neham.to_read;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.neham.to_read.db.BookDbHelper;
import com.example.neham.to_read.db.Contract;

public class AddBook extends AppCompatActivity {

    private BookDbHelper mHelper;
    private boolean editBook=false;//are we editing or creating new record?
    private String bookid;

    private EditText editTextTitle;
    private EditText editTextAuth;
    private EditText editTextDesc;

    public static final String TAG="AddBook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.wtf(TAG,"in the add screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Bundle extras=getIntent().getExtras();

        final View addBookView=findViewById(R.id.activity_add_book);
        addBookView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            @Override
            public void onSwipeLeft() {
                Log.d(TAG,"Swipe left");
                addButtonClicked(addBookView);
            }

            @Override
            public void onSwipeRight() {
                Log.d(TAG,"Swipe right");

                addButtonClicked(addBookView);
            }
        });
        Log.d(TAG,"Swipe listener set");

        mHelper=new BookDbHelper(this);

        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        editTextAuth = (EditText)findViewById(R.id.editTextAuth);
        editTextDesc = (EditText)findViewById(R.id.editTextDesc);

        if(extras!=null){
            editBook=extras.getBoolean("EditRecord");
            if(editBook){
                bookid=extras.getString("BookID");

            }
        }
        setPlaceholders();

        Typeface luna= Typeface.createFromAsset(getAssets(),"fonts/Luna.ttf");
        editTextTitle.setTypeface(luna);
        editTextAuth.setTypeface(luna);
        editTextDesc.setTypeface(luna);
    }
    public void addButtonClicked(View view){
        if(editBook){
            edit();
        }
        else{
            add();
        }
        finish();

    }
    public void deleteButtonClicked(View view){
        if(editBook){
            delete();
        }
        finish();
    }
    public void add(){
        Log.d(TAG,"We are adding a book");

        EditText editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        EditText editTextAuth = (EditText)findViewById(R.id.editTextAuth);
        EditText editTextDesc = (EditText)findViewById(R.id.editTextDesc);

        SQLiteDatabase db=mHelper.getWritableDatabase();

        ContentValues values=new ContentValues();

        values.put(Contract.BookEntry.COL_BOOK_NAME, editTextTitle.getText().toString());
        values.put(Contract.BookEntry.COL_BOOK_AUTHOR, editTextAuth.getText().toString());
        values.put(Contract.BookEntry.COL_BOOK_DESCRIPTION, editTextDesc.getText().toString());
        long mynewid = db.insert(Contract.BookEntry.TABLE, null, values);

        db.close();

    }
    public void edit(){
        EditText editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        EditText editTextAuth = (EditText)findViewById(R.id.editTextAuth);
        EditText editTextDesc = (EditText)findViewById(R.id.editTextDesc);

        ContentValues values=new ContentValues();

        values.put(Contract.BookEntry.COL_BOOK_NAME, editTextTitle.getText().toString());
        values.put(Contract.BookEntry.COL_BOOK_AUTHOR, editTextAuth.getText().toString());
        values.put(Contract.BookEntry.COL_BOOK_DESCRIPTION, editTextDesc.getText().toString());

        SQLiteDatabase db=mHelper.getWritableDatabase();

        db.update(Contract.BookEntry.TABLE,values,
                Contract.BookEntry._ID+"= ?",new String[]{bookid});
        db.close();

    }
    public void delete(){

        SQLiteDatabase db=mHelper.getWritableDatabase();
        db.delete(Contract.BookEntry.TABLE,Contract.BookEntry._ID+"= ?",new String[]{bookid});
        db.close();
    }

    public void setPlaceholders(){

        //if we are editing a book, we must set values to initial data first

        SQLiteDatabase db=mHelper.getReadableDatabase();

        if(editBook) {
            String auth="Author", desc="Description",title="Title";
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    Contract.BookEntry.COL_BOOK_NAME,
                    Contract.BookEntry.COL_BOOK_AUTHOR,
                    Contract.BookEntry.COL_BOOK_DESCRIPTION
            };
            // Filter results WHERE "title" = 'My Title'
            String selection = Contract.BookEntry._ID + " = ?";
            String[] selectionArgs = {bookid};


            Cursor cursor = db.query(
                    Contract.BookEntry.TABLE,                 // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    title = cursor.getString(
                            cursor.getColumnIndex(Contract.BookEntry.COL_BOOK_NAME));
                    auth = cursor.getString(
                            cursor.getColumnIndexOrThrow(Contract.BookEntry.COL_BOOK_AUTHOR));
                    desc = cursor.getString(
                            cursor.getColumnIndexOrThrow(Contract.BookEntry.COL_BOOK_DESCRIPTION));
                }
            }
            cursor.close();
            db.close();

            editTextTitle.setText(title);
            editTextAuth.setText(auth);
            editTextDesc.setText(desc);
        }


    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(0,0);
    }
}
