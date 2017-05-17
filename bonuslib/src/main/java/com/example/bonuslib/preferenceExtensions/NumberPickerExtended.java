package com.example.bonuslib.preferenceExtensions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * Created by ivan on 5/18/17.
 */

public class NumberPickerExtended extends NumberPicker {
    public NumberPickerExtended(Context context) {
        super(context);
    }

    public NumberPickerExtended(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    public NumberPickerExtended(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributeSet(attrs);
    }

    private void processAttributeSet(AttributeSet attrs) {
        // get params from xml
        this.setMinValue(attrs.getAttributeIntValue(null, "minValue", 0));
        this.setMaxValue(attrs.getAttributeIntValue(null, "maxValue", 0));
    }
}
