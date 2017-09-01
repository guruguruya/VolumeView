package com.example.volumeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class VolumeView extends View{

	int top;
    private int defaultValue;
    private RectF mArcRectf;
    private SweepGradient mSweepGradient;
    private int mCenter;//屏幕宽度中部
    private int mRadius;
    private int insideRadius; //内部半径
    
    private int circleWidth = 30;
    private Paint mPaint1,mPaint2;
    private Paint mLinePaint;
    private Paint mTextPaint;
    
    private int scanDegrees;
    private String text;
    
    private boolean isCanMove;
    
    private MoveInterface moveInterface;
    private int startDegrees = 0;
    private int textColor;
    private int textSize;
    private int finalDegrees;//最后手指离开的度数 更换模式需要
    private Rect mBound;

 // 指定了光源的方向和环境光强度来添加浮雕效果
    /** The emboss. */
    private EmbossMaskFilter emboss = null;
    // 设置光源的方向
    /** The direction. */
    float[] direction = new float[]{1,1,1};
    //设置环境光亮度
    /** The light. */
    float light = 0.4f;
    // 选择要应用的反射等级
    /** The specular. */
    float specular = 6;
    // 向 mask应用一定级别的模糊
    /** The blur. */
    float blur = 3.5f;

    private BlurMaskFilter mBlur = null;
    
    public VolumeView(Context context) {
        this(context,null);
    }

    public VolumeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LearningView, defStyleAttr,0);

        textColor = a.getColor(R.styleable.LearningView_titleColor, Color.RED);
        textSize = a.getDimensionPixelSize(R.styleable.LearningView_titleSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics()));

        a.recycle();
        init();
    }
	
    // 画笔初始化
    private void init() {
       
        mPaint1 = new Paint (); 
        mPaint1.setStrokeWidth(circleWidth);
        //修正锯齿
        mPaint1.setAntiAlias(true);
        mPaint1.setColor(Color.WHITE);
        mPaint1.setStyle(Paint.Style.STROKE);
        
        mBlur = new BlurMaskFilter(100, BlurMaskFilter.Blur.OUTER);
        
        mPaint2=new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setColor(Color.WHITE);
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setDither(true);
        emboss = new EmbossMaskFilter(direction,light,specular,blur);
        mBlur = new BlurMaskFilter(20, BlurMaskFilter.Blur.SOLID);
        

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(0xffdddddd);
        mLinePaint.setStrokeWidth(1);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(0xff64646f);
        mTextPaint.setTextSize(30);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //绘制文本的范围
        mBound = new Rect();
    }


    // 复写onDraw()进行绘制  
    @Override
    protected void onDraw(Canvas canvas) {

        initSize();
        
        mPaint1.setShader(null);
        canvas.drawArc(mArcRectf,135,270, false, mPaint1);
        mPaint1.setShader(mSweepGradient);
        
        canvas.drawArc(mArcRectf, 135, scanDegrees, false, mPaint1);
        mPaint2.setColor(Color.WHITE);
        mPaint1.setMaskFilter(emboss);
        
     // 设置画笔遮罩滤镜  ,传入度数和样式
        mPaint1.setMaskFilter(mBlur);
        
     //绘制内圆
        canvas.drawCircle(mCenter, mCenter, 40, mPaint2);
        
        //设置字体颜色
        mPaint2.setColor(textColor);
        //设置字体大小
        mPaint2.setTextSize(textSize);
        //得到字体的宽高范围
        text = String.valueOf(finalDegrees);
        mPaint2.getTextBounds(text, 0, text.length(), mBound);
        //绘制字体
        canvas.drawText(text, mCenter - mBound.width() / 2, mCenter + mBound.height() / 2, mPaint2);
    
        //画线 每隔3度画一天线 整个圆共画120条线
        for (int i = 0; i < 120; i++) {
            //圆心正顶部直线的Y坐标
            top = mCenter - mRadius - circleWidth / 2;
            // 去除底部不包含的区域 只旋转不划线
            if (i <= 45 || i >= 75) {
                if (i % 15 == 0) {
                    //整点时Y轴向外延伸
                    top = top - 15;
                }
                //第一条线为y轴
                canvas.drawLine(mCenter, mCenter - mRadius+15, mCenter, top+5, mLinePaint);
            }
            //旋转
            canvas.rotate(3, mCenter, mCenter);
        }
        //画文字+50代表这个字距离圆外边的距离
        
        //斜边
        int c = mRadius + circleWidth / 2 + 45;
        //x代表文字的中心距离圆心的距离 这是原点中心正左边字的长度
        int x = (int) Math.sqrt((c * c / 2));
        
        canvas.drawText(startDegrees + "", mCenter - x, mCenter + x, mTextPaint);
        canvas.drawText((startDegrees + 20) + "", mCenter - c, mCenter + 10, mTextPaint);
        canvas.drawText((startDegrees + 35) + "", mCenter - x, mCenter - x + 10, mTextPaint);
        canvas.drawText((startDegrees + 50) + "", mCenter, mCenter - c + 10, mTextPaint);
        canvas.drawText((startDegrees + 65) + "", mCenter + x, mCenter - x + 10, mTextPaint);
        canvas.drawText((startDegrees + 80) + "", mCenter + c, mCenter + 10, mTextPaint);
        canvas.drawText((startDegrees + 100) + "", mCenter + x+10, mCenter + x+5, mTextPaint);
        
    }
    
    private void initSize() {
        mCenter = 320/2;
        mRadius = 320/2-80;
        insideRadius = mRadius - circleWidth / 2;
        mArcRectf = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);
        
        int[] colors = {0xFFE5BD7D, 0xFFFAAA64,
                0xFFFFFFFF, 0xFF6AE2FD,
                0xFF8CD0E5, 0xFFA3CBCB,
                0xFFBDC7B3, 0xFFD1C299, 0xFFE5BD7D,};
//        float[] positions = {0, 1f / 8, 2f / 8, 3f / 8, 4f / 8, 5f / 8, 6f / 8, 7f / 8, 1};
        //渐变色
        mSweepGradient = new SweepGradient(mCenter, mCenter, colors, null);

    }
    
    /**
     * 测量宽度
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        //默认宽高;
        defaultValue = Integer.MAX_VALUE;

        switch (mode) {
            case MeasureSpec.AT_MOST:
                //最大值模式 当控件的layout_Width或layout_height属性指定为wrap_content时
                size = Math.min(defaultValue, size);
                break;
            case MeasureSpec.EXACTLY:
                //精确值模式 当控件的android:layout_width=”100dp”或android:layout_height=”match_parent”时

                break;
            default:
                size = defaultValue;
                break;
        }
        defaultValue = size;
        return size;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        switch (mode) {
            case MeasureSpec.AT_MOST:
                //最大值模式 当控件的layout_Width或layout_height属性指定为wrap_content时
                size = Math.min(defaultValue, size);
                break;
            case MeasureSpec.EXACTLY:
                //精确值模式 当控件的android:layout_width=”100dp”或android:layout_height=”match_parent”时

                break;
            default:
                size = defaultValue;
                break;
        }
        return size;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCanMove = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //判断手指在空白区域不能滑动
                if (!isCanMove) {
                    return false;
                }
                float y = event.getY();
                float x = event.getX();
                float firstX = event.getX();
                float firstY = event.getY();
                //判断当前手指距离圆心的距离 代表在圆心的右侧
                if (x > mCenter) {
                    x = x - mCenter;
                } else {
                    x = mCenter - x;
                }
                if (y < mCenter) {
                    y = mCenter - y;
                } else {
                    y = y - mCenter;
                }
                //判断当前手指是否在空白区域
                if (Math.sqrt(x * x + y * y) < (mRadius - 40)) {
                    isCanMove = false;
                    return false;
                }
                float v = x / (float) Math.sqrt(x * x + y * y);
                // 根据cos求角度
                double acos = Math.acos(v);
                acos = Math.toDegrees(acos);
                //手指在第三象限
                if (firstX <= mCenter && firstY >= mCenter) {
                    acos = 180 - acos;
                } else if (firstX <= mCenter && firstY <= mCenter) {
                    //手指在第二象限
                    acos = acos + 180;
                } else if (firstX >= mCenter && firstY <= mCenter) {
                    //手指在第一象限
                    acos = 360 - acos;
                } else {
                }
                scanDegrees = (int) acos;
                //计算度数 每22.5度值+1
                if (scanDegrees >= 135 && scanDegrees <= 360) {
                    scanDegrees = scanDegrees - 135;
                    int degrees = (int) (scanDegrees / 2.7);
                    finalDegrees = degrees;
                    if (moveInterface != null) {
                        moveInterface.getCurrentDegrees((degrees + startDegrees));
                    }
                    invalidate();

                } else if (scanDegrees <= 65) {
                    //小于45无法算到最后一个 故意多加了20度
                    scanDegrees = (int) (360 - 135 + acos);
                    if (scanDegrees > 270) {
                        scanDegrees = 270;
                    }
                    int degrees = (int) (scanDegrees / 2.7);
                    finalDegrees = degrees;
                    if (moveInterface != null) {
                        moveInterface.getCurrentDegrees((degrees + startDegrees));
                    }
                    if (scanDegrees > 270) {
                        return false;
                    }
                    invalidate();
                } else {
                    scanDegrees = 270;
                }
                return true;


        }
        return true;
    }


    public void setMoveInterface(MoveInterface moveInterface) {
        this.moveInterface = moveInterface;
    }

    public interface MoveInterface {
        public void getCurrentDegrees(int degress);
    }
}
