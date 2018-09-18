package com.awolity.trakrviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

public class PrimaryPropertyView extends ConstraintLayout {

    TextView labelTextView;
    TextView valueTextView;
    TextView unitTextView;
    final Context context;

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

    void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_primary_property, this, true);
        labelTextView = findViewById(R.id.tv_label);
        valueTextView = findViewById(R.id.tv_value);
        unitTextView = findViewById(R.id.tv_unit);
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
