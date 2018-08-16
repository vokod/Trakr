package com.awolity.trakr.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awolity.trakr.R;

public class ButtonSetting extends ConstraintLayout {

    private TextView labelTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private final Context context;

    public ButtonSetting(@NonNull Context context) {
        super(context);
        this.context = context;
        inflate();
    }

    public ButtonSetting(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate();
    }

    public ButtonSetting(@NonNull Context context, @Nullable AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate();
    }

    void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_setting_button, this, true);
        labelTextView = findViewById(R.id.tv_label);
        descriptionTextView = findViewById(R.id.tv_desc);
        iconImageView = findViewById(R.id.iv_icon);
    }

    private void setLabel(String labelText) {
        labelTextView.setText(labelText);
    }

    public void setDescription(String descriptionText) {
        if (descriptionText == null) {
            descriptionTextView.setVisibility(GONE);
            labelTextView.setPadding(labelTextView.getPaddingStart(),
                    getInPx(context, 12),
                    labelTextView.getPaddingEnd(),
                    getInPx(context, 12));
            return;
        }
        descriptionTextView.setText(descriptionText);
    }

    private void setIcon(int resId) {
        iconImageView.setImageResource(resId);
    }

    public void setup(String label, String description, int iconResource) {
        setLabel(label);
        setDescription(description);
        setIcon(iconResource);
    }

    private static int getInPx(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
