package com.example.zjh.call_roll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZJH on 2016-10-25-0025.
 */

public class reduce_score_Activity extends Activity {

    ArrayAdapter<String> arrayAdapter;
    Spinner spinner_late;
    Spinner spinner_early;
    Spinner spinner_off;
    Spinner spinner_skip;
    List<String> lists;
    int late;
    int early;
    int off;
    int skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reduce_score_list_layout);
        lists_init();
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,lists);

        spinner_late=(Spinner)findViewById(R.id.spinner_late);
        spinner_early=(Spinner)findViewById(R.id.spinner_early);
        spinner_off=(Spinner)findViewById(R.id.spinner_off);
        spinner_skip=(Spinner) findViewById(R.id.spinner_skip);
        spinner_early.setAdapter(arrayAdapter);
        spinner_late.setAdapter(arrayAdapter);
        spinner_off.setAdapter(arrayAdapter);
        spinner_skip.setAdapter(arrayAdapter);
        readDatebase();
    }

    void readDatebase(){
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        Cursor cursor=db.query("reduce_info",new String[]{"late","skip","early","off"},null,null,null,null,null);
        if(cursor.getCount()==0)
        {
            late=0;
            early=0;
            off=0;
            skip=0;
        }
        else if(cursor!=null&&cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                //"late","skip","early","off"
                late=cursor.getInt(0);
                skip=cursor.getInt(1);
                early=cursor.getInt(2);
                off=cursor.getInt(3);
            }
        }
        spinner_skip.setSelection(skip);
        spinner_off.setSelection(off);
        spinner_late.setSelection(late);
        spinner_early.setSelection(early);
        db.close();
    }



    void lists_init()
    {
        lists=new ArrayList<String>();
        lists.add("0");
        lists.add("1");
        lists.add("2");
        lists.add("3");
        lists.add("4");
        lists.add("5");
        lists.add("6");
        lists.add("7");
        lists.add("8");
        lists.add("9");
        lists.add("10");
    }
    public void btn_save_reduce_clicked(View v){

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("确定修改？");
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                late=spinner_late.getSelectedItemPosition();
                early=spinner_early.getSelectedItemPosition();
                skip=spinner_skip.getSelectedItemPosition();
                off=spinner_off.getSelectedItemPosition();
                SQLiteDatabase db=new Sqldata(reduce_score_Activity.this).getReadableDatabase();
                db.execSQL(getApplicationContext().getString(R.string.update_sql_reduce_list),new Object[]{late,early,off,skip});

                db.execSQL(getApplicationContext().getString(R.string.update_socre));
                db.close();
                Toast.makeText(reduce_score_Activity.this, "修改成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();

    }
}
