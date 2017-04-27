package com.example.neham.to_read;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.neham.to_read.db.BookDbHelper;
import com.example.neham.to_read.db.Contract;


public class MainActivity extends AppCompatActivity {


    public SimpleCursorAdapter mAdapter;
    public BookDbHelper mHelper;
    public ListView mList;

    private Typeface luna;
    private Typeface poetsen1;
    private Typeface cl;

    public static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //load fonts
        luna=Typeface.createFromAsset(getAssets(),"fonts/Luna.ttf");
        cl=Typeface.createFromAsset(getAssets(),"fonts/CeriaLebaran.otf");
        poetsen1=Typeface.createFromAsset(getAssets(),"fonts/PoetsenOne.ttf");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper=new BookDbHelper(this);
        mList=(ListView)findViewById(R.id.mList);
        updateUI();

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c=mAdapter.getCursor();
                String item=c.getString(c.getColumnIndex(Contract.BookEntry._ID));
                Intent intent = new Intent(MainActivity.this, AddBook.class);
                intent.putExtra("EditRecord",true);
                intent.putExtra("BookID",item);
                startActivity(intent);
            }
        });
        TextView heading=(TextView)findViewById(R.id.AppTitle);
        heading.setTypeface(cl);
    }

    @Override
    protected void onResume(){
        updateUI();
        super.onResume();
    }

    public void addBook(View view){
        //move to add book activity
        Intent intent=new Intent(this, AddBook.class);

        intent.putExtra("EditRecord",false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void updateUI(){

        //updates the list to get books from db,and formats it
        Log.wtf(TAG,"updating ui notice meeeeeeeeeeeee");
        SQLiteDatabase db=mHelper.getReadableDatabase();
        Cursor cursor=db.query(
                Contract.BookEntry.TABLE,    //table name
                new String[]{Contract.BookEntry.COL_BOOK_NAME, Contract.BookEntry._ID},
                null,
                null,
                null,
                null,
                null
        );
        Log.wtf(TAG,cursor.toString());
        if(mAdapter==null){
            mAdapter=new SimpleCursorAdapter(this,
                    R.layout.book_thumbnail, cursor,
                    new String[]{Contract.BookEntry.COL_BOOK_NAME,Contract.BookEntry._ID},
                    new int[]{R.id.book_entry_name, R.id.book_entry_id},
                    0);
            mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
                public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                    TextView tv=(TextView)view.findViewById(R.id.book_entry_name);
                    if(tv!=null){

                        tv.setTypeface(poetsen1);

                    }
                    return false;
                }
            });
            mList.setAdapter(mAdapter);
        }
        else{
            mAdapter.changeCursor(cursor);
        }
        db.close();
    }

}
