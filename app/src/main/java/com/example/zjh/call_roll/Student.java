package com.example.zjh.call_roll;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by ZJH on 2016-10-22-0022.
 */

public class Student implements Serializable{
    String name;
    String sno;//学号
    int sex;//0男 1女
    String classes;//班级
    Bitmap bp;//照片路径
    int score;//平时成绩
    int num_late;
    int num_early;
    int num_skip;
    int num_off;
    String type;
    public Student() {
        num_late=0;
        num_early=0;
        num_skip=0;
        num_off=0;
        score=100;
        type="出勤";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public Bitmap getBp() {
        return bp;
    }

    public void setBp(Bitmap bp) {
        this.bp = bp;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNum_late() {
        return num_late;
    }

    public void setNum_late(int num_late) {
        this.num_late = num_late;
    }

    public int getNum_early() {
        return num_early;
    }

    public void setNum_early(int num_early) {
        this.num_early = num_early;
    }

    public int getNum_skip() {
        return num_skip;
    }

    public void setNum_skip(int num_skip) {
        this.num_skip = num_skip;
    }

    public int getNum_off() {
        return num_off;
    }

    public void setNum_off(int num_off) {
        this.num_off = num_off;
    }
}
