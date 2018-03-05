package com.vanethos.expandabletextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
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
    private int ellipsizeReadMore;
    private int ellipsizeReadLess;
    private String ellipsizedText;
    private int ellipsizeTextColor;
    private int maxLines;
    private String expandedText;
    private ReadMoreClickableSpan readMoreClickableSpan;
    private boolean underlinedEllipsize;
    private boolean isTextExpanded;
    private boolean needsReadLess;

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
                maxLines = ta.getInteger(R.styleable.ExpandableTextView_ellipsizeAtLine, 3);
                ellipsizeReadMore = ta.getResourceId(R.styleable.ExpandableTextView_ellipsizeReadMore, R.string.ellipsize_read_more);
                ellipsizeReadLess = ta.getResourceId(R.styleable.ExpandableTextView_ellipsizeReadLess, R.string.ellipsize_read_less);
                ellipsizeTextColor = ta.getResourceId(R.styleable.ExpandableTextView_ellipsizeColor, R.color.default_ellipsize_color);
                needsReadLess = ta.getBoolean(R.styleable.ExpandableTextView_needsReadLess, true);
                underlinedEllipsize = ta.getBoolean(R.styleable.ExpandableTextView_underlineEllipsize, false);
                ellipsizedText = textView.getContext().getResources().getString(ellipsizeReadMore);
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
        String ellipsis = textView.getContext().getString(ellipsizeReadMore);
        if ((maxLines+1) <= 1) {
            int lineEndIndex = textView.getLayout().getLineEnd(0);
            String text = textView.getText()
                    .subSequence(0, lineEndIndex - ellipsizedText.length()).toString();
            textView.setText(getClickableEllipsizeText(text, ellipsis));
        } else if (textView.getLineCount() >= (maxLines+1)) {
            int lineEndIndex = textView.getLayout().getLineEnd((maxLines+1) - 1);
            int offsetEllipsizeCharacter =
                    ellipsizedText.contains(textView.getContext().getResources().getString(R.string.ellipsize_character)) ? 3 : 0;
            String text = textView.getText()
                    .subSequence(0, lineEndIndex - ellipsizedText.length() - offsetEllipsizeCharacter).toString();
            textView.setText(getClickableEllipsizeText(text, ellipsis));
        }
    }

    private void expandText() {
        if (!needsReadLess) {
            textView.setText(expandedText);
        } else {
            String ellipsis = textView.getContext().getString(ellipsizeReadLess);
            textView.setText(getClickableEllipsizeText(expandedText, ellipsis));
        }
    }

    private CharSequence getClickableEllipsizeText (String text, String ellipsis) {
        SpannableStringBuilder spannableText = new SpannableStringBuilder(text, 0, text.length())
                .append(ellipsis);
        spannableText.setSpan(readMoreClickableSpan, spannableText.length() - ellipsis.length(), spannableText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableText;
    }

    private class ReadMoreClickableSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            isTextExpanded = !isTextExpanded;
            toggleEllipsizeText(isTextExpanded);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(ellipsizeTextColor));
            ds.setUnderlineText(underlinedEllipsize);
        }
    }
}
