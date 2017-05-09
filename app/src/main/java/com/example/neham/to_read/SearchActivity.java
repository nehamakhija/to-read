package com.example.neham.to_read;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.neham.to_read.db.BookDbHelper;
import com.example.neham.to_read.db.Contract;

public class SearchActivity extends AppCompatActivity {

    public SimpleCursorAdapter mAdapter;
    public BookDbHelper mHelper;
    public ListView mList;
    public TextWatcher mTextWatcher;

    private Typeface luna;
    private Typeface poetsen;


    public static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mHelper = new BookDbHelper(this);
        mList = (ListView) findViewById(R.id.mList);

        poetsen = Typeface.createFromAsset(getAssets(), "fonts/PoetsenOne.ttf");
        luna = Typeface.createFromAsset(getAssets(), "fonts/Luna.ttf");


        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = mAdapter.getCursor();
                String item = c.getString(c.getColumnIndex(Contract.BookEntry._ID));
                Intent intent = new Intent(SearchActivity.this, AddBook.class);
                intent.putExtra("EditRecord", true);
                intent.putExtra("BookID", item);
                startActivity(intent);
            }
        });

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText searchbox = (EditText) findViewById(R.id.searchbox);
                displayList(s.toString());
            }
        };
        EditText searchbox = (EditText) findViewById(R.id.searchbox);
        searchbox.addTextChangedListener(mTextWatcher);
        searchbox.setTypeface(luna);
    }

    public void displayList(String searchtext) {

        searchtext = "%" + searchtext + "%";
        //updates the list to get books from db,and formats it
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Contract.BookEntry.TABLE,    //table name
                new String[]{Contract.BookEntry.COL_BOOK_NAME, Contract.BookEntry._ID},
                Contract.BookEntry.COL_BOOK_NAME + " LIKE ? OR " +
                        Contract.BookEntry.COL_BOOK_AUTHOR + " LIKE ? OR " +
                        Contract.BookEntry.COL_BOOK_DESCRIPTION + " LIKE ? ",
                new String[]{searchtext, searchtext, searchtext},
                null,
                null,
                null
        );
        Log.wtf(TAG, cursor.toString());
        if (mAdapter == null) {
            mAdapter = new SimpleCursorAdapter(this,
                    R.layout.book_thumbnail, cursor,
                    new String[]{Contract.BookEntry.COL_BOOK_NAME, Contract.BookEntry._ID},
                    new int[]{R.id.book_entry_name, R.id.book_entry_id},
                    0);
            mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    TextView tv = (TextView) view.findViewById(R.id.book_entry_name);
                    if (tv != null) {
                        tv.setTypeface(poetsen);
                    }
                    return false;
                }
            });
            mList.setAdapter(mAdapter);
        } else {
            mAdapter.changeCursor(cursor);
        }
        db.close();
    }

}
