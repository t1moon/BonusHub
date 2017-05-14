package com.example.bonuslib.preferenceExtensions;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by ivan on 5/11/17.
 */

public class NumberPreference extends DialogPreference {
    public int value = 0;

    public NumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int value;
        if (restoreValue) {
            value = (defaultValue != null) ? (int) defaultValue : 5;
        } else {
            value = (int) defaultValue;
        }
    }
}
