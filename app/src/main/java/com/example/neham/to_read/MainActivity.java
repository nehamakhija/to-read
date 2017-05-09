package com.example.neham.to_read;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neham.to_read.db.BookDbHelper;
import com.example.neham.to_read.db.Contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


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
        poetsen1 = Typeface.createFromAsset(getAssets(), "fonts/Luna.ttf");

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

    public void exportDb(View view) {

        //convert to csv
        String filename = "mytoread.csv";
        exportToCSV(filename);
        Uri uri = new MyFileProvider(filename).getDatabaseURI(this);
        Log.d(TAG, uri.toString());

        //send intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My to read list");
        intent.putExtra(Intent.EXTRA_TEXT, "Attached is the csv file of your to-read list!");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(intent, "Export as CSV"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void exportToCSV(String filename) {

        File csvFile = new File(getFilesDir(), filename);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Contract.BookEntry.TABLE,    //table name
                new String[]{
                        Contract.BookEntry._ID,
                        Contract.BookEntry.COL_BOOK_NAME,
                        Contract.BookEntry.COL_BOOK_AUTHOR,
                        Contract.BookEntry.COL_BOOK_DESCRIPTION},
                null,
                null,
                null,
                null,
                null
        );

        try {
            FileWriter fw = new FileWriter(csvFile);
            BufferedWriter bw = new BufferedWriter(fw);
            int columncount = cursor.getColumnCount();
            bw.write(Contract.BookEntry._ID + "," +
                    Contract.BookEntry.COL_BOOK_NAME + "," +
                    Contract.BookEntry.COL_BOOK_AUTHOR + "," +
                    Contract.BookEntry.COL_BOOK_DESCRIPTION + "\n");
            while (cursor.moveToNext()) {
                for (int i = 0; i < columncount; i++) {
                    String str = cursor.getString(i);
                    bw.write("\"" + str + "\"");

                    if (i == columncount - 1) {
                        bw.write("\n");
                    } else {
                        bw.write(",");
                    }
                }
            }
            bw.close();
            fw.close();
            Log.d(TAG, "file seems created okay");
            logFileContents(filename);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

    }

    public void logFileContents(String filename) {
        BufferedReader br;
        FileReader fr;

        try {
            File csvFile = new File(getFilesDir(), filename);
            fr = new FileReader(csvFile);
            br = new BufferedReader(fr);

            String s;

            while ((s = br.readLine()) != null) {
                Log.d(TAG, s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateUI(){

        //updates the list to get books from db,and formats it
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
