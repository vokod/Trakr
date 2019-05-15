package com.awolity.trakrviews;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ButtonSetting extends ConstraintLayout {

    private TextView labelTextView;
    private TextView descriptionTextView;
    private ImageView iconImageView;
    private final Context context;
    private FrameLayout clickOverlay;
    private int iconResource;
    private int disabledIconResource;

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

    @SuppressWarnings("WeakerAccess")
    void inflate() {
        LayoutInflater.from(context).inflate(R.layout.view_setting_button, this, true);
        labelTextView = findViewById(R.id.tv_label);
        descriptionTextView = findViewById(R.id.tv_desc);
        iconImageView = findViewById(R.id.iv_icon);
        clickOverlay = findViewById(R.id.fl_click_overlay);
    }

    private void setLabel(String labelText) {
        labelTextView.setText(labelText);
    }

    public void setDescription(String descriptionText) {
        if (descriptionText == null) {
            descriptionTextView.setVisibility(View.GONE);
            labelTextView.setPadding(labelTextView.getPaddingStart(),
                    getInPx(context, 12),
                    labelTextView.getPaddingEnd(),
                    getInPx(context, 12));
            return;
        }
        descriptionTextView.setText(descriptionText);
    }

    private void setIcon(int iconResource, int disabledIconResource) {
        iconImageView.setImageResource(iconResource);
        this.disabledIconResource = disabledIconResource;
        this.iconResource = iconResource;
    }

    public void setup(String label, String description, int iconResource, int disabledIconResource,
                      View.OnClickListener clickListener) {
        setLabel(label);
        setDescription(description);
        setIcon(iconResource, disabledIconResource);
        clickOverlay.setOnClickListener(clickListener);
    }

    private static int getInPx(Context context, @SuppressWarnings("SameParameterValue") int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            labelTextView.setTextColor(context.getResources().getColor(R.color.primaryText));
            descriptionTextView.setTextColor(context.getResources().getColor(R.color.secondaryText));
            clickOverlay.setVisibility(View.VISIBLE);
            iconImageView.setImageResource(iconResource);
        } else {
            descriptionTextView.setTextColor(context.getResources().getColor(R.color.disabledText));
            labelTextView.setTextColor(context.getResources().getColor(R.color.disabledText));
            clickOverlay.setVisibility(View.GONE);
            if (disabledIconResource != 0) {
                iconImageView.setImageResource(disabledIconResource);
            }
        }
    }
}
