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
    private int mCenter;//��Ļ����в�
    private int mRadius;
    private int insideRadius; //�ڲ��뾶
    
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
    private int finalDegrees;//�����ָ�뿪�Ķ��� ����ģʽ��Ҫ
    private Rect mBound;

 // ָ���˹�Դ�ķ���ͻ�����ǿ������Ӹ���Ч��
    /** The emboss. */
    private EmbossMaskFilter emboss = null;
    // ���ù�Դ�ķ���
    /** The direction. */
    float[] direction = new float[]{1,1,1};
    //���û���������
    /** The light. */
    float light = 0.4f;
    // ѡ��ҪӦ�õķ���ȼ�
    /** The specular. */
    float specular = 6;
    // �� maskӦ��һ�������ģ��
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
	
    // ���ʳ�ʼ��
    private void init() {
       
        mPaint1 = new Paint (); 
        mPaint1.setStrokeWidth(circleWidth);
        //�������
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

        //�����ı��ķ�Χ
        mBound = new Rect();
    }


    // ��дonDraw()���л���  
    @Override
    protected void onDraw(Canvas canvas) {

        initSize();
        
        mPaint1.setShader(null);
        canvas.drawArc(mArcRectf,135,270, false, mPaint1);
        mPaint1.setShader(mSweepGradient);
        
        canvas.drawArc(mArcRectf, 135, scanDegrees, false, mPaint1);
        mPaint2.setColor(Color.WHITE);
        mPaint1.setMaskFilter(emboss);
        
     // ���û��������˾�  ,�����������ʽ
        mPaint1.setMaskFilter(mBlur);
        
     //������Բ
        canvas.drawCircle(mCenter, mCenter, 40, mPaint2);
        
        //����������ɫ
        mPaint2.setColor(textColor);
        //���������С
        mPaint2.setTextSize(textSize);
        //�õ�����Ŀ�߷�Χ
        text = String.valueOf(finalDegrees);
        mPaint2.getTextBounds(text, 0, text.length(), mBound);
        //��������
        canvas.drawText(text, mCenter - mBound.width() / 2, mCenter + mBound.height() / 2, mPaint2);
    
        //���� ÿ��3�Ȼ�һ���� ����Բ����120����
        for (int i = 0; i < 120; i++) {
            //Բ��������ֱ�ߵ�Y����
            top = mCenter - mRadius - circleWidth / 2;
            // ȥ���ײ������������� ֻ��ת������
            if (i <= 45 || i >= 75) {
                if (i % 15 == 0) {
                    //����ʱY����������
                    top = top - 15;
                }
                //��һ����Ϊy��
                canvas.drawLine(mCenter, mCenter - mRadius+15, mCenter, top+5, mLinePaint);
            }
            //��ת
            canvas.rotate(3, mCenter, mCenter);
        }
        //������+50��������־���Բ��ߵľ���
        
        //б��
        int c = mRadius + circleWidth / 2 + 45;
        //x�������ֵ����ľ���Բ�ĵľ��� ����ԭ������������ֵĳ���
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
        //����ɫ
        mSweepGradient = new SweepGradient(mCenter, mCenter, colors, null);

    }
    
    /**
     * �������
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        //Ĭ�Ͽ��;
        defaultValue = Integer.MAX_VALUE;

        switch (mode) {
            case MeasureSpec.AT_MOST:
                //���ֵģʽ ���ؼ���layout_Width��layout_height����ָ��Ϊwrap_contentʱ
                size = Math.min(defaultValue, size);
                break;
            case MeasureSpec.EXACTLY:
                //��ȷֵģʽ ���ؼ���android:layout_width=��100dp����android:layout_height=��match_parent��ʱ

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
                //���ֵģʽ ���ؼ���layout_Width��layout_height����ָ��Ϊwrap_contentʱ
                size = Math.min(defaultValue, size);
                break;
            case MeasureSpec.EXACTLY:
                //��ȷֵģʽ ���ؼ���android:layout_width=��100dp����android:layout_height=��match_parent��ʱ

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
                //�ж���ָ�ڿհ������ܻ���
                if (!isCanMove) {
                    return false;
                }
                float y = event.getY();
                float x = event.getX();
                float firstX = event.getX();
                float firstY = event.getY();
                //�жϵ�ǰ��ָ����Բ�ĵľ��� ������Բ�ĵ��Ҳ�
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
                //�жϵ�ǰ��ָ�Ƿ��ڿհ�����
                if (Math.sqrt(x * x + y * y) < (mRadius - 40)) {
                    isCanMove = false;
                    return false;
                }
                float v = x / (float) Math.sqrt(x * x + y * y);
                // ����cos��Ƕ�
                double acos = Math.acos(v);
                acos = Math.toDegrees(acos);
                //��ָ�ڵ�������
                if (firstX <= mCenter && firstY >= mCenter) {
                    acos = 180 - acos;
                } else if (firstX <= mCenter && firstY <= mCenter) {
                    //��ָ�ڵڶ�����
                    acos = acos + 180;
                } else if (firstX >= mCenter && firstY <= mCenter) {
                    //��ָ�ڵ�һ����
                    acos = 360 - acos;
                } else {
                }
                scanDegrees = (int) acos;
                //������� ÿ22.5��ֵ+1
                if (scanDegrees >= 135 && scanDegrees <= 360) {
                    scanDegrees = scanDegrees - 135;
                    int degrees = (int) (scanDegrees / 2.7);
                    finalDegrees = degrees;
                    if (moveInterface != null) {
                        moveInterface.getCurrentDegrees((degrees + startDegrees));
                    }
                    invalidate();

                } else if (scanDegrees <= 65) {
                    //С��45�޷��㵽���һ�� ��������20��
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
