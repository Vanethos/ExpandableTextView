package com.vanethos.expandabletextview;

import android.annotation.TargetApi;
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
    private int ellipsizedTextResid;
    private String ellipsizedText;
    private int ellipsizeTextColor;
    private int maxLines;
    private String expandedText;
    private ReadMoreClickableSpan readMoreClickableSpan;
    private boolean underlinedEllipsize;

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
                ellipsizedTextResid = ta.getResourceId(R.styleable.ExpandableTextView_ellipsizeTextResId, R.string.default_ellipsize_text);
                ellipsizeTextColor = ta.getResourceId(R.styleable.ExpandableTextView_ellipsizeColor, R.color.default_ellipsize_color);
                underlinedEllipsize = ta.getBoolean(R.styleable.ExpandableTextView_underlineEllipsize, false);
                ellipsizedText = textView.getContext().getResources().getString(ellipsizedTextResid);
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
                ellipsizeText();
                ViewTreeObserver currentTreeObserver = textView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT < 16) {
                    currentTreeObserver.removeGlobalOnLayoutListener(this);
                } else {
                    currentTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void ellipsizeText() {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        if ((maxLines+1) <= 1) {
            int lineEndIndex = textView.getLayout().getLineEnd(0);
            String text = textView.getText()
                    .subSequence(0, lineEndIndex - ellipsizedText.length()).toString();
            textView.setText(getClickableEllipsizeText(text));
        } else if (textView.getLineCount() >= (maxLines+1)) {
            int lineEndIndex = textView.getLayout().getLineEnd((maxLines+1) - 1);
            int offsetEllipsizeCharacter =
                    ellipsizedText.contains(textView.getContext().getResources().getString(R.string.default_ellipsize_text)) ? 3 : 0;
            String text = textView.getText()
                    .subSequence(0, lineEndIndex - ellipsizedText.length() - offsetEllipsizeCharacter).toString();
            textView.setText(getClickableEllipsizeText(text));
        }
    }

    private void expandText() {
        textView.setText(expandedText);
    }

    private CharSequence getClickableEllipsizeText (String text) {
        SpannableStringBuilder spannableText = new SpannableStringBuilder(text, 0, text.length())
                .append(ellipsizedText);
        spannableText.setSpan(readMoreClickableSpan, spannableText.length() - ellipsizedText.length(), spannableText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableText;
    }

    private class ReadMoreClickableSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            expandText();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(ellipsizeTextColor));
            ds.setUnderlineText(underlinedEllipsize);
        }
    }
}
