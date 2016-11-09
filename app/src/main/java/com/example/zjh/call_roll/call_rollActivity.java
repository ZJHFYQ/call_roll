package com.example.zjh.call_roll;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZJH on 2016-10-22-0022.
 */

public class call_rollActivity extends Activity {

    int count_student;//学生数目

    Map<String,String> map_call_role;

    ListView listView;
    SimpleAdapter simpleAdapter;
    List<Map<String,Object>> list_student;
    List<Map<String,Object>> list_student_class;//代表相同班级的学生
    String []from={"photo","name","sno","classes"};
    int []to={R.id.iv_photo,R.id.txt_name,R.id.txt_sno,R.id.txt_class};
    List<String> list_class=new ArrayList<String>(); //Spinner 的item  list
    ArrayAdapter<String> arrayAdapter;
    Spinner spinner;
    Cursor cursor;//查询结果
    ImageView view;

    int student_position;//代表当前点击的点名的学生的位置


    LayoutInflater inflater;
    RadioGroup radioGroup;
    RadioButton radioButton_arrive;
    RadioButton radioButton_late;
    RadioButton radioButton_skip;
    RadioButton radioButton_early;
    RadioButton radioButton_off;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_role_layout);

        listView=(ListView)findViewById(R.id.lv);


        spinner=(Spinner)findViewById(R.id.spinner_class);
        list_class.add("所有班级");
        readDataBase(this); //打开数据库  查询数据
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list_class);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //选择班级代码
                String classes=list_class.get(position);//得到spinner当前选择的班级

                if(classes.equals("所有班级"))
                {
                    list_student_class=list_student;
                }
                else{
                    list_student_class=new ArrayList<Map<String, Object>>();
                    for (Map<String,Object> map: list_student) {
                        String cla=(String)map.get("classes");
                        if(cla.equals(classes))
                        {
                            list_student_class.add(map);
                        }
                    }
                }
                count_student=list_student_class.size();
                simpleAdapter=new SimpleAdapter(call_rollActivity.this,list_student_class,R.layout.listitem_layout,from,to);
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
               //listView.setAdapter(simpleAdapter);
                listView.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return count_student;
                    }

                    @Override
                    public Object getItem(int position) {
                        return list_student_class.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final View v;
                        if(convertView==null)
                        {
                            inflater= LayoutInflater.from(call_rollActivity.this);
                            v=inflater.inflate(R.layout.listitem_layout,null);
                        }else{
                            v=convertView;
                        }
                        final Map<String,Object> map=(Map<String,Object>)getItem(position);
                        radioGroup=(RadioGroup) v.findViewById(R.id.radioGroup);
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton rb=(RadioButton) v.findViewById(group.getCheckedRadioButtonId());
                                try{
                                    map_call_role.remove((String) map.get("sno"));
                                }
                                catch (Exception e){}
                                map_call_role.put((String) map.get("sno"),rb.getText().toString());
                            }
                        });
                        radioButton_arrive=(RadioButton)v.findViewById(R.id.btn_radio_arrive);
                        radioButton_late=(RadioButton)v.findViewById(R.id.btn_radio_late);
                        radioButton_skip=(RadioButton)v.findViewById(R.id.btn_radio_skip);
                        radioButton_early=(RadioButton)v.findViewById(R.id.btn_radio_early);
                        radioButton_off=(RadioButton)v.findViewById(R.id.btn_radio_off);


                        ImageView iv=(ImageView) v.findViewById(R.id.iv_photo);
                        iv.setImageBitmap((Bitmap) map.get("photo"));

                        TextView tv_name=(TextView)v.findViewById(R.id.txt_name);
                        tv_name.setText((String)map.get("name"));

                        TextView tv_sno=(TextView)v.findViewById(R.id.txt_sno);
                        tv_sno.setText((String)map.get("sno"));
                        if(map_call_role.get((String)map.get("sno")).equals("出勤"))
                        {
                            radioButton_arrive.setChecked(true);
                        }
                        else if(map_call_role.get((String)map.get("sno")).equals("迟到"))
                        {
                            radioButton_late.setChecked(true);
                        }
                        else if(map_call_role.get((String)map.get("sno")).equals("逃课"))
                        {
                            radioButton_skip.setChecked(true);
                        }
                        else if(map_call_role.get((String)map.get("sno")).equals("请假"))
                        {
                            radioButton_off.setChecked(true);
                        }
                        else if(map_call_role.get((String)map.get("sno")).equals("早退"))
                        {
                            radioButton_early.setChecked(true);
                        }

                        TextView tv_class=(TextView)v.findViewById(R.id.txt_class);
                        tv_class.setText((String)map.get("classes"));

                        return v;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        view=(ImageView)findViewById(R.id.iv_photo);
    }

    void readDataBase(Context context)
    {
        map_call_role=new HashMap<String,String>();
        list_student=new ArrayList<Map<String,Object>>();
        SQLiteDatabase db=new Sqldata(context).getReadableDatabase();
        cursor=db.query(context.getString(R.string.database_name_info), new String[]{"photo","sno","sname","sclass"},null,null,null,null,null);
        //添加下拉选择班级  添加条目
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
                map_call_role.put(no,"出勤");
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
        count_student=list_student.size();
        list_student_class=list_student;
        //System.out.println(count_student);
    }

    public void btn_txt_back_click(View v)
    {
        finish();
    }

    public void btn_txt_post_click(View v)
    {
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        for (Map<String,Object> map:list_student) {

            String snum=(String) map.get("sno");
            ContentValues values=new ContentValues();

            values.put("sno",snum);
            values.put("sname",(String)map.get("name"));
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            values.put("date",formatter.format( new Date(System.currentTimeMillis())) );

            String sql="update student_info set ";

            if(map_call_role.get(snum).equals("迟到"))
            {
                sql=sql+" num_late=num_late+1 where sno="+snum;
                db.execSQL(sql);
                values.put("type","迟到");
                db.insert("record_info",null,values);
            }
            else if(map_call_role.get(snum).equals("早退"))
            {
                sql=sql+" num_early=num_early+1 where sno="+snum;
                db.execSQL(sql);
                values.put("type","早退");
                db.insert("record_info",null,values);
            }
            else if(map_call_role.get(snum).equals("逃课"))
            {
                sql=sql+" num_skip=num_skip+1 where sno="+snum;
                db.execSQL(sql);
                values.put("type","逃课");
                db.insert("record_info",null,values);
            }
            else if(map_call_role.get(snum).equals("请假"))
            {
                sql=sql+" num_off=num_off+1 where sno="+snum;
                db.execSQL(sql);
                values.put("type","请假");
                db.insert("record_info",null,values);
            }
        }
        db.close();
        Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
    }
}
