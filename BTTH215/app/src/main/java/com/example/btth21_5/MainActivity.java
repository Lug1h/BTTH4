package com.example.btth21_5;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Declare UI elements
    EditText edtMalop, edtTenlop, edtSiso;
    Button btnThem, btnXoa, btnUpdate, btnQuery;
    ListView lvList;
    ArrayList<String> myList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        edtMalop = findViewById(R.id.edtClassCode);
        edtTenlop = findViewById(R.id.edtNameClass);
        edtSiso = findViewById(R.id.edtNumberStd);
        btnThem = findViewById(R.id.btnInsert);
        btnXoa = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnQuery = findViewById(R.id.btnQuery);

        // Initialize ListView and adapter
        lvList = findViewById(R.id.lvListName);
        myList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        lvList.setAdapter(myAdapter);

        // Create or open the database
        myDatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);

        // Create table if not exists
        try {
            String sql = "CREATE TABLE IF NOT EXISTS tbllop(malop TEXT PRIMARY KEY, tenlop TEXT, siso INTEGER)";
            myDatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table already exists");
        }

        // Insert data into table
        btnThem.setOnClickListener(view -> {
            String malop = edtMalop.getText().toString();
            String tenlop = edtTenlop.getText().toString();
            int siso = Integer.parseInt(edtSiso.getText().toString());
            ContentValues myValue = new ContentValues();
            myValue.put("malop", malop);
            myValue.put("tenlop", tenlop);
            myValue.put("siso", siso);
            String msg;
            if (myDatabase.insert("tbllop", null, myValue) == -1) {
                msg = "Failed to insert record!";
            } else {
                msg = "Insert record success";
                refreshListView();
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });

        // Delete data from table
        btnXoa.setOnClickListener(view -> {
            String malop = edtMalop.getText().toString();
            int n = myDatabase.delete("tbllop", "malop = ?", new String[]{malop});
            String msg;
            if (n == 0) {
                msg = "No record to delete";
            } else {
                msg = n + " record(s) deleted";
                refreshListView();
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });

        // Update data in table
        btnUpdate.setOnClickListener(view -> {
            int siso = Integer.parseInt(edtSiso.getText().toString());
            String malop = edtMalop.getText().toString();
            ContentValues myValue = new ContentValues();
            myValue.put("siso", siso);
            int n = myDatabase.update("tbllop", myValue, "malop = ?", new String[]{malop});
            String msg;
            if (n == 0) {
                msg = "No record to update";
            } else {
                msg = n + " record(s) updated";
                refreshListView();
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });

        // Query data from table
        btnQuery.setOnClickListener(view -> refreshListView());
    }

    private void refreshListView() {
        myList.clear();
        Cursor c = myDatabase.query("tbllop", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                String data = c.getString(0) + " - " + c.getString(1) + " - " + c.getInt(2);
                myList.add(data);
            } while (c.moveToNext());
        }
        c.close();
        myAdapter.notifyDataSetChanged();
    }
}
