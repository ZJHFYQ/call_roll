package com.example.zjh.call_roll;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static android.os.Environment.DIRECTORY_DOCUMENTS;


public class MainActivity extends AppCompatActivity {
    ListView listview;
    ImageView imageView_school;
    Button btn_add_student ;


    List<Student> list_student; //从数据库中查询学生数据列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView_school=(ImageView)findViewById(R.id.iv_school);
        imageView_school.setImageResource(R.drawable.school);
        btn_add_student=(Button)findViewById(R.id.btn_add_student);
        SQLiteDatabase sqLiteDatabase=(new Sqldata(this)).getReadableDatabase();//创建数据库,若存在则跳过
    }

    void QueryDatebase()
    {
        list_student=new ArrayList<Student>();
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        Cursor cursor=db.query("student_info",new String[]{"sno","sname","sclass","ssex","num_skip","num_off","num_late","num_early","score"},
                        null,null,null,null,null);
        if(cursor!=null&&cursor.getCount()>0)
        {
            while (cursor.moveToNext())
            {
                Student s=new Student();
                //"学号",
                s.sno=cursor.getString(0);
                // "姓名",
                s.name=cursor.getString(1);
                // "班级",
                s.classes=cursor.getString(2);
                // "性别",
                s.sex=cursor.getInt(3);
                // "逃课次数",
                s.num_skip=cursor.getInt(4);
                // "请假次数",
                s.num_off=cursor.getInt(5);
                // "迟到次数",
                s.num_late=cursor.getInt(6);
                // "早退次数",
                s.num_early=cursor.getInt(7);
                // "平时成绩"
                s.score=cursor.getInt(8);

                list_student.add(s);
            }
        }
        db.close();
    }
void outputData() throws  Exception
{
    //查询数据库  得到数据
    QueryDatebase();
    String path= Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)+"/Student_info.xls";
    File file=new File(path);
    if(!file.exists())
    {
        file.getParentFile().mkdirs();
        file.createNewFile();
    }
    System.out.println(path);
    /**创建Excel工作薄*/
    WritableWorkbook wwb;
    OutputStream os=new FileOutputStream(file);

    wwb= Workbook.createWorkbook(os);
    //准备设置excel工作表的标题
    WritableSheet sheet=wwb.createSheet("学生信息",0);

    String[] title ={"学号","姓名","班级","性别","逃课次数","请假次数","迟到次数","早退次数","平时成绩"};
    Label label;
    for(int i=0;i<title.length;i++){
        // Label(x,y,z) 代表单元格的第x行，第y列, 内容z
        // 在Label对象的子对象中指明单元格的位置和内容
        label = new Label(i,0,title[i]);
        // 将定义好的单元格添加到工作表中
        try{
            sheet.addCell(label);
        }
        catch (Exception e)
        {
            System.out.println("标题出错");
        }
    }
    for(int i=1;i<list_student.toArray().length+1;i++)
    {
        Student s=list_student.get(i-1);
        //"学号",
        jxl.write.Label sno=new jxl.write.Label(0,i,s.sno);
        sheet.addCell(sno);
        // "姓名",
        jxl.write.Label name=new jxl.write.Label(1,i,s.name);
        sheet.addCell(name);
        // "班级",
        jxl.write.Label sclass=new jxl.write.Label(2,i,s.classes);
        sheet.addCell(sclass);
        // "性别",
        jxl.write.Label sex;
        if(s.sex==0)
        {
            sex=new jxl.write.Label(3,i,"男");
        }
        else
        {
            sex=new jxl.write.Label(3,i,"女");
        }
        sheet.addCell(sex);

        // "逃课次数",
        jxl.write.Number num_skip=new jxl.write.Number(4,i,s.num_skip);
        sheet.addCell(num_skip);
        // "请假次数",
        jxl.write.Number num_off=new jxl.write.Number(5,i,s.num_off);
        sheet.addCell(num_off);
        // "迟到次数",
        jxl.write.Number num_late=new jxl.write.Number(6,i,s.num_late);
        sheet.addCell(num_late);
        // "早退次数",
        jxl.write.Number num_early=new jxl.write.Number(7,i,s.num_early);
        sheet.addCell(num_early);
        // "平时成绩"
        jxl.write.Number score=new jxl.write.Number(8,i,s.score);
        sheet.addCell(score);
    }
    wwb.write();//写入数据
    wwb.close();
    os.close();
    Toast.makeText(MainActivity.this, "导出完成，到文件资源Document中查看", Toast.LENGTH_SHORT).show();
}


    public  void btn_export_clicked (View v) throws Exception
    {
        if(Build.VERSION.SDK_INT>=23)//安卓6.0以上  动态申请权限
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        else{
            outputData();
        }
    }

    public void call_role(View v)  //点名按钮
    {
        Intent intent=new Intent();
        intent.setClassName("com.example.zjh.call_roll", "com.example.zjh.call_roll.call_rollActivity");
        startActivity(intent);
    }

    boolean check_class()
    {
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        Cursor cursor=db.query(true,"classes",new String[]{"class"},null,null,null,null,null,null);
        if(cursor!=null&&cursor.getCount()>0)
        {
            return true;
        }
        else{
            db.close();
            return false;
        }
    }

    public void addStudent(View v)  //添加按钮
    {
        if(!check_class()){
            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("提醒");
            builder.setMessage("还没有添加班级，请先添加班级后再来添加学生信息");
            builder.setPositiveButton("确定",null);
            builder.show();
            return;
        }

        Intent intent=new Intent();
        intent.setClassName("com.example.zjh.call_roll", "com.example.zjh.call_roll.addStudentActivity");
        intent.putExtra("flag","add");//告诉子页面 这是要添加学生
        startActivityForResult(intent,1);//代表请求添加按钮
    }
    public void showStudent(View v)  //查看按钮 查看各类信息
    {
        Intent intent=new Intent();
        intent.setClassName("com.example.zjh.call_roll", "com.example.zjh.call_roll.list_all_Activity");
        startActivity(intent);
    }

    public  void btn_reduce_score_list_clicked(View v)
    {
        Intent intent=new Intent();
        intent.setClassName("com.example.zjh.call_roll","com.example.zjh.call_roll.reduce_score_Activity");
        startActivity(intent);
    }

    public  void btn_class_set_clicked(View v)
    {
        Intent intent=new Intent();
        intent.setClassName("com.example.zjh.call_roll","com.example.zjh.call_roll.class_set_Activity");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)  {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 2:{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    try{
                        outputData();
                    }
                    catch (Exception e){};
                }
                else{
                    Toast.makeText(MainActivity.this, "写权限被拒绝，请开启权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
