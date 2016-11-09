package com.example.zjh.call_roll;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZJH on 2016-10-23-0023.
 */

public class addStudentActivity extends Activity {
    ImageView iv_photo;
    Student student=new Student();
    EditText et_name;
    Spinner sp_class;
    EditText et_sno;
    Spinner sp;

    private static final int CAMERA_REQUEST_CODE = 547;

    ArrayAdapter arrayAdapter_class;
    String []sexs={"男","女"};
    Intent intent;


    ArrayAdapter<String> arrayAdapter;
    List<String> list_class;
    String sno_last;//记录之前的学号 防止修改时  学号也被修改了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addstudent_layout);

        iv_photo=(ImageView)findViewById(R.id.iv_add_photo);
        et_name=(EditText)findViewById(R.id.et_name);
        sp_class=(Spinner) findViewById(R.id.sp_class_add);
        sp=(Spinner)findViewById(R.id.spinner_sex);
        et_sno=(EditText)findViewById(R.id.et_sno);

        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,sexs);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp.setAdapter(arrayAdapter);

        readclass();

        intent=getIntent();
        if(intent.getStringExtra("flag").equals("update"))//说明这是请求修改数据 填充数据
        {
//        String sno;
          student.sno=intent.getStringExtra("sno");
            et_sno.setText(student.sno);
            sno_last=student.sno;
//        String sname;
          student.name=  intent.getStringExtra("sname");
            et_name.setText(student.name);
//        String sclass;
          student.classes= intent.getStringExtra("sclass");
           sp_class.setSelection(list_class.indexOf(student.classes));

//        String ssex;
          String s=intent.getStringExtra("ssex");
            if(s.equals("男"))
                student.sex=0;
            else{
                student.sex=1;
            }
            sp.setSelection(student.sex);


            SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
            Cursor cursor= db.query("student_info",new String[]{"photo"},"sno=?",new String[]{student.sno},null,null,null);
            if(cursor!=null&&cursor.getCount()>0)
            {
                while(cursor.moveToNext())
                {
                    byte [] bytes=cursor.getBlob(0);
                    student.bp=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    iv_photo.setImageBitmap(student.bp);
                    break;
                }
            }
        }

    }

    void readclass(){
        list_class=new ArrayList<>();
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        Cursor cursor=db.query(true,"classes",new String[]{"class"},null,null,null,null,null,null);
        if(cursor!=null&&cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                list_class.add(cursor.getString(0));
            }
        }
        arrayAdapter_class=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list_class);
        sp_class.setAdapter(arrayAdapter_class);
    }



    public void btn_save(View v)//保存按钮,把信息添加进数据库
    {
        if(student.bp==null)
        {
            Toast.makeText(this, "头像不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(et_name.getText().toString().trim().length()==0)
        {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(et_sno.getText().toString().trim().length()==0)
        {
            Toast.makeText(this, "学号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(sp_class.getSelectedItem().toString().trim().length()==0)
        {
            Toast.makeText(this, "班级不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        student.name=et_name.getText().toString().trim();
        student.classes=sp_class.getSelectedItem().toString().trim();
        student.sno=et_sno.getText().toString().trim();
        if(sp.getSelectedItem().toString().equals("男"))
        {
            student.sex=0;
        }
        else
        {
            student.sex=1;
        }
        save_database();
    }

    void save_database()
    {
        SQLiteDatabase db=new Sqldata(this).getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("sno",student.sno);
        values.put("sname",student.name);
        values.put("sclass",student.classes);
        values.put("score",student.score);
        values.put("num_late",student.num_late);
        values.put("num_early",student.num_early);
        values.put("num_off",student.num_off);
        values.put("num_skip",student.num_skip);
        values.put("ssex",student.sex);

        ByteArrayOutputStream os=new ByteArrayOutputStream();
        student.bp.compress(Bitmap.CompressFormat.PNG,100,os);
        values.put("photo",os.toByteArray());

        if(intent.getStringExtra("flag").equals("add"))
        {
            long flag=-1;
            try {
                flag=db.insert("student_info", null, values);
            }catch (Exception e){};
            if(flag==-1)
            {
                Toast.makeText(this, "添加失败,检查学号", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            }
        }
        else if(intent.getStringExtra("flag").equals("update"))
        {

            long flag=-1;
            try{
                flag=db.update("student_info",values,"sno=?",new String[]{sno_last});
            }catch (Exception e){};
            if(flag>=0)
            {
                ContentValues value=new ContentValues();
                value.put("sno",student.sno);
                db.update("record_info",value,"sno=?",new String[]{sno_last});
                Toast.makeText(this,"更改成功",Toast.LENGTH_SHORT).show();
                Intent intents=new Intent();
                intents.putExtra("sno",student.sno);
                setResult(1,intents);//设置页面返回Intent
            }
            else{
                Toast.makeText(this, "更改失败，检查学号", Toast.LENGTH_SHORT).show();
            }
        }
        db.close();
    }

    public  void btn_iv_photo(View v) //点击图片 选择图片 或者启动摄像头拍摄
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("请选择头像来源");
        final String []source={"本地图库","启动摄像头拍照"};
        builder.setItems(source, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)//选择的是本地图库
                {
//                    Intent intent = new Intent(Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                    intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra("return-data", true);  //是否要返回值。 一般都要
                /* 取得相片后返回本画面 */
                    startActivityForResult(intent, 1);
                }
                else{//启动摄像头拍摄
                    if(hasCamera())
                    {
                        if(Build.VERSION.SDK_INT>=23)//安卓6.0以后需要动态申请权限
                        {
                            ActivityCompat.requestPermissions(addStudentActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                        }
                        else{
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 2);
                        }

                    }
                    else
                    {
                        Toast.makeText(addStudentActivity.this, "该设备没有摄像装备", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 2);
                }
                else{
                    Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static boolean checkCameraFacing(final int facing) {
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasCamera() {
        return hasBackFacingCamera() || hasFrontFacingCamera();
    }

    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            try
            {
                Uri uri=data.getData();
                ContentResolver cr = this.getContentResolver();
                //getThumbnail(uri,500 , cr.openInputStream(uri));
                //student.bp= BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
                //iv_photo.setImageBitmap(student.bp);
                student.bp=getBitmapFormUri(this,  uri);
                iv_photo.setImageBitmap(student.bp);
            }
            catch (Exception e)
            {
                //Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==2)
        {
            try
            {
                student.bp=compressImage((Bitmap)data.getExtras().get("data"));
                iv_photo.setImageBitmap(student.bp);
            }
            catch (Exception e)
            {
                //Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {

        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放

        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return compressImage(bitmap);//再进行质量压缩
    }
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
