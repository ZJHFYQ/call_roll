package com.example.zjh.call_roll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZJH on 2016-10-31-0031.
 */

public class class_set_Activity extends Activity {

    ListView lv_class;
    Button btn_add;
    Button btn_del;
    List<String> list_class;
    EditText et_class;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_set_layout);
        lv_class=(ListView)findViewById(R.id.lv_class);
        btn_add=(Button)findViewById(R.id.btn_add_class);
        btn_del=(Button)findViewById(R.id.btn_del_class);
        et_class=(EditText)findViewById(R.id.et_class_name);
        readDataBase();
    }

    void readDataBase()
    {
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        Cursor cursor=db.query(true,"classes",new String[]{"class"},null,null,null,null,null,null);
        list_class=new ArrayList<>();
        if(cursor!=null&&cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                list_class.add(cursor.getString(0));
            }
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list_class);

        lv_class.setAdapter(arrayAdapter);
        db.close();
    }

    public  void btn_add_clicked(View v)
    {
        if(et_class.getText().toString().length()==0)
        {
            Toast.makeText(this, "班级名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        for (String s:list_class) {
            if(s.equals(et_class.getText().toString()))
            {
                Toast.makeText(this, "班级名称已经存在", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        SQLiteDatabase db=new Sqldata(this).getWritableDatabase();
        db.execSQL(getApplicationContext().getString(R.string.sql_add_class)+et_class.getText().toString().trim()+"')");
        Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
        et_class.setText("");
        readDataBase();
    }

    public  void btn_del_clicked(View v)
    {
        if(list_class.size()<=0)
        {
            Toast.makeText(this, "还没有班级，不能删除", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("删除班级");
        final String [] strings=new String[list_class.size()];
        int i=0;
        for (String s:list_class) {
            strings[i]=list_class.get(i);
            i++;
        }

        final List<String> del=new ArrayList<>();
        builder.setMultiChoiceItems(strings, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked==true)
                {
                   del.add(strings[which]);
                }
                if(isChecked==false)
                {
                    del.remove(strings[which]);
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder b=new AlertDialog.Builder(class_set_Activity.this);
                b.setTitle("提醒");
                b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String s:del) {
                            SQLiteDatabase db=new Sqldata(class_set_Activity.this).getWritableDatabase();
                            db.execSQL(getApplicationContext().getString(R.string.sql_del_class)+s+"'");
                            readDataBase();
                        }
                        Toast.makeText(class_set_Activity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
                b.setNegativeButton("取消",null);
                b.show();
            }
        });
        builder.setNeutralButton("取消",null);
        builder.show();
    }

}
