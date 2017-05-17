package com.example.bonuslib.preferenceExtensions;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.example.bonuslib.R;

/**
 * Created by ivan on 5/11/17.
 */

public class NumberPreference extends DialogPreference {
    private int value = 0;
    private int dialogResId = R.layout.pref_number_picker;

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public NumberPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }

    public NumberPreference(Context context) {
        this(context, null);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(value); // save to sp
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 1); // 1 is fallback default value; index is from xml
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(value) : (int) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        return dialogResId;
    }
}
