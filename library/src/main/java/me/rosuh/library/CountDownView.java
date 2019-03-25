package me.rosuh.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CountDownView extends LinearLayout{

    private TextView mDaysTv;
    private TextView mMinesTv;
    private TextView mHourTv;
    private TextView mSecTv;
    private TextView mMilliTv;
    private DecimalFormat decimalFormat = new DecimalFormat("00");
    private int mTvBg;
    private int mTvTextColor;
    private float mTvTextSize;
    private int mTvWidth;
    private int mTvHeight;

    private boolean isConvertDaysToHours;

    private String daysSuffix;
    private String hoursSuffix;
    private String minuteSuffix;
    private String secondsSuffix;
    private String millisecondsSuffix;

    private InnerCountDownTimer countDownTimer;
    private static final long defaultCountdownInterval = 1;
    private CountDownStatusChange countDownStatusChange;


    public CountDownView(Context context) {
        super(context);
        initView(context);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        mTvBg = typedArray.getResourceId(R.styleable.CountDownView_tv_bg, R.drawable.count_down_text_view_bg);
        mTvTextColor = typedArray.getColor(R.styleable.CountDownView_tv_color, Color.WHITE);
        mTvTextSize = typedArray.getDimension(R.styleable.CountDownView_tv_size, 14);
        mTvWidth = (int)typedArray.getDimension(R.styleable.CountDownView_tv_width, 20);
        mTvHeight = (int)typedArray.getDimension(R.styleable.CountDownView_tv_height, 20);
        // 是否将天数转换为小时显示
        isConvertDaysToHours = typedArray.getBoolean(R.styleable.CountDownView_isConvertDaysToHours, false);

        // 单位后缀
        daysSuffix = typedArray.getString(R.styleable.CountDownView_daysSuffix);
        hoursSuffix = typedArray.getString(R.styleable.CountDownView_hoursSuffix);
        minuteSuffix = typedArray.getString(R.styleable.CountDownView_minuteSuffix);
        secondsSuffix = typedArray.getString(R.styleable.CountDownView_secondsSuffix);
        millisecondsSuffix = typedArray.getString(R.styleable.CountDownView_millisecondsSuffix);
        typedArray.recycle();
        initView(context);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.count_down_layout, this, true);
        mDaysTv = findViewById(R.id.count_down_tv_days);
        mHourTv = findViewById(R.id.count_down_tv_hour);
        mMinesTv = findViewById(R.id.count_down_tv_min);
        mSecTv = findViewById(R.id.count_down_tv_sec);
        mMilliTv = findViewById(R.id.count_down_tv_mini);
        applyStyleForAllTvs(mDaysTv, mHourTv, mMinesTv, mSecTv, mMilliTv);

        if (isConvertDaysToHours){
            // 隐藏天数
            mDaysTv.setVisibility(GONE);
            findViewById(R.id.tv_days_divider).setVisibility(GONE);
        }
    }

    private void applyStyleForAllTvs(TextView...tvArray){
        for (TextView aTvArray : tvArray) {
            applyStyleForTv(aTvArray);
        }
    }

    private void applyStyleForTv(TextView textView){
        textView.setTextSize(mTvTextSize);
        textView.setTextColor(mTvTextColor);
        textView.setBackgroundResource(mTvBg);
        LayoutParams params = (LayoutParams) textView.getLayoutParams();
        params.width = mTvWidth;
        params.height = mTvHeight;
        textView.setLayoutParams(params);
    }

    public CountDownView buildTimer(long millisInFuture) {
        return buildTimer(millisInFuture, defaultCountdownInterval);
    }

    public CountDownView buildTimer(long millisInFuture, long countDownInterval) {
        if (this.countDownTimer == null){
            countDownTimer = new InnerCountDownTimer(millisInFuture, countDownInterval);
        }else {
            countDownTimer.cancel();
            countDownTimer = new InnerCountDownTimer(millisInFuture, countDownInterval);
        }
        return this;
    }

    public void start(){
        if (countDownTimer != null){
            countDownTimer.start();
        }
    }

    public void cancel(){
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    private class InnerCountDownTimer extends CountDownTimer{
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public InnerCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateView(millisUntilFinished);
            if (countDownStatusChange != null){
                countDownStatusChange.onTick();
            }
        }

        @Override
        public void onFinish() {
            resetView();
            if (countDownStatusChange != null){
                countDownStatusChange.onFinish();
            }
        }
    }

    /**
     * 每毫秒更新视图
     * @param millisUntilFinished 当前的剩余时间
     */
    private void updateView(final long millisUntilFinished){
        // 余数，从最高位（天）开始往下计算
        long remainder;
        long days = millisUntilFinished / (1000 * 60 * 60 * 24);
        if (isConvertDaysToHours){
            // 如果选择把天数转换成小时，则不计算天数，也就是不把天数除掉
            remainder = millisUntilFinished;
        }else {
            remainder = millisUntilFinished % (1000 * 60 * 60 * 24);
        }
        long hours = remainder / (1000 * 60 * 60);
        remainder = millisUntilFinished % (1000 * 60 * 60);
        long minutes = remainder / (1000 * 60);
        remainder = millisUntilFinished % (1000 * 60);
        long seconds = remainder / (1000);
        // 毫秒只显示后两位
        remainder = millisUntilFinished % (100);

        if (mDaysTv.getVisibility() != GONE && !mDaysTv.getText().equals(decimalFormat.format(days))){
            mDaysTv.setText(decimalFormat.format(days));
        }
        if (!mHourTv.getText().equals(decimalFormat.format(hours))){
            mHourTv.setText(decimalFormat.format(hours));
        }
        if (!mMinesTv.getText().equals(decimalFormat.format(minutes))){
            mMinesTv.setText(decimalFormat.format(minutes));
        }
        if (!mSecTv.getText().equals(decimalFormat.format(seconds))){
            mSecTv.setText(decimalFormat.format(seconds));
        }
        mMilliTv.setText(decimalFormat.format(remainder));
    }

    private void resetView(){
        mDaysTv.setText("00");
        mHourTv.setText("00");
        mMinesTv.setText("00");
        mSecTv.setText("00");
        mMilliTv.setText("00");
    }

    interface CountDownStatusChange{
        void onTick();

        void onFinish();
    }


    public void setCountDownStatusChange(CountDownStatusChange countDownStatusChange) {
        this.countDownStatusChange = countDownStatusChange;
    }

    public CountDownStatusChange getCountDownStatusChange() {
        return countDownStatusChange;
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }
}
