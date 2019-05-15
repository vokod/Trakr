package com.awolity.trakrviews;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

public class ListPropertyViewIcon extends PrimaryPropertyViewIcon{

    public ListPropertyViewIcon(@NonNull Context context) {
        super(context);
    }

    public ListPropertyViewIcon(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPropertyViewIcon(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_list_property_icon, this, true);
        labelTextView = findViewById(R.id.tv_label);
        valueTextView = findViewById(R.id.tv_value);
        unitTextView = findViewById(R.id.tv_unit);
        iconImageView = findViewById(R.id.iv_icon);
    }
}
