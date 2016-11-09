package com.example.zjh.call_roll;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ZJH on 2016-10-22-0022.
 */

public class Sqldata extends SQLiteOpenHelper{

    Context context;

    public Sqldata(Context c) {
        super(c,"students.db", null,1);
        context=c;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建学生信息表
        db.execSQL(context.getResources().getString( R.string.creat_sql_student));
        //创建记录减分的记录表单
        db.execSQL(context.getString(R.string.creat_sql_record));
        //创建减分规则表单
        db.execSQL(context.getString(R.string.creat_sql_reduce_list));
        //给减分规则表初始化都为0
        db.execSQL(context.getString(R.string.sql_create_class));
        db.execSQL(context.getString(R.string.init_sql_reduce_list));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
