package com.liyudong.home.antinitview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/12/22.
 */

public class Ant extends View {

    private Context context;

    public Ant(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAtttr(attrs,context);
        initPaint();
    }

    public Ant(Context context) {
        super(context);
        this.context = context;
    }

    public Ant(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAtttr(attrs,context);
        initPaint();
    }

    private int maxNum;
    private int startAngle;
    private int sweepAngle;
    private int sweepInWidth;
    private int sweepOutWidth;

    private void initAtttr(AttributeSet attrs, Context context) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundIndicatorView);
        maxNum = array.getInt(R.styleable.RoundIndicatorView_maxNum, 500);
        startAngle = array.getInt(R.styleable.RoundIndicatorView_startAngle, 160);
        sweepAngle = array.getInt(R.styleable.RoundIndicatorView_sweepAngle, 220);
        sweepInWidth = dp2Px(8);
        sweepOutWidth = dp2Px(3);
    }

    public int dp2Px( float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private Paint paint;
    private Paint paint_2;
    private Paint paint_3;
    private Paint paint_4;
    private void initPaint(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffffffff);
        paint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_4 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private int mWidth;
    private int mHeight;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hSize = MeasureSpec.getMode(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if(wMode == MeasureSpec.EXACTLY){
            mWidth = wSize;
        }else{
            mWidth = dp2Px(300);
        }

        if(hMode == MeasureSpec.EXACTLY){
            mHeight = hSize;
        }else{
            mHeight = dp2Px(400);
        }
        setMeasuredDimension(mWidth,mHeight);
    }

    private int radius;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = getMeasuredWidth() / 4;
        canvas.save();
        canvas.translate(mWidth/2,(mWidth)/2);
        drawRound(canvas);
        drawScale(canvas);
        drawIndicator(canvas);
    }

    private void drawRound(Canvas canvas){
        canvas.save();
        paint.setAlpha(0x40);
        paint.setStrokeWidth(sweepInWidth);
        RectF rectf = new RectF(-radius,-radius,radius,radius);
        canvas.drawArc(rectf,startAngle,sweepAngle,false,paint);
        paint.setStrokeWidth(sweepOutWidth);
        int w = dp2Px(10);
        RectF rectf2 = new RectF(-radius-w, -radius-w,radius+w,radius+w);
        canvas.drawArc(rectf2,startAngle,sweepAngle,false,paint);
        canvas.restore();
    }

    private String[] text = {"较差","中等","良好","优秀","极好"};
    private void drawScale(Canvas canvas){
        canvas.save();
        float angle = (float)sweepAngle/30;
        canvas.rotate(-270 + startAngle);
        for(int i = 0 ; i <= 30 ; i++){
            if(i%6 == 0){
                paint.setStrokeWidth(dp2Px(2));
                paint.setAlpha(0x70);
                canvas.drawLine(0,-radius-sweepInWidth/2,0,
                        -radius+sweepInWidth/2+dp2Px(1),paint);
                drawText(canvas,i+maxNum/30+"",paint);
            }else {
                paint.setStrokeWidth(dp2Px(1));
                paint.setAlpha(0x50);
                canvas.drawLine(0,-radius-sweepInWidth/2,0,-radius+sweepInWidth/2,paint);
            }
            if(i ==3||i==9||i==15||i==21||i==27){
                paint.setStrokeWidth(dp2Px(2));
                paint.setAlpha(0x50);
                drawText(canvas,text[(i-3)/6],paint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    private int[] indicatorColor = {0xfffffff,0x00ffffff,0x99ffffff,0xffffffff};
    private int currentNum = 160;
    private void drawIndicator(Canvas canvas){
        canvas.save();
        paint_2.setStyle(Paint.Style.STROKE);
        int sweep;
        if(currentNum <= maxNum){
            sweep = (int)((float)currentNum/(float)maxNum*sweepAngle);
        }else{
            sweep = sweepAngle;
        }
        paint_2.setStrokeWidth(sweepOutWidth);
        Shader shader = new SweepGradient(0,0,indicatorColor,null);
        paint_2.setShader(shader);
        int w = dp2Px(10);
        RectF rectF = new RectF(-radius - w, -radius - w,radius + w,radius + w);
        canvas.drawArc(rectF,startAngle,sweep,false,paint_2);
        float x = (float) (radius+dp2Px(10)*Math.cos(Math.toRadians(startAngle+sweep)));
        float y = (float) (radius+dp2Px(10)*Math.sin(Math.toRadians(startAngle+sweep)));
        paint_3.setStyle(Paint.Style.FILL);
        paint_3.setColor(0xfffffff);
        paint_3.setMaskFilter(new BlurMaskFilter(dp2Px(3),BlurMaskFilter.Blur.SOLID));
        canvas.drawCircle(x,y,dp2Px(3),paint_3);
        canvas.restore();
    }


    private void drawText(Canvas canvas, String text, Paint paint){
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp2Px(8));
        float width = paint.measureText(text);
        canvas.drawText(text,-width/2,-radius + dp2Px(15),paint);
        paint.setStyle(Paint.Style.STROKE);
    }


}
