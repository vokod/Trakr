package com.awolity.trakr.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.awolity.trakr.R;

public class PrimaryPropertyView extends ConstraintLayout {

    protected TextView labelTextView, valueTextView, unitTextView;
    protected final Context context;

    public PrimaryPropertyView(@NonNull Context context) {
        super(context);
        this.context = context;
        inflate();
    }

    public PrimaryPropertyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate();
    }

    public PrimaryPropertyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate();
    }

    protected void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_primary_property, this, true);
        labelTextView = findViewById(R.id.tvLabel);
        valueTextView = findViewById(R.id.tvValue);
        unitTextView = findViewById(R.id.tvUnit);
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

}
