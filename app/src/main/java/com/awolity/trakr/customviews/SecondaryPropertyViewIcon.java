package com.awolity.trakr.customviews;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.awolity.trakr.R;

public class SecondaryPropertyViewIcon extends PrimaryPropertyViewIcon{

    public SecondaryPropertyViewIcon(@NonNull Context context) {
        super(context);
    }

    public SecondaryPropertyViewIcon(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SecondaryPropertyViewIcon(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_secondary_property_icon, this, true);
        labelTextView = findViewById(R.id.tv_label_duration);
        valueTextView = findViewById(R.id.tv_value_duration);
        unitTextView = findViewById(R.id.tv_unit_duration);
        iconImageView = findViewById(R.id.iv_icon_duration);
    }
}
