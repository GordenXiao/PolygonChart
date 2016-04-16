package gorden.widget.ploygonchart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gorden on 2016/4/15.
 */
public class PolygonChart extends View{
    private final int DEFAULT_SIDENUM=5;
    private final int DEFAULT_MAXVALUE=100;
    private final int DEFAULT_BORDERCOLOR=0xFF30ce60;
    private final int DEFAULT_BORDERSIZE=2;
    private final int DEFAULT_FILLCOLOR=0x5530ce60;
    private final int DEFAULT_TEXTCOLOR=0xFF000000;
    private final float DEFAULT_CHARTDATA=50;


    private int sideNum;    //多边形边数
    private int maxValue;   //每个点最大数据值
    private int borderColor;
    private int textColor;
    private float borderSize;
    private int fillColor;
    private float textSize=22;
    private float textPadding=10;


    private int viewWidth,viewHeight;
    private Paint borderPaint;
    private Paint fillPaint;
    private Paint textPaint;

    private float[] chartData;  //每个点的数据值
    private String[] pointExplain;     //每个点的描述

    private boolean isRunning =false;

    public void setSideNum(int sideNum) {
        this.sideNum = Math.max(3,sideNum);
//        if(chartData.length<sideNum){
//            int temp=chartData.length;
//            chartData= Arrays.copyOf(chartData,sideNum);
//        }
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        init();
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        init();
        invalidate();
    }

    public void setBorderSize(float borderSize) {
        this.borderSize = borderSize;
        init();
        invalidate();
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
        init();
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        init();
        invalidate();
    }

    public void setTextPadding(float textPadding) {
        this.textPadding = textPadding;
        invalidate();
    }

    public int getSideNum() {
        return sideNum;
    }

    public PolygonChart(Context context) {
        this(context,null);
    }

    public PolygonChart(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PolygonChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.PolygonChart);
        sideNum=array.getInt(R.styleable.PolygonChart_side_num,DEFAULT_SIDENUM);
        chartData=new float[sideNum];
        for(int i=0;i<sideNum;i++){
            chartData[i]=DEFAULT_CHARTDATA;
        }
        textPadding=array.getDimension(R.styleable.PolygonChart_textPadding,10);
        maxValue=array.getInt(R.styleable.PolygonChart_max_value,DEFAULT_MAXVALUE);
        borderColor=array.getColor(R.styleable.PolygonChart_border_color,DEFAULT_BORDERCOLOR);
        borderSize=array.getDimension(R.styleable.PolygonChart_border_size,DEFAULT_BORDERSIZE);
        textSize=array.getDimension(R.styleable.PolygonChart_text_size,22);
        fillColor=array.getColor(R.styleable.PolygonChart_fill_color,DEFAULT_FILLCOLOR);
        textColor=array.getColor(R.styleable.PolygonChart_text_color,DEFAULT_TEXTCOLOR);
        init();
    }

    private void init() {
        borderPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderSize);
        borderPaint.setStyle(Paint.Style.STROKE);

        fillPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(fillColor);

        textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);;
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
    }

    /**
     * 设置数值
     * @param charts
     */
    public void setChartData(float[] charts){
        if(charts!=null&&!isRunning){
            chartData=charts;
            invalidate();
        }
    }
    public void setChartDataAnimation(float[] charts){
        if(charts!=null&&!isRunning){
            post(new ChartDataRunnable(charts));
        }
    }

    /**
     * 动画形式修改数据
     */
    private class ChartDataRunnable implements Runnable{
        float[] charts;
        boolean complete=false;
        float bulking=5;
        public ChartDataRunnable(float[] charts) {
            this.charts = charts;
            isRunning =true;
        }

        @Override
        public void run() {
            complete=true;
            for(int i=0;i<Math.min(sideNum,charts.length);i++){
                if(Math.abs(chartData[i]-charts[i])<bulking)
                    chartData[i]=charts[i];
                if(chartData[i]<charts[i]){
                    chartData[i]+=bulking;
                    complete=false;
                }
                if(chartData[i]>charts[i]){
                    chartData[i]-=bulking;
                    complete=false;
                }
            }
            invalidate();
            if(complete){
                isRunning =false;
            }else{
                post(this);
            }
        }
    }

    /**
     * 设置每个点的描述
     * @param explains
     */
    public void setPointExplain(String[] explains){
        pointExplain=explains;
        invalidate();
    }
    public void clearPointExplain(){
        pointExplain=null;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth=getMeasuredSize(widthMeasureSpec,true);
        viewHeight=getMeasuredSize(heightMeasureSpec,false);
        setMeasuredDimension(viewWidth,viewHeight);
    }

    /**
     * 计算控件的实际大小
     * @param length onMeasure方法的参数，widthMeasureSpec或者heightMeasureSpec
     * @param isWidth 是宽度还是高度
     * @return int 计算后的实际大小
     */
    private int getMeasuredSize(int length, boolean isWidth){
        int specMode = MeasureSpec.getMode(length);
        int specSize = MeasureSpec.getSize(length);
        // 计算所得的实际尺寸，要被返回
        int retSize = 0;
        // 对不同的指定模式进行判断
        if(specMode==MeasureSpec.EXACTLY){  // 显式指定大小，如40dp或fill_parent
            retSize = specSize;
            if(retSize<2*calculatePadding(isWidth)){
                retSize = (int) (specSize+2*calculatePadding(isWidth));
            }
        }else{                              // 如使用wrap_content
            retSize = (int) (isWidth? 200+2*calculatePadding(true) : 200 + 2*calculatePadding(false));
            if(specMode==MeasureSpec.UNSPECIFIED){
                retSize = Math.min(retSize, specSize);
            }
        }
        return retSize;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(canvas);
        drawChart(canvas);
        if(pointExplain!=null){
            drawExplain(canvas);
        }
    }

    /**
     * 添加描述词
     * @param canvas
     */
    private void drawExplain(Canvas canvas) {
        List<PointF> pointfList=calculatePoints();
        PointF circle=pointfList.get(pointfList.size()-1);
        for(int i=0;i<Math.min(pointExplain.length,pointfList.size()-1);i++){
            PointF tmpPoint=pointfList.get(i);
            float drawX=tmpPoint.x;
            float drawY=tmpPoint.y;
            if(tmpPoint.x<circle.x){
                drawX=tmpPoint.x-textPaint.measureText(pointExplain[i])-textPadding;
            }
            if(tmpPoint.x>circle.x){
                drawX=tmpPoint.x+textPadding;
            }
            if(Math.abs(tmpPoint.x-circle.x)<5){
                drawX=tmpPoint.x-textPaint.measureText(pointExplain[i])/2;
            }
            if(tmpPoint.y<circle.y){
                drawY=tmpPoint.y-textPadding;
            }
            if(tmpPoint.y>circle.y){
                drawY=tmpPoint.y+textPadding+calculateTextSize(pointExplain[i],false);
            }
            if(Math.abs(tmpPoint.y-circle.y)<2){
                drawY=tmpPoint.y+calculateTextSize(pointExplain[i],false)/2;

            }
            canvas.drawText(pointExplain[i],drawX,drawY,textPaint);
        }
    }

    /**
     * 填充数据
     * @param canvas
     */
    private void drawChart(Canvas canvas) {
        List<PointF> pointfList=calculatePoints();
        Path path=new Path();
        float multiple=chartData[0]/maxValue;;
        path.moveTo(pointfList.get(pointfList.size()-1).x+(pointfList.get(0).x-pointfList.get(pointfList.size()-1).x)*multiple,pointfList.get(pointfList.size()-1).y-(pointfList.get(pointfList.size()-1).y-pointfList.get(0).y)*multiple);
        for(int i=1;i<Math.min(chartData.length,pointfList.size()-1);i++){
            multiple=chartData[i]/maxValue;
            float dx=pointfList.get(pointfList.size()-1).x+(pointfList.get(i).x-pointfList.get(pointfList.size()-1).x)*multiple;
            float dy=pointfList.get(pointfList.size()-1).y-(pointfList.get(pointfList.size()-1).y-pointfList.get(i).y)*multiple;
            path.lineTo(dx,dy);
        }
        if(chartData.length<pointfList.size()-1){
            path.lineTo(pointfList.get(pointfList.size()-1).x,pointfList.get(pointfList.size()-1).y);
        }
        path.close();
        canvas.drawPath(path,fillPaint);
    }

    /**
     * 画正多边形
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        List<PointF> pointfList=calculatePoints();
        Path path=new Path();
        path.moveTo(pointfList.get(0).x,pointfList.get(0).y);
        canvas.drawLine(pointfList.get(pointfList.size()-1).x,pointfList.get(pointfList.size()-1).y,pointfList.get(0).x,pointfList.get(0).y,borderPaint);
        for(int i=1;i<pointfList.size()-1;i++){
            path.lineTo(pointfList.get(i).x,pointfList.get(i).y);
            canvas.drawLine(pointfList.get(pointfList.size()-1).x,pointfList.get(pointfList.size()-1).y,pointfList.get(i).x,pointfList.get(i).y,borderPaint);
        }
        path.close();
        canvas.drawPath(path,borderPaint);
    }

    /**
     * 计算多边形的顶点
     * @return
     */
    private List<PointF> calculatePoints(){
        float angle=360f/sideNum;
        float radio=Math.min(viewHeight/2-calculatePadding(false),viewWidth/2-calculatePadding(true));//多边形半径
        float circleX=viewWidth/2;
        float circleY=viewHeight/2;
        float maxY=0;
        float minY=circleY;
        List<PointF> points=new ArrayList<PointF>();
        for(int i=0;i<sideNum;i++){
            float tmpAngle=angle*i;
            float tmpX= (float) (Math.sin(tmpAngle*Math.PI/180)*radio);
            float tmpY= (float) (Math.cos(tmpAngle*Math.PI/180)*radio);
            minY=Math.min(circleY-tmpY,minY);
            maxY=Math.max(circleY-tmpY,maxY);
            points.add(new PointF(tmpX+circleX,-tmpY+circleY));
        }
        points.add(new PointF(circleX,circleY));
        float offsetY=(viewHeight-maxY-minY)/2;
        for(int i=0;i<points.size();i++){
            points.set(i,new PointF(points.get(i).x,points.get(i).y+offsetY));
        }
        return points;
    }

    /**
     * 计算顶点描述需要留的padding
     * @return
     */
    private float calculatePadding(boolean isWidth){
        int padding=0;
        if(pointExplain==null)return textPadding;
        if(isWidth){
            for(String ss:pointExplain){
                padding= Math.max((int) textPaint.measureText(ss),padding);
            }
            padding+=textPadding;
        }else{
            padding= (int) (textSize+textPadding);
        }
        return padding;
    }

    private float calculateTextSize(String str,boolean isWidth){
        Rect rect=new Rect();
        textPaint.getTextBounds(str,0,str.length(),rect);
        return isWidth?rect.width():rect.height();
    }
}
