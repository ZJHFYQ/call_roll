package com.example.zjh.call_roll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZJH on 2016-10-23-0023.
 */

public class list_all_Activity extends Activity {

    ListView listView;
    SimpleAdapter simpleAdapter;
    List<Map<String,Object>> list_student;
    List<Map<String,Object>> list_student_class;//代表相同班级的学生
    String []from={"photo","name","sno","classes"};
    int []to={R.id.iv_photo_list_all_item,R.id.txt_name_list_all_item,R.id.txt_sno_list_all_item,R.id.txt_class_list_all_item};
    List<String> list_class=new ArrayList<String>(); //Spinner 的item  list
    ArrayAdapter<String> arrayAdapter;
    Spinner spinner;
    Cursor cursor;//查询结果
    ImageView view;
    String sno;//代表查看详情的学生的学号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_all_layout);
        listView=(ListView)findViewById(R.id.lv_list_all);
        spinner=(Spinner)findViewById(R.id.spinner_class_list_all);
        list_class.add("所有班级");

        readDataBase(this);//打开数据库  查询数据

        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list_class);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //选择班级代码
                String classes=list_class.get(position);//得到spinner当前选择的班级
                list_student_class=new ArrayList<Map<String, Object>>();
                for (Map<String,Object> map: list_student) {
                    String cla=(String)map.get("classes");
                    if(cla.equals(classes))
                    {
                        list_student_class.add(map);
                    }
                }
                if(classes.equals("所有班级"))
                {
                    list_student_class=list_student;
                }
                simpleAdapter=new SimpleAdapter(list_all_Activity.this,list_student_class,R.layout.ltem_list_all_layout,from,to);
                simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    //simpleAdapter不支持bitmap或是 imageview  默认的只是支持ImageView的id

                    //这里添加支持
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if(view instanceof ImageView && data instanceof Bitmap){
                            ImageView iv=(ImageView)view;
                            iv.setImageBitmap((Bitmap)data);
                            return true;
                        }else{
                            return false;
                        }
                    }
                });
                listView.setAdapter(simpleAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap <String,Object> item=(HashMap <String,Object>)list_student_class.get(position);
                sno=(String)item.get("sno");
                //System.out.println(number);
                Intent intent=new Intent();
                intent.putExtra("sno",sno);
                intent.setClassName("com.example.zjh.call_roll","com.example.zjh.call_roll.detail_Activity");
                //startActivity(intent);
                startActivityForResult(intent,1);
            }
        });
        view=(ImageView)findViewById(R.id.iv_photo_list_all_item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1)//删除结果码
        {
            if(data.getStringExtra("flag").equals("yes"))//说明删除成功，则刷新列表
            {
                Map<String,Object> map=new HashMap<String,Object>();
                for (Map<String,Object> m: list_student) {
                    String num=(String) m.get("sno");
                    if(num.equals(sno))
                    {
                        map=m;
                        break;
                    }
                }
                list_student.remove(map);
                list_student_class=list_student;//*******
                updateSpinner();//更新下拉列表
                listView.setAdapter(simpleAdapter);//更新学生信息列表
            }
        }
        if(2==resultCode)//修改结果码  已经修改过了
        {
            readDataBase(this);
            simpleAdapter=new SimpleAdapter(list_all_Activity.this,list_student,R.layout.ltem_list_all_layout,from,to);
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                //simpleAdapter不支持bitmap或是 imageview  默认的只是支持ImageView的id
                //这里添加支持
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if(view instanceof ImageView && data instanceof Bitmap){
                        ImageView iv=(ImageView)view;
                        iv.setImageBitmap((Bitmap)data);
                        return true;
                    }else{
                        return false;
                    }
                }
            });
            listView.setAdapter(simpleAdapter);
        }
    }

    void updateSpinner()
    {
        list_class.clear();
        list_class.add("所有班级");
        for ( Map<String,Object> m:list_student) {

            if(!list_class.contains((String) m.get("classes")))
            {
                list_class.add((String) m.get("classes"));
            }
        }
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list_class);
        spinner.setAdapter(arrayAdapter);//跟新下拉列表
    }

    void readDataBase(Context context)
    {
        list_student=new ArrayList<Map<String,Object>>();
        SQLiteDatabase db=new Sqldata(context).getReadableDatabase();
        cursor=db.query(context.getString(R.string.database_name_info), new String[]{"photo","sno","sname","sclass"},null,null,null,null,null);
        //tianjia下拉选择班级
        //添加条目
        if(cursor!=null&&cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                Map<String,Object> map=new HashMap<String,Object>();
                //照片
                byte []bytes=cursor.getBlob(cursor.getColumnIndex("photo"));
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                map.put("photo",bitmap);
                //学号
                String no=cursor.getString(1);
                map.put("sno",no);
                //班级
                String cla=cursor.getString(3);
                if(!list_class.contains(cla))
                {
                    list_class.add(cla);
                }
                map.put("classes",cla);
                //姓名
                String name=cursor.getString(2);
                map.put("name",name);
                list_student.add(map);
            }
        }
        list_student_class=list_student;//***
        updateSpinner();
        db.close();
    }



}
