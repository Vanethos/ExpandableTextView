package com.vanethos.expandabletextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by goncalopalma on 05/03/2018.
 */

public class ExpandableTextView extends AppCompatTextView {
    private TextView textView;
    private String readMoreLabel;
    private String readLessLabel;
    private int ellipsizeTextColor;
    private int maxLines;
    private String expandedText;
    private ReadMoreClickableSpan readMoreClickableSpan;
    private boolean underlinedEllipsize;
    private boolean isTextExpanded;
    private boolean needsReadLess;
    private String contractedString;

    private float MAX_WIDTH_PERCENTAGE = 0.9f;
    private float screenWidth;


    public ExpandableTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    protected void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyleAttr, 0);
            try {
                textView = this;
                textView.setMaxLines(Integer.MAX_VALUE); // default value for TextView's maxLines attribute
                maxLines = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "maxLines", 3);
                readMoreLabel = getIntOrStringResource(ta, R.styleable.ExpandableTextView_readMoreLabel, R.string.ellipsize_read_more);
                readLessLabel = getIntOrStringResource(ta, R.styleable.ExpandableTextView_readLessLabel, R.string.ellipsize_read_less);
                ellipsizeTextColor = getIntOrColorResource(ta,R.styleable.ExpandableTextView_ellipsizeColor, R.color.default_ellipsize_color);
                needsReadLess = ta.getBoolean(R.styleable.ExpandableTextView_needsReadLess, true);
                underlinedEllipsize = ta.getBoolean(R.styleable.ExpandableTextView_underlineEllipsize, false);
                readMoreClickableSpan = new ReadMoreClickableSpan();
                getOnGlobalLayoutChangeAndEllipsize();
            } finally {
                ta.recycle();
            }
        }
    }

    private void getOnGlobalLayoutChangeAndEllipsize() {
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                expandedText = textView.getText().toString();
                screenWidth = textView.getLayout().getWidth();
                toggleEllipsizeText(isTextExpanded);
                ViewTreeObserver currentTreeObserver = textView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT < 16) {
                    currentTreeObserver.removeGlobalOnLayoutListener(this);
                } else {
                    currentTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void toggleEllipsizeText(boolean isTextExpanded) {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        if (!isTextExpanded) {
            contractText();
        } else {
            expandText();
        }
    }

    private void contractText() {
        int line = (maxLines+1) <= 1 ? 0 : maxLines - 1;
        if (TextUtils.isEmpty(contractedString)) contractedString = getContractedString(line);
        textView.setText(getClickableEllipsizeText(contractedString, readMoreLabel));
    }

    private void expandText() {
        if (!needsReadLess) {
            textView.setText(expandedText);
        } else {
            textView.setText(getClickableEllipsizeText(expandedText, readLessLabel));
        }
    }

    private CharSequence getClickableEllipsizeText (String text, String ellipsis) {
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        spannable.append(text)
                .append(" ")
                .append(ellipsis);
        spannable.setSpan(readMoreClickableSpan, spannable.length() - ellipsis.length(), spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private String getContractedString(int line) {
        int originalLineEndIndex = textView.getLayout().getLineVisibleEnd(line);
        int lineEndIndex = getLineEndIndex(line, originalLineEndIndex);
        if (
                originalLineEndIndex == lineEndIndex &&
                        getStringWidth(readLessLabel) + textView.getLayout().getLineWidth(line) < MAX_WIDTH_PERCENTAGE * screenWidth
                ) {
            return textView.getText()
                    .subSequence(0, lineEndIndex).toString();
        }
        return textView.getText()
                .subSequence(0, lineEndIndex - (readMoreLabel.length() + 1)).toString();
    }

    private int getLineEndIndex(int line, int originalEndLineIndex) {
        if (textView.getLayout().getLineWidth(line) >= MAX_WIDTH_PERCENTAGE * screenWidth) {
            return originalEndLineIndex -
                    getLastWordLength(
                            textView.getText().toString().substring(0, originalEndLineIndex)
                    );
        } else {
            return originalEndLineIndex;
        }
    }

    //region Utils
    private int getIntOrColorResource(TypedArray ta, int typedArrayIndex, int defaultResIdValue) {
        return ta.getResourceId(typedArrayIndex, -1) == -1 ?
                ta.getColor(typedArrayIndex, getResourceColor(defaultResIdValue)) :
                getResourceColor(ta.getResourceId(typedArrayIndex, defaultResIdValue));
    }

    private String getIntOrStringResource(TypedArray ta, int typedArrayIndex, int defaultResIdValue) {
        return ta.getString(typedArrayIndex) == null ?
                getResourceString(ta.getResourceId(typedArrayIndex, defaultResIdValue)) : ta.getString(typedArrayIndex);
    }

    private String getResourceString(int id) {
        return getResources().getString(id);
    }

    private int getResourceColor(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    private int getLastWordLength(String text) {
        return text.substring(text.lastIndexOf(" "), text.length() - 1).length();
    }

    private int getStringWidth(String text) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }
    //endregion

    //region ClickableSpan styling
    private class ReadMoreClickableSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            isTextExpanded = !isTextExpanded;
            toggleEllipsizeText(isTextExpanded);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ellipsizeTextColor);
            ds.setUnderlineText(underlinedEllipsize);
        }
    }
    //endregion
}
