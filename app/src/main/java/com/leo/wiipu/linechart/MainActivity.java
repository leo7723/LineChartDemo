package com.leo.wiipu.linechart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MyLineChart chart;
    ArrayList<MyLineData> lineDatas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart= (MyLineChart) findViewById(R.id.chart);
        MyLineData data=new MyLineData("4 0",70);
        lineDatas.add(data);
        MyLineData data0=new MyLineData("4 1",90);
        lineDatas.add(data0);
        MyLineData data1=new MyLineData("4 2",80);
        lineDatas.add(data1);
        MyLineData data2=new MyLineData("4 3",80);
        MyLineData data3=new MyLineData("4 4",30);
        MyLineData data4=new MyLineData("4 5",40);
        MyLineData data5=new MyLineData("4 6",55);
        MyLineData data6=new MyLineData("4 7",60);
        MyLineData data7=new MyLineData("4 8",80);
        MyLineData data8=new MyLineData("4 9",90);
        lineDatas.add(data2);
        lineDatas.add(data3);
        lineDatas.add(data4);
        lineDatas.add(data5);
        lineDatas.add(data6);
        lineDatas.add(data7);
        lineDatas.add(data8);
        MyLineData data9=new MyLineData("4 3",60);
        MyLineData data10=new MyLineData("4 4",30);
        MyLineData data11=new MyLineData("4 5",40);
        MyLineData data12=new MyLineData("4 6",55);
        MyLineData data13=new MyLineData("4 7",60);
        MyLineData data14=new MyLineData("4 8",80);
        MyLineData data15=new MyLineData("4 9",90);
        lineDatas.add(data9);
        lineDatas.add(data10);
        lineDatas.add(data11);
        lineDatas.add(data12);
        lineDatas.add(data13);
        lineDatas.add(data14);
        lineDatas.add(data15);

        chart.setData(lineDatas);
    }
}
