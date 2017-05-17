package com.leo.wiipu.linechart;

/**
 * Created by changliliao on 2017/4/25.
 */

public class MyLineData {
    private String time;
    private float num;
    private float x;//记住你的x
    private float y;//记住你的y

    public MyLineData(String time, float num){
        this.time=time;
        this.num=num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
