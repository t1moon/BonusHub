package com.example.bonuslib.preferenceExtensions;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by ivan on 5/9/17.
 */

public class NumberPickerPreferenceDialog extends PreferenceDialogFragmentCompat implements android.support.v7.preference.DialogPreference.TargetFragment {

    // declare number params
    protected NumberPicker picker = null;
    private int minValue = 1;
    private int maxValue = 1000;
    private int defaultValue = 6;

    @Override
    protected View onCreateDialogView(Context context)
    {
        picker = new NumberPicker(context);
        return picker;
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        picker.setMaxValue(1000);
        picker.setMinValue(1);
        NumberPreference pref = (NumberPreference) getPreference();
        // TODO
        picker.setValue(5);
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            NumberPreference pref = (NumberPreference) getPreference();
            pref.value = picker.getValue();
            int value = picker.getValue();
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence)
    {
        return getPreference();
    }
}
