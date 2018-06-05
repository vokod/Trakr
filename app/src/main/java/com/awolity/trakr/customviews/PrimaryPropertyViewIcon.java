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

public class PrimaryPropertyViewIcon extends ConstraintLayout {

    protected TextView labelTextView, valueTextView, unitTextView;
    protected ImageView iconImageView;
    protected final Context context;

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

    protected void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_primary_property, this, true);
        labelTextView = findViewById(R.id.tvLabel);
        valueTextView = findViewById(R.id.tvValue);
        unitTextView = findViewById(R.id.tvUnit);
        iconImageView = findViewById(R.id.iv_icon);
    }

    public void setLabel(String labelText){
       labelTextView.setText(labelText);
    }

    public void setValue(String valueText){
        valueTextView.setText(valueText);
    }

    public String getValue(){return valueTextView.getText().toString();}

    public void setUnit(String unitText){
        unitTextView.setText(unitText);
    }

    public void setIcon (int resId){
        iconImageView.setImageResource(resId);
    }

    public void setup(String label, String unit, String defaultValue, int iconResource){
        setLabel(label);
        setUnit(unit);
        setValue(defaultValue);
        setIcon(iconResource);
    }

}
