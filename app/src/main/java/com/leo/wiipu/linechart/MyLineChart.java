package com.leo.wiipu.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import java.util.List;

/**
 * Created by changliliao on 2017/4/25.
 */

public class MyLineChart extends View {

    Context mContext;
    Paint mPaint;
    Paint mLinePaint;
    private int mXDown,mLastX=0;
    //滑动最短距离
    int a=0;

    float maxX;//总的最长的长度
    float mMaxX=0;//当前自己的X的总长度
    float mXDownFloat=0;

    float popAllX=0;
    float popLastX=0;//用于让气泡跟手


    float startX=0;
    float lastStartX =0;//抬起手指后，当前控件最左边X的坐标
    float cellCountW = 9.5f;//一个屏幕的宽度会显示的格子数 用来计算一个格子的具体宽高度
    float cellCountH = 12.5f;//整个控件的高度会显示的格子数

    float cellH,cellW;
    float topPadding= 0.25f;//flot其实是表示百分比 并不是具体高度 所有的高度都是这些乘以测量值


    int popDeltaX=0;

    float deltaXFloat;
    float mLastXFloat;

    PathEffect mEffect = new CornerPathEffect(20);//平滑过渡的角度

    int state = -100;
    int lineWidth;

    List<MyLineData> data;


    boolean isFirst=true;

    public void setData(List<MyLineData> data){
        this.data=data;
        state=-100;
        postInvalidate();
    }

    public MyLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        a= (int) DensityUtil.px2dp(context, ViewConfiguration.get(context).getScaledDoubleTapSlop());
        setClickable(true);
        lineWidth=DensityUtil.dp2px(context,1);
        initPaint();
    }

    private void initPaint(){
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.white));

        mLinePaint=new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(getResources().getColor(R.color.grey_lite));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cellH=getHeight()/cellCountH;
        cellW=getWidth()/cellCountW;

        if (isFirst&&(data!=null)){
            maxX=cellW*(data.size()-1);
        }

        if(data==null||data.size()==0){
            return;
        }

        DraWHorizontalLines(canvas);
        DraWVerticalLines(canvas);
        //--------画完背景----------
        canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        DrawDataLine(canvas);
        canvas.restore();

        canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        DrawVertical(canvas);
        canvas.restore();

        showpop(canvas);
        isFirst=false;

    }

    //画背景横线
    public void DraWHorizontalLines(Canvas canvas){
        mLinePaint.setColor(getResources().getColor(R.color.grey_lite));
        for(int i=0;i<11;i++){
            //startX startY stopX stopY
            canvas.drawLine(0,(topPadding+i)*cellH,cellW*cellCountW,(topPadding+i)*cellH,mLinePaint);
        }
    }

    //画横坐标
    public void DraWVerticalLines(Canvas canvas){
        mLinePaint.reset();
        mPaint.setTextSize(getWidth()/cellCountW/3f);
        mPaint.setColor(getResources().getColor(R.color.black));
        float i = 0.5f;
        for(MyLineData tmp:data){
            mLinePaint.setColor(getResources().getColor(R.color.grey_lite));
            mLinePaint.setTextSize(getWidth()/cellCountW/3.2f);
            MyLineData tmp2=getDataByX(mLastX);

            //加深选中项
            if(tmp2!=null&&tmp2.getTime().equals(tmp.getTime())&&state== MotionEvent.ACTION_UP&&Math.abs(mLastX-mXDown)<a){
                mLinePaint.setColor(getResources().getColor(R.color.grey));
            }else mLinePaint.setColor(getResources().getColor(R.color.grey_lite));

            String str1 =tmp.getTime().split(" ")[1];
            //除以二使字在中间 measureText返回字符宽度
            canvas.drawText(str1,startX+cellW*i-mLinePaint.measureText(str1)/2
                    //,(((int)cellCountH-1)+topPadding+cellCountH)/2*cellH-1.5f*(mLinePaint.ascent()+mLinePaint.descent())
                    ,(topPadding+10.5f)*cellH+mPaint.measureText("end")
                    ,mPaint);

            //画背景竖线
            mLinePaint.setColor(getResources().getColor(R.color.grey_lite));
            canvas.drawLine(startX+cellW*i,topPadding*cellH,startX+cellW*i,(topPadding+10.5f)*cellH,mLinePaint);
            i++;

        }

        // text x y paint
        canvas.drawText("end",startX+cellW*i-mPaint.measureText("end")/2
                //,(((int)cellCountH-1)+topPadding+cellCountH)/2*cellH-(mPaint.ascent()+mPaint.descent())/2
                ,(topPadding+10.5f)*cellH+mPaint.measureText("end")
                ,mPaint);
    }

    //画纵坐标
    public void DrawVertical(Canvas canvas){
        //Y 轴的遮挡背景
        //mPaint.reset();
        //mPaint.setColor(0xffffffff);
        //BA最后一条线露出来
        //canvas.drawRect(cellW*((int)cellCountW-0.5f+0.01f),0,cellW*((int)cellCountW+1),11.2f*cellH,mLinePaint);

        mLinePaint.setColor(getResources().getColor(R.color.blue));
        mLinePaint.setTextSize(getWidth()/cellCountW/3);

        int percent=100;
        for(int i=0;i<11;i+=2){
            canvas.drawText(String.valueOf(percent)+"%"
                    ,cellW*(int)cellCountW-mLinePaint.measureText(String.valueOf(percent)+"%")/2
                    ,(topPadding+i)*cellH-(mLinePaint.ascent()+mLinePaint.descent())/2
                    ,mLinePaint);
            percent-=20;
        }
    }


    //画数据线
    public void DrawDataLine(Canvas canvas){
        float i =0.5f;
        mLinePaint.reset();
        mLinePaint.setStrokeWidth(lineWidth);
        mLinePaint.setColor(getResources().getColor(R.color.dark_blue));

        //使用Path进行绘制 从movrTo(x,y)开始 接着之前的画就是lineTo(x,y) mEffect控制拐弯的的弧度
        //依旧是通过控制startX来进行模拟滑动
        Path path=new Path();
        path.moveTo(startX+cellW*i-1,getHByValue(data.get(0).getNum()));
        //path.lineTo(startX+cellW*i,getHByValue(data.get(0).getNum()));
        MyLineData tmpLast=null;
        for(MyLineData tmp:data){
            path.lineTo(startX + cellW * i, getHByValue(tmp.getNum()));

            if(isFirst){
                tmp.setX(startX + cellW * i);
                tmp.setY(getHByValue(tmp.getNum()));
            }
            i++;
        }
        path.lineTo(startX+cellW*(i-1),getHByValue(data.get(data.size()-1).getNum()));
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(mEffect);
        canvas.drawPath(path,mLinePaint);
    }

    //追踪的逻辑 通过手的移动 获得当前移动累积的距离
    //累积的距离根据 屏幕/总长度 换算出折线图应该滑动的距离
    private void showpop(Canvas canvas){
        if((state==MotionEvent.ACTION_MOVE||(state==MotionEvent.ACTION_UP))){

            int position=getDataByScreenX(popAllX);

            MyLineData dataq=data.get(position);
            MyLineData datap;
            if(position+1>=data.size())
                datap=dataq;
            else datap=data.get(position+1);
            if(datap==null||dataq==null){
                return;
            }
            initPaint();
            //画气泡背景
            mPaint.setColor(0xffffffff);
            mPaint.setTextSize(getWidth()/cellCountW/3f);
            Paint.FontMetricsInt fontMetricsInt=mPaint.getFontMetricsInt();
            RectF r;

            float left=popAllX-mPaint.measureText(dataq.getNum() + "%") * 0.8f;
            if(left < 0 ){
                left = 0;
            }
            float right = left + 2 * mPaint.measureText(dataq.getNum() + "%") * 0.8f;
            float height;
            float deltaH=getDeltaY(popAllX,dataq.getY(),datap.getY());
            height=dataq.getY()+deltaH;

            if (dataq.getNum() >= 10) {
                r = new RectF(left,
                        height,
                        right,
                        height + 1.5f * (fontMetricsInt.bottom - fontMetricsInt.top));
            } else {
                r = new RectF(left,
                        height - 1.5f * (fontMetricsInt.bottom - fontMetricsInt.top),
                        right,
                        height);
            }
            canvas.drawRoundRect(r,90,90,mPaint);
            //画气泡上的文字

            mPaint.setColor(getResources().getColor(R.color.dark_blue));

            float baseline = (r.bottom + r.top - fontMetricsInt.bottom - fontMetricsInt.top) / 2;

            canvas.drawText(dataq.getNum()+"%",(r.left+r.right)/2-mPaint.measureText(dataq.getNum()+"%")/2f
                    ,baseline,mPaint);
        }
    }

    private int getDataByScreenX(float currentX){
        float maxCurrentX;
        maxCurrentX=currentX*(maxX/getWidth());
        if(Math.abs(maxCurrentX-maxX)<2) return data.size()-1;
        for(int i=0;i<data.size();i++){
            if(maxCurrentX<cellW*i){return i;}
        }
        return (data.size()-1);
    }


    private float getDeltaY(float currentX,float Yq,float Yp){
        float maxCurrentX;
        maxCurrentX=currentX*(maxX/getWidth());
        return maxCurrentX%cellW*((Yp-Yq)/cellW);
    }


    //通过坐标获得附近的点
    private MyLineData getDataByX(int pointX){
        float i =0.5f;
        MyLineData result =  null;
        for(MyLineData tmp:data){
            float x =startX+cellW*i;
            if(Math.abs(x-pointX)<cellW/2){
                result=tmp;
                return result;
            }
            i++;
        }
        return result;
    }

    private float getHByValue(float value){
        return (topPadding+10)*cellH-(cellH*10)*value/100;
    }
    //通过横坐标文字获取坐标
    private float getXBykey(String key){
        float i =0.5f;
        for(MyLineData tmp:data){
            if(tmp.getTime().equals(key)){
                return startX+cellW*i;
            }
            i++;
        }
        return 0;
    }

    private void gotoEnd(){
        if(data==null||data.size()==0){
            return;
        }
        if(data.size()<cellCountW-1){
            startX=0;
            lastStartX=startX;
            postInvalidate();
            return;
        }
        startX=-(cellW)*(data.size()-cellCountW+1);
        lastStartX=startX;
        postInvalidate();
        Log.d("toEnd","go to end");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (data == null || data.size() == 0) {
            return super.onTouchEvent(event);
        }
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mXDown = (int) event.getRawX();
                popLastX=event.getX();
                state = MotionEvent.ACTION_DOWN;
                mXDownFloat=event.getRawX();
                mLastXFloat=event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastX = (int) event.getRawX();
                popAllX+=popLastX-event.getX();
                popLastX=event.getX();
                Log.d("x",String.valueOf(popAllX));


                if (Math.abs(lastStartX - mXDown) < a) {
                    break;
                }

                //滑动限制
                if (lastStartX + mLastX - mXDown > 0.5f * cellW || lastStartX + mLastX - mXDown + cellW * (data.size() + 0.5f) < cellW * (cellCountW - 1)) {
                    break;
                }

                deltaXFloat=event.getX()-mXDown;
                mMaxX+=(mLastXFloat-event.getRawX());

                mLastXFloat=event.getRawX();
                popDeltaX=(int) event.getRawX()-mLastX;

                state = MotionEvent.ACTION_MOVE;
                startX = lastStartX + mLastX - mXDown;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                // 抬起
                lastStartX = startX;
                state = MotionEvent.ACTION_UP;
                postInvalidate();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

}
