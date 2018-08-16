package com.awolity.trakr.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;

public class ButtonSetting extends ConstraintLayout {

    TextView labelTextView;
    TextView descriptionTextView;
    ImageView iconImageView;
    final Context context;

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
        LayoutInflater.from(context).inflate(R.layout.view_button_setting, this, true);
        labelTextView = findViewById(R.id.tv_label);
        descriptionTextView = findViewById(R.id.tv_desc);
        iconImageView = findViewById(R.id.iv_icon);
    }

    private void setLabel(String labelText) {
        labelTextView.setText(labelText);
    }

    public void setDescription(String valueText) {
        descriptionTextView.setText(valueText);
    }

    private void setIcon(int resId) {
        iconImageView.setImageResource(resId);
    }

    public void setup(String label, String description, int iconResource) {
        setLabel(label);
        setDescription(description);
        setIcon(iconResource);
    }
}
