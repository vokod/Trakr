package com.awolity.trakrviews;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

public class PrimaryPropertyViewIcon extends ConstraintLayout {

    TextView labelTextView;
    TextView valueTextView;
    TextView unitTextView;
    ImageView iconImageView;
    final Context context;

    public PrimaryPropertyViewIcon(@NonNull Context context) {
        super(context);
        this.context = context;
        inflate();
    }

    public PrimaryPropertyViewIcon(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate();
    }

    public PrimaryPropertyViewIcon(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate();
    }

    void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_primary_property_icon, this, true);
        labelTextView = findViewById(R.id.tv_label);
        valueTextView = findViewById(R.id.tv_value);
        unitTextView = findViewById(R.id.tv_unit);
        iconImageView = findViewById(R.id.iv_icon);
    }

    private void setLabel(String labelText) {
        labelTextView.setText(labelText);
    }

    public void setValue(String valueText) {
        valueTextView.setText(valueText);
    }

    @SuppressWarnings("unused")
    public String getValue() {
        return valueTextView.getText().toString();
    }

    private void setUnit(String unitText) {
        unitTextView.setText(unitText);
    }

    private void setIcon(int resId) {
        iconImageView.setImageResource(resId);
    }

    public void setup(String label, String unit, String defaultValue, int iconResource) {
        setLabel(label);
        setUnit(unit);
        setValue(defaultValue);
        setIcon(iconResource);
    }
}
