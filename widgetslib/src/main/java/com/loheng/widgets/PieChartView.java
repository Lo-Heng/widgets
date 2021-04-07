package com.loheng.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author lzh
 * @mail 560182@gree.com.cn
 * @time 2020/5/23 9:29 AM
 * @detail 饼状图的view
 * 每一个色块均可以着色，可以是渐变色或者单色，按照需求使用即可
 * @revise
 */
public class PieChartView extends View {

    /**
     * 每块占比的绘制的颜色
     */
    private List<Integer> mColorList = new ArrayList<>();
    /**
     * 渐变颜色
     */
    private List<int[]> mGradientColors = new ArrayList<>();
    /**
     * 圆弧占比的集合
     */
    private List<Float> mRateList = new ArrayList<>();
    /**
     * 是否展示图例
     */
    private boolean isShowLegend;
    /**
     * 圆弧半径
     */
    private float radius;

    /**
     * 不同色块之间是否需要空隙offset
     */
    private int offset;
    /**
     * 字体大小(全部更改，无法局部更改)
     */
    private float showRateSize;
    /**
     * 线条宽度
     */
    private int strokeWidth;
    /**
     * 整个饼图的旋转角度
     */
    private float mRotateAngle = -90;
    /**
     * 对应比例的名称
     */
    private List<String> mNameList = new ArrayList<>();
    /**
     * 用于计算文字控件大小
     */
    private Rect textRect = new Rect();
    /**
     * 图例的方型点 大小
     */
    private int mDotsSize;
    /**
     * 文字和方块的间隔
     */
    private int mTextOffset;
    /**
     * 图例最大字数限制
     */
    public static final int MAX_TEXT = 4;
    private Context mContext;
    private Paint mPaint;
    /**
     * 饼图的框
     */
    private RectF mCircleRecf = new RectF();
    private Paint textPaint;
    private float mPosition;
    private SweepGradient mSweepGradient;


    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init();
        insertTest();
        refreshData();
    }

    /**
     * 测试和预览用，方便之后的使用和测试
     */
    private void insertTest() {
        mRateList.add(0.1f);
        mRateList.add(0.1f);
        mRateList.add(0.1f);
        mRateList.add(0.1f);
        mRateList.add(0.1f);
        mRateList.add(0.3f);
        mRateList.add(0.1f);
        mRateList.add(0.1f);

        mNameList.add("测试123123123123112312312312323");
        mNameList.add("测试");
        mNameList.add("测试");
        mNameList.add("测试");
        mNameList.add("测试");
        mNameList.add("测试");
        mNameList.add("测试");
        mNameList.add("测试");

//        mColorList.add(Color.parseColor("#bafc7e"));
//        mColorList.add(Color.parseColor("#bafc7e"));
//        mColorList.add(Color.parseColor("#bafc7e"));
//        mColorList.add(Color.parseColor("#bafc7e"));
//        mColorList.add(Color.parseColor("#bafc7e"));
//        mColorList.add(Color.parseColor("#78e4ac"));
//        mColorList.add(Color.parseColor("#36a4ac"));
//        mColorList.add(Color.parseColor("#36a4ac"));

        mGradientColors.add(new int[]{0xffc6cdff, 0xff83b8ff,0xff248bd6,0xff0f6bae});
//        mGradientColors.add(new int[]{0xffffded4, 0xffffdeaa});
//        mGradientColors.add(new int[]{0xffff9f83, 0xffff6b40});
//        mGradientColors.add(new int[]{0xffff6b40, 0xffff5220});
//        mGradientColors.add(new int[]{0xffff3c03, 0xffe83500});
//        mGradientColors.add(new int[]{0xffd13000, 0xffbc2b00});
//        mGradientColors.add(new int[]{0xffdeb9fe, 0xffab4cfe});
//        mGradientColors.add(new int[]{0xff9d2efe, 0xff7601dc});
//        mGradientColors.add(new int[]{0xff7601dc, 0xff5601a0});
    }

    /**
     * 需要每次都刷新的数据
     */
    private void refreshData() {

        measureTextRect();
    }

    /**
     * 图例中最长的文字
     */
    private void measureTextRect() {
        //默认长度
        int len = 0;
        String name = "";
        for (int i = 0; i < mNameList.size(); i++) {
            if (len < mNameList.get(i).length()) {
                name = mNameList.get(i);
                len = mNameList.get(i).length();
                if(mNameList.get(i).length() > MAX_TEXT){
                    mNameList.set(i,name.substring(0,MAX_TEXT));
                }
            }
        }

        //默认数据
        name = Math.min(name.length(),MAX_TEXT) + " " + "000%";
        mPaint.getTextBounds(name, 0, name.length(), textRect);
    }

    /**
     * 初始化数据 原则上只执行一次
     */
    private void init() {
        //默认从12点方向开始画
        mRotateAngle = -90;
        radius = dp2px(mContext, 68);
        showRateSize = dp2px(mContext, 10);
        strokeWidth = dp2px(mContext, 33.6f);
        offset = dp2px(mContext, 1);
        mDotsSize = dp2px(mContext, 12);
        mTextOffset = dp2px(mContext, 2);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(showRateSize);
        isShowLegend = true;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();

        int widthSize = measureWidth(minimumWidth, widthMeasureSpec);
        int heightSize = measureHeight(minimumHeight, heightMeasureSpec);

        //保存测量结果
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 计算宽度
     * @param defaultWidth
     * @param measureSpec
     * @return 宽度
     */
    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int areaLegend = isShowLegend ? (mTextOffset * 2 + mDotsSize + textRect.width() ) * 2 : 0;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) (radius * 2) + strokeWidth * 2 + getPaddingLeft() + getPaddingRight() + areaLegend;
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = (int) (radius * 2) + strokeWidth * 2 + getPaddingLeft() + getPaddingRight() + areaLegend;
            default:
                break;
        }
        return defaultWidth;
    }

    /**
     * 计算高度
     * @param defaultHeight
     * @param measureSpec
     * @return 计算高度
     */
    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);


        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (radius * 2) + strokeWidth + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = (int) (radius * 2) + strokeWidth + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }
        return defaultHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float circleBeginX = isShowLegend ? textRect.width() + 2 * mTextOffset + mDotsSize + strokeWidth : strokeWidth;
        float circleBeginY = strokeWidth / 2;
        float circleEndX = circleBeginX + 2 * radius;
        float circleEndY = circleBeginY + 2 * radius;
        mCircleRecf.left = circleBeginX;
        mCircleRecf.right = circleEndX;
        mCircleRecf.top = circleBeginY;
        mCircleRecf.bottom = circleEndY;

        float startAngle = mRotateAngle;
        //1.绘制圆饼
        for (int i = 0; i < mRateList.size(); i++) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(strokeWidth);

            drawArcs(canvas, startAngle, i);
            //2.画图例
            if (isShowLegend) {
                drawLegends(canvas, i);
            }
            startAngle = startAngle + (mRateList.get(i) * 360);
        }

    }

    /**
     * 画单个扇形
     * @param canvas 画布
     * @param startAngle 起始角度
     * @param index 索引
     */
    private void drawArcs(Canvas canvas, float startAngle, int index) {

        if (mGradientColors.size() == 0) {
            //如果是属于单色
            mPaint.setColor(mColorList.get(index));
        }else if(mGradientColors.size() == 1){
            //如果是属于渐变色
            int[] colors = mGradientColors.get(0);
            //间隔
            float step = 1;

            if(colors.length > 1) {
                step = 1f / (colors.length - 1);
            }
            //起始位置
            float startPos = (mRotateAngle * (-1)) / 360;

            float[] positions = new float[colors.length];
            for (int j = 0; j < colors.length; j++) {
                positions[j] = startPos;
                startPos += step;
            }

            //创建一个渲染器 旋转渐变
            mSweepGradient = new SweepGradient(mCircleRecf.centerX(), mCircleRecf.centerY(), colors, positions);

            //旋转
            Matrix matrix = new Matrix();
            matrix.setRotate(mRotateAngle, mCircleRecf.centerX(), mCircleRecf.centerY());
            mSweepGradient.setLocalMatrix(matrix);

            //把渐变设置到笔刷
            mPaint.setShader(mSweepGradient);

        }else {
            //如果是属于渐变色
            int[] colors = index >= mGradientColors.size() ? mGradientColors.get(mGradientColors.size() - 1) : mGradientColors.get(index);

            int sweepAngle = (int) (mRateList.get(index) * (360)) - offset;
            float[] positions = new float[colors.length];
            //起始位置
            float startPos = (startAngle + mRotateAngle * (-1)) / 360;
            //最大值
            float maxPos = startPos + mRateList.get(index);

            //间隔
            float step = 1;

            if(colors.length > 1) {
                step = mRateList.get(index) / (colors.length - 1);
            }
            for (int j = 0; j < colors.length; j++) {
                positions[j] = startPos;
                startPos += step;
            }

            //创建一个渲染器 旋转渐变
            mSweepGradient = new SweepGradient(mCircleRecf.centerX(), mCircleRecf.centerY(), colors, positions);

            //旋转
            Matrix matrix = new Matrix();
            matrix.setRotate(mRotateAngle, mCircleRecf.centerX(), mCircleRecf.centerY());
            mSweepGradient.setLocalMatrix(matrix);

            //把渐变设置到笔刷
            mPaint.setShader(mSweepGradient);

        }

        if (mRateList.size() == 1 && mRateList.get(index) == 1) {
            canvas.drawArc(mCircleRecf, startAngle, (mRateList.get(index) * (360)), false, mPaint);

        } else {
            canvas.drawArc(mCircleRecf, startAngle, (mRateList.get(index) * (360)) - offset, false, mPaint);
        }
    }

    /**
     * 画图例
     * @param canvas 画布
     * @param index
     */
    private void drawLegends(Canvas canvas, int index) {

        float beginX, endX;
        //换一边的分界点
        int dividePos = mRateList.size() / 2 + mRateList.size() % 2;
        //图例的间隔
        int gap = (int) (2 * radius + strokeWidth) / dividePos - mDotsSize / 2;
        mPaint.setStyle(Paint.Style.FILL);
        //分两边分布
        float beginY;
        float endY;
        if (index < dividePos) {
            //右边的从上到下排列
            beginY = (textRect.height()) + gap * (index % dividePos) + mDotsSize * (index % dividePos);

            beginX = mCircleRecf.right + strokeWidth / 2 + 2 * mTextOffset;
            endX = beginX + mDotsSize;
        } else {
            //左边由下到上排列
            float y = (textRect.height()) + gap * (dividePos - 1) + mDotsSize * (dividePos - 1);
            beginY = y - gap * (index % dividePos) - mDotsSize * (index % dividePos);
            beginX = mCircleRecf.left - strokeWidth / 2 - mDotsSize - 2 * mTextOffset;
            endX = beginX + mDotsSize;
        }
        endY = beginY + mDotsSize;
        canvas.drawRect(beginX, beginY, endX, endY, mPaint);

        //单独开一支画笔写字
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(showRateSize);
        String text = mNameList.get(index) + " " + (int) (mRateList.get(index) * 100) + "%";

        float textWidth = textPaint.measureText(text);
        if (index < dividePos) {
            canvas.drawText(text, endX + mTextOffset, (beginY + endY) / 2 + textRect.height() / 2, textPaint);
        } else {
            canvas.drawText(text, endX - mDotsSize - textWidth - mTextOffset, (beginY + endY) / 2 + textRect.height() / 2, textPaint);
        }

    }




    /**
     * 更新数据
     * @param rateList  色块占比
     * @param colorList 色块颜色
     * @param nameList  色块名称
     */
    public void updateData(List<Float> rateList, int[] colorList, List<String> nameList) {
        mRateList.clear();
        mNameList.clear();
        mColorList.clear();
        mGradientColors.clear();

        mRateList.addAll(rateList);
        mNameList.addAll(nameList);

        for (int color : colorList) {
            mColorList.add(color);
        }

        refreshData();
        invalidate();
    }



    /**
     * 更新数据
     * @param rateList  色块占比
     * @param colorList 色块颜色(渐变)
     * @param nameList  色块名称
     */
    public void updateData(List<Float> rateList, List<int[]> colorList, List<String> nameList) {

        mRateList.clear();
        mNameList.clear();
        mGradientColors.clear();
        mColorList.clear();

        mRateList.addAll(rateList);
        mNameList.addAll(nameList);
        mGradientColors.addAll(colorList);
        refreshData();
        invalidate();
    }

    /**
     * 默认的0度是3点钟方向，如果需要旋转图形请在此设置旋转的角度
     * @param rotateAngle 旋转的角度(0-360)
     */
    public void setRotateAngle(float rotateAngle) {
        mRotateAngle = rotateAngle;
        invalidate();
    }

    /**
     * 是否显示图例
     * @param showLegend
     */
    public void setShowLegend(boolean showLegend) {
        isShowLegend = showLegend;
    }

    /**
     * 获取格式化的保留两位数的数
     */
    public String getFormatPercentRate(float dataValue) {
        //构造方法的字符格式这里如果小数不足2位,会以0补足
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        return decimalFormat.format(dataValue);
    }

    /**
     * dp转px
     *
     */
    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
