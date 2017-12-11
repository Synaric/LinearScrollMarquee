package com.synaric.marquee;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import marquee.synaric.com.library.R;

/**
 * 纵向轮播TextView，同时TextView自身横向滚动内容。
 * <br/><br/>Created by Synaric on 2017/11/13.
 */
public class LinearScrollMarquee extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "LinearScrollMarquee";
    private static final int MAX_FIXED_INTERNAL_COUNT = 15;
    private static final int FIXED_INTERNAL = 5000;
    private static final int SCROLL_INTERNAL_PER_CHARACTER = 300;

    private String[] text;
    private MarqueeTextView firstText;
    private MarqueeTextView secondText;
    private float scrollLength;
    private int runCount;
    private ItemAdapter adapter;
    private OnItemClickListener onItemClickListener;

    public LinearScrollMarquee(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_linear_scroll, this);
        firstText = (MarqueeTextView) findViewById(R.id.first);
        secondText = (MarqueeTextView) findViewById(R.id.second);

        firstText.setOnClickListener(this);
        secondText.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "mode = " + mode);
        Log.d(TAG, "size = " + size);
        if (mode == MeasureSpec.EXACTLY) {
            scrollLength = size;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            measureChild(firstText, widthMeasureSpec, heightMeasureSpec);
            measureChild(secondText, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            List data = adapter.getData();
            if (data != null) {
                String[] text = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    String str = adapter.onBind(data.get(i));
                    text[i] = str;
                }
                setText(text);
            }
        }
    }

    public void setText(String[] text) {
        if (text == null || text.length == 0) {
            return;
        }
        this.text = text;
        firstText.setText(text[0]);
        if (text.length > 1) {
            secondText.setText(text[1]);
            secondText.setEllipsize(null);

            ticker.removeMessages(0);
            ticker.sendEmptyMessageDelayed(0, calculateTime(text[0]));
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private int calculateTime(String text) {
        return text.length() < MAX_FIXED_INTERNAL_COUNT ?
                FIXED_INTERNAL : SCROLL_INTERNAL_PER_CHARACTER * text.length();
    }

    private Runnable doScroll = new Runnable() {
        @Override
        public void run() {
            if (runCount == Integer.MAX_VALUE) {
                runCount = 0;
            } else {
                runCount++;
            }

            float firstStart = runCount % 2 == 0 ? scrollLength : 0;
            float firstEnd = runCount % 2 == 0 ? 0 : -scrollLength;
            float secondStart = runCount % 2 == 0 ? -scrollLength : 0;
            float secondEnd = runCount % 2 == 0 ? -2 * scrollLength : -scrollLength;

            firstText.getTranslationY();
            ObjectAnimator firstOa =
                    ObjectAnimator.ofFloat(firstText, "translationY", firstStart, firstEnd);
            firstOa.setDuration(500);
            firstOa.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (runCount % 2 == 1) {
                        firstText.setTranslationY(scrollLength);
                        int index = (runCount + 1) % text.length;
                        firstText.setText(text[index]);
                    }
                    firstText.setEllipsize(runCount % 2 == 0 ? TextUtils.TruncateAt.MARQUEE : null);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            firstOa.start();

            ObjectAnimator secondOa =
                    ObjectAnimator.ofFloat(secondText, "translationY", secondStart, secondEnd);
            secondOa.setDuration(500);
            secondOa.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (runCount % 2 == 0) {
                        secondText.setTranslationY(0);
                        int index = (runCount + 1) % text.length;
                        secondText.setText(text[index]);
                    }
                    secondText.setEllipsize(runCount % 2 == 1 ? TextUtils.TruncateAt.MARQUEE : null);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            secondOa.start();
        }
    };

    private Handler ticker = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            doScroll.run();
            ticker.sendEmptyMessageDelayed(0, calculateTime(text[runCount % text.length]));
        }
    };

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null && text != null) {
            int position = runCount % text.length;
            onItemClickListener.onClick(v, position, adapter.getData().get(position));
        }
    }

    public interface OnItemClickListener<T> {

        void onClick(View view, int position, T data);
    }

    public static abstract class ItemAdapter<T> {

        private List<T> data;

        public ItemAdapter(List<T> data) {
            this.data = data;
        }

        public abstract String onBind(T t);

        public List<T> getData() {
            return data;
        }
    }
}
