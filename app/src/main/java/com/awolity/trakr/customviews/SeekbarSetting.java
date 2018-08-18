package com.awolity.trakr.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.awolity.trakr.R;

public class SeekbarSetting extends ConstraintLayout {

    private TextView labelTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private SeekBar seekBar;
    private final Context context;

    public SeekbarSetting(@NonNull Context context) {
        super(context);
        this.context = context;
        inflate();
    }

    public SeekbarSetting(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate();
    }

    public SeekbarSetting(@NonNull Context context, @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate();
    }

    void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_setting_seekbar, this, true);
        labelTextView = findViewById(R.id.tv_label);
        descriptionTextView = findViewById(R.id.tv_desc);
        iconImageView = findViewById(R.id.iv_icon);
        seekBar = findViewById(R.id.seekbar);
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

    private void setSeekBar(int max, int pos, SeekBar.OnSeekBarChangeListener listener) {
        seekBar.setMax(max);
        seekBar.setProgress(pos);
        seekBar.setOnSeekBarChangeListener(listener);
    }

    public void setup(String label, String description, int iconResource, int max, int pos,
                      SeekBar.OnSeekBarChangeListener listener) {
        setLabel(label);
        setDescription(description);
        setIcon(iconResource);
        setSeekBar(max, pos, listener);
    }

    public void setSeekBarPosition(int position){
        seekBar.setProgress(position);
    }
}
