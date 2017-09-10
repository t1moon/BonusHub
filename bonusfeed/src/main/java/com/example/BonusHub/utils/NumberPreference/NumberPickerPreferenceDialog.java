package com.example.BonusHub.utils.NumberPreference;

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;

import com.example.timur.BonusHub.R;

public class NumberPickerPreferenceDialog extends PreferenceDialogFragmentCompat implements android.support.v7.preference.DialogPreference.TargetFragment {
    private final static String ARG_KEY = "key";
    private NumberPickerExtended picker = null;

    public static NumberPickerPreferenceDialog newInstance(String key) {
        final NumberPickerPreferenceDialog fragment = new NumberPickerPreferenceDialog();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker = (NumberPickerExtended) view.findViewById(R.id.number_picker_pref);

        if (picker == null) {
            throw new IllegalStateException("Dialog view must contain a NumberPicker with id" +
                    "number_picker_pref");
        }

        // get the value from sp
        Integer value = null;
        DialogPreference preference = getPreference();
        if (preference instanceof NumberPreference){
            value = ((NumberPreference) preference).getValue();
        }

        // set value on screen
        if (value != null) {
            picker.setValue(value);
        }
    }

    // save value on dialog closed
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (!positiveResult) {
            return;
        }

        int value = picker.getValue();
        DialogPreference preference = getPreference();
        if (preference instanceof NumberPreference) {
            NumberPreference np = (NumberPreference) preference;
            if (np.callChangeListener(value)) {
                np.setValue(value);
            }
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence)
    {
        return getPreference();
    }
}
