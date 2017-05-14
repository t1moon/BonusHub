package com.example.BonusHub.activity.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.bonuslib.preferenceExtensions.NumberPickerPreferenceDialog;
import com.example.bonuslib.preferenceExtensions.NumberPreference;
import com.example.timur.BonusHub.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerSettingsFragment#newInstance} factoryhttp://netology.ru/programs/data-scientist?utm_source=context&utm_medium=1267&utm_campaign=ds-rsya&utm_content=4171435107&utm_term=python%20machine%20learning&yclid=2600899913511344354&stop=1 method to
 * create an instance of this fragment.
 */
public class OwnerSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final static String TAG = "OwnerSettingsFragment";
    private SharedPreferences sp;
    protected final String THIS_FRAGMENT = "android.support.v7.preference.PreferenceFragment.DIALOG";
    protected final int BONUS_SYSTEM_KEY_ADDRESS = R.string.bonus_system_key;
//    protected final CharSequence TYPED_SETTINGS_KEY_ADDRESS = R.string.typed_settings_key;
    protected final int typedSettings[] = {R.xml.xml_nfree_settings, 999, 999};

    public OwnerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // add xml
        addPreferencesFromResource(R.xml.xml_owner_settings);
        // define shared preferences to use
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // register listener in order to change settings appearence depending
        // on bonus system type
        sp.registerOnSharedPreferenceChangeListener(this);
        // find preferences for chosen bonus type
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        // TRY
//        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        // TRY
//        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    // list of actions perfomed depending on a certain preference changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!isAdded()) {
            Log.d(TAG, "onSharedPreferenceChanged: not added to activity");
            return;
        }
        Preference preference = findPreference(key);

        if (getString(BONUS_SYSTEM_KEY_ADDRESS).equals(key)) {
            int typeFromSharedPreferences = Integer.parseInt(sharedPreferences.getString(key, ""));
            if (typeFromSharedPreferences == 0) {
                this.addPreferencesFromResource(R.xml.xml_nfree_settings);
            }
            // TODO
//            this.addPreferencesFromResource(typedSettings);
        }
    }

    // needed for custom dialog preference to work
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof NumberPreference) {
            dialogFragment = new NumberPickerPreferenceDialog();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), THIS_FRAGMENT);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
