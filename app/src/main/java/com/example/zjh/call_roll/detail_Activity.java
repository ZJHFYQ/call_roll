package com.example.zjh.call_roll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZJH on 2016-10-23-0023.
 */

public class detail_Activity extends Activity {

    String sno;//记录从学生信息页面跳转过来时传递过来的学号
    String sname;
    String sclass;
    String ssex;
    int num_late;
    int num_early;
    int num_off;
    int num_skip;
    Bitmap photo;
    int score;

    Cursor cursor_student;
    Cursor cursor_record;
    ImageView imageView;
    TextView textView_sno;
    TextView textView_sname;
    TextView textView_ssex;
    TextView textView_sclss;
    TextView textView_num_late;
    TextView textView_num_early;
    TextView textView_num_skip;
    TextView textView_num_off;
    TextView textView_score;
    ListView listView;

    Intent intent=new Intent();

    SimpleAdapter simpleAdapter;
    String []from={"date","type"};
    int []to={R.id.txt_record_time,R.id.txt_record_type};
    Map<String,Object> record;
    List<Map<String,Object>> list_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_info_layout);
        Intent intent=this.getIntent();
        sno=intent.getStringExtra("sno");

        intent.putExtra("flag","no");
        setResult(1,intent);

        imageView=(ImageView)findViewById(R.id.iv_detail_photo);
        textView_sno=(TextView)findViewById(R.id.txt_detail_sno);
        textView_sname=(TextView)findViewById(R.id.txt_detail_name);
        textView_ssex=(TextView)findViewById(R.id.txt_detail_sex);
        textView_sclss=(TextView)findViewById(R.id.txt_detail_calss);
        textView_num_late=(TextView)findViewById(R.id.txt_detail_late);
        textView_num_early=(TextView)findViewById(R.id.txt_detail_early);
        textView_num_skip=(TextView)findViewById(R.id.txt_detail_skip);
        textView_num_off=(TextView)findViewById(R.id.txt_detail_off);
        textView_score=(TextView)findViewById(R.id.txt_detail_score);

        listView=(ListView)findViewById(R.id.lv_record);
        QueryFromDatabase_student_info();
        QueryFromDatabase_record_info();

    }


    void QueryFromDatabase_record_info()
    {
        list_record=new ArrayList<Map<String,Object>>();
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        cursor_record=db.query("record_info",new String[]{"date","type"},"sno=?",new String[]{sno},null,null,null);
        if(cursor_record!=null&&cursor_record.getCount()>0)
        {
            while(cursor_record.moveToNext())
            {
                record=new HashMap<String, Object>();
                String date=cursor_record.getString(0);
                record.put("date",date);

                String type=cursor_record.getString(1);
                record.put("type",type);
                list_record.add(record);
            }
        }
        simpleAdapter=new SimpleAdapter(this,list_record,R.layout.record_layout,from,to);
        listView.setAdapter(simpleAdapter);
    }

    void QueryFromDatabase_student_info()
    {
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        db.execSQL(getApplicationContext().getString(R.string.update_socre));
        cursor_student=db.query("student_info",new String []{"photo","sname","sclass","ssex",
                    "num_late","num_early","num_skip","num_off","score"},"sno=?",new String[]{sno},null,null,null);

        if(cursor_student!=null&&cursor_student.getCount()>0)
        {
            while (cursor_student.moveToNext())
            {
                byte [] bytes=cursor_student.getBlob(0);
                photo= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(photo);

                textView_sno.setText(sno);

                sname=cursor_student.getString(1);
                textView_sname.setText(sname);

                sclass=cursor_student.getString(2);
                textView_sclss.setText(sclass);

                int n=cursor_student.getInt(3);
                if(n==0)
                {
                    ssex="男";
                }
                else if(n==1)
                {
                    ssex="女";
                }
                textView_ssex.setText(ssex);

                //"num_late","num_early","num_skip","num_off","score"

                num_late=cursor_student.getInt(4);
                textView_num_late.setText(String.valueOf(num_late));

                num_early=cursor_student.getInt(5);
                textView_num_early.setText(String.valueOf(num_early));

                num_skip=cursor_student.getInt(6);
                textView_num_skip.setText(String.valueOf(num_skip));

                num_off=cursor_student.getInt(7);
                textView_num_off.setText(String.valueOf(num_off));

                score=cursor_student.getInt(8);
                textView_score.setText(String.valueOf(score));
            }
        }
        db.close();
    }

    public void btn_txt_back_click(View v)  //删除学生信息
    {
        finish();
    }


    public void btn_delete_click(View v)  //删除学生信息
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db=new Sqldata(detail_Activity.this).getReadableDatabase();
                db.execSQL(detail_Activity.this.getString(R.string.delete_student_from_db),new Object[]{sno});
                db.execSQL(detail_Activity.this.getString(R.string.delete_record_from_db),new Object[]{sno});
                Toast.makeText(detail_Activity.this, "删除成功", Toast.LENGTH_SHORT).show();

                intent.removeExtra("flag");
                intent.putExtra("flag","yes");
                setResult(1,intent);
                finish();//删除成功后退出页面
            }
        });
        builder.setTitle("确定删除？");
        builder.setNegativeButton("取消", null);

        builder.show();
    }

    public void btn_update_click(View v) //修改学生信息
    {
        Intent intent=new Intent();
        intent.putExtra("flag","update");
//        String sno;
        intent.putExtra("sno",sno);
//        String sname;
        intent.putExtra("sname",sname);
//        String sclass;
        intent.putExtra("sclass",sclass);
//        String ssex;
        intent.putExtra("ssex",ssex);

//        Bitmap photo;
//        intent.putExtra("photo",photo); //不传递照片  intent能传递的图像大小不超过40K

        intent.setClassName("com.example.zjh.call_roll","com.example.zjh.call_roll.addStudentActivity");
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理修改后的学生信息
        if(resultCode==1)
        {
            sno=data.getStringExtra("sno");
            QueryFromDatabase_student_info();
            QueryFromDatabase_record_info();
            Intent intents=new Intent();
            intents.putExtra("sno",sno);
            intents.putExtra("flag","update");
            setResult(2,intents);
        }
    }
}
