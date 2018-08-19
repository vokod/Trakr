package com.awolity.trakrviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class RadiogroupSetting extends ConstraintLayout {

    private static final String TAG = "RadiogroupSetting";
    private TextView labelTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private RadioButton firstButton;
    private RadioButton secondButton;
    private final Context context;
    private RadiogroupSettingListener listener;

    public RadiogroupSetting(@NonNull Context context) {
        super(context);
        this.context = context;
        inflate();
    }

    public RadiogroupSetting(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate();
    }

    public RadiogroupSetting(@NonNull Context context, @Nullable AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate();
    }

    void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_setting_radiogroup, this, true);
        labelTextView = findViewById(R.id.tv_label);
        descriptionTextView = findViewById(R.id.tv_desc);
        iconImageView = findViewById(R.id.iv_icon);
        firstButton = findViewById(R.id.rb_one);
        secondButton = findViewById(R.id.rb_two);
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

    private void setRadioButtons(String labelFirst, String labelSecond, int selected,
                                 final RadiogroupSettingListener listener) {
        firstButton.setText(labelFirst);
        secondButton.setText(labelSecond);
        this.listener = listener;
        setSelected(selected);

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRadioButtonClicked(0);
            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRadioButtonClicked(1);
            }
        });
    }

    public void setup(String label, String description, int iconResource, String labelFirst,
                      String labelSecond, int selected, RadiogroupSettingListener listener) {
        setLabel(label);
        setDescription(description);
        setIcon(iconResource);
        setRadioButtons(labelFirst, labelSecond, selected, listener);
    }

    public interface RadiogroupSettingListener {
        void OnRadioButtonClicked(int selected);
    }

    public void setSelected(int selected){
        if (selected == 0) {
            firstButton.setChecked(true);
            secondButton.setChecked(false);
        } else if (selected == 1) {
            firstButton.setChecked(false);
            secondButton.setChecked(true);
        }

    }
}
