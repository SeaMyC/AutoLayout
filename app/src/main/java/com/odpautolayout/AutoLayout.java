package com.odpautolayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


/**
 * @author ChenHh
 * @time 2018/11/26 14:54
 * @des AutoLayout实现的功能：像目前主流软件淘宝，京东，搜索功能底下的搜索记录布局样式
 **/
public class AutoLayout extends ViewGroup {

    private int textColor;
    private int textBackgroundColor;
    private int textBackgroundResource;
    private float textViewSize;
    private int textViewPadding;
    private int textViewPaddingLeft;
    private int textViewPaddingRight;
    private int textViewPaddingTop;
    private int textViewPaddingBottom;
    private float childspacing;

    public ItemListener itemListener;
    public int contentSize = 0;
    public int lineNum = 1;


    private class NoImplementationException extends Exception {
        NoImplementationException(String s) {
            super(s);
        }

    }

    public AutoLayout(Context context) throws NoImplementationException {
        super(context);
        throw new NoImplementationException("未执行attributeSet");
    }

    public AutoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
    }

    public AutoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs);
    }

    public void initData(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoLayout);
        this.textColor = ta.getColor(R.styleable.AutoLayout_childTextColor, Color.BLACK);
        this.textViewSize = ta.getFloat(R.styleable.AutoLayout_childTextSize, 13);
        this.textBackgroundColor = ta.getColor(R.styleable.AutoLayout_childTextBackgroundColor, 0);
        this.textBackgroundResource = ta.getResourceId(R.styleable.AutoLayout_childTextBackgroundResource, 0);
        this.textViewPadding = ta.getInt(R.styleable.AutoLayout_childPadding, 0);
        this.textViewPaddingLeft = ta.getInt(R.styleable.AutoLayout_childLeftPadding, 0);
        this.textViewPaddingRight = ta.getInt(R.styleable.AutoLayout_childRightPadding, 0);
        this.textViewPaddingTop = ta.getInt(R.styleable.AutoLayout_childTopPadding, 0);
        this.textViewPaddingBottom = ta.getInt(R.styleable.AutoLayout_childBottomPadding, 0);
        this.childspacing = ta.getFloat(R.styleable.AutoLayout_childSpacing, 0);
        ta.recycle();
    }

    /**
     * @param infos 字符串集合
     */
    public void loadView(List<String> infos) {

        if (infos != null && infos.size() > 0 && !isXceedParentHeight()) {
            int index = 0;
            for (String info : infos) {
                if (!TextUtils.isEmpty(info)) {
                    TextView textView = new TextView(getContext());
                    textView.setTextColor(this.textColor);
                    textView.setTextSize(this.textViewSize);
                    textView.setText(info);
                    if (this.textBackgroundResource != 0) {
                        textView.setBackgroundResource(this.textBackgroundResource);
                    } else if (this.textBackgroundColor != 0) {
                        textView.setBackgroundColor(this.textBackgroundColor);
                    } else {
                        textView.setBackgroundResource(R.drawable.select_auto_background);
                    }

                    if (this.textViewPadding != 0) {
                        int paddingValue = dip2px(getContext(), this.textViewPadding);
                        textView.setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
                    } else {
                        int paddingLeftValue = dip2px(getContext(), this.textViewPaddingLeft);
                        int paddingRightValue = dip2px(getContext(), this.textViewPaddingRight);
                        int paddingTopValue = dip2px(getContext(), this.textViewPaddingTop);
                        int paddingBottomValue = dip2px(getContext(), this.textViewPaddingBottom);
                        textView.setPadding(paddingLeftValue, paddingTopValue, paddingRightValue, paddingBottomValue);
                    }
                    addView(textView, index);
                    index++;
                }
            }
            if (itemListener != null) {
                addListener();
            }
        } else if (infos != null && infos.size() > 0) {
            for (int i = 0; i < infos.size(); i++) {
                TextView childView = (TextView) getChildAt(i);
                if (childView != null) {
                    childView.setText(infos.get(i));
                }
            }
        }
    }

    public void clearChildView() {
        removeAllViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        lineNum = 1;
        int lineWidth = getPaddingLeft();
        int lineHeight = getPaddingTop();
        int margin = dip2px(getContext(), (childspacing > 0 ? childspacing : 5));
        int childCount = getChildCount();
        //实现布局代码逻辑
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView != null) {
                if (lineWidth + childView.getMeasuredWidth() < getWidth() - getPaddingRight()) {
                    childView.layout(lineWidth, lineHeight, childView.getMeasuredWidth() + lineWidth, childView.getMeasuredHeight() + lineHeight);
                    lineWidth = lineWidth + margin + childView.getMeasuredWidth();
                } else if (!isXceedParentHeight()) {
                    lineNum++;
                    lineWidth = getPaddingLeft();
                    lineHeight += margin + childView.getMeasuredHeight();
                    childView.layout(lineWidth, lineHeight, childView.getMeasuredWidth() + lineWidth, childView.getMeasuredHeight() + lineHeight);
                    lineWidth = lineWidth + margin + childView.getMeasuredWidth();
                } else {
                    int count = getChildCount() - i;
                    removeViews(i, count);
                    break;
                }
            }

        }
    }

    public boolean isXceedParentHeight() {
        View childView = getChildAt(0);
        if (childView != null) {
            contentSize = (lineNum * childView.getMeasuredHeight()) + (lineNum * dip2px(getContext(), (childspacing > 0 ? childspacing : 5)));
            if (contentSize + childView.getMeasuredHeight() > (getHeight() - getPaddingTop() - getPaddingBottom())) {
                return true;
            }
            return false;
        }
        return false;
    }


    public void addItemListener(ItemListener itemListener) {
        if (itemListener != null) {
            this.itemListener = itemListener;
            addListener();
        } else {
            throw new NullPointerException("ItemListener Must not be empty");
        }
    }

    private void addListener() {
        if (getChildCount() > 0 && itemListener != null) {
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView != null) {
                    final int item = i;
                    childView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemListener.registerListener(item, (TextView) v);
                        }
                    });
                }
            }
        }
    }

    public interface ItemListener {
        void registerListener(int position, TextView textView);
    }

    private int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}
